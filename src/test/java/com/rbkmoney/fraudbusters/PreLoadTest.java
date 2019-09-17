package com.rbkmoney.fraudbusters;

import com.rbkmoney.damsel.base.Content;
import com.rbkmoney.damsel.domain.Invoice;
import com.rbkmoney.damsel.domain.InvoicePayment;
import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.CommandBody;
import com.rbkmoney.damsel.fraudbusters.Template;
import com.rbkmoney.damsel.fraudbusters.TemplateReference;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.damsel.proxy_inspector.Context;
import com.rbkmoney.damsel.proxy_inspector.InspectorProxySrv;
import com.rbkmoney.fraudbusters.config.KafkaConfig;
import com.rbkmoney.fraudbusters.constant.ResultStatus;
import com.rbkmoney.fraudbusters.constant.TemplateLevel;
import com.rbkmoney.fraudbusters.domain.MgEventSinkRow;
import com.rbkmoney.fraudbusters.repository.EventRepository;
import com.rbkmoney.fraudbusters.serde.CommandDeserializer;
import com.rbkmoney.fraudbusters.serde.MgEventSinkRowDeserializer;
import com.rbkmoney.fraudbusters.stream.aggregate.EventSinkAggregationStreamFactoryImpl;
import com.rbkmoney.fraudbusters.stream.aggregate.handler.MgEventSinkHandler;
import com.rbkmoney.fraudbusters.util.BeanUtil;
import com.rbkmoney.kafka.common.serialization.ThriftSerializer;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import com.rbkmoney.machinegun.msgpack.Value;
import com.rbkmoney.woody.thrift.impl.http.THClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.thrift.TException;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;


@Slf4j
@ContextConfiguration(classes = {KafkaConfig.class, EventSinkAggregationStreamFactoryImpl.class, MgEventSinkHandler.class},
        initializers = PreLoadTest.Initializer.class)
public class PreLoadTest extends KafkaAbstractTest {

    private static final String TEMPLATE = "rule: 12 >= 1\n" +
            " -> accept;";
    private static final String TEST = "test";
    public static final String PAYMENT_ID = "1";
    public static final String TEST_MAIL_RU = "test@mail.ru";
    public static final String BIN = "1234";
    public static final String SHOP_ID = "shopId";

    private InspectorProxySrv.Iface client;

    @MockBean
    EventRepository eventRepository;

    @Autowired
    EventSinkAggregationStreamFactoryImpl eventSinkAggregationStreamFactory;

    @Autowired
    Properties eventSinkStreamProperties;

    @LocalServerPort
    int serverPort;

    private static String SERVICE_URL = "http://localhost:%s/fraud_inspector/v1";

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues
                    .of("kafka.bootstrap.servers=" + kafka.getBootstrapServers())
                    .applyTo(configurableApplicationContext.getEnvironment());
            try {
                createTemplate();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        private static String createTemplate() throws InterruptedException, ExecutionException {
            Producer<String, Command> producer = createProducer();
            Command command = new Command();
            Template template = new Template();
            String id = TEST;
            template.setId(id);
            template.setTemplate(PreLoadTest.TEMPLATE.getBytes());
            command.setCommandBody(CommandBody.template(template));
            command.setCommandType(com.rbkmoney.damsel.fraudbusters.CommandType.CREATE);
            ProducerRecord<String, Command> producerRecord = new ProducerRecord<>("template",
                    id, command);
            producer.send(producerRecord).get();
            producer.close();

            Consumer<String, Object> consumer = createConsumer(CommandDeserializer.class);
            consumer.subscribe(List.of("template"));

            recurPolling(consumer);

            consumer.close();
            return id;
        }
    }

    @Before
    public void init() throws ExecutionException, InterruptedException {
        createGlobalReferenceToTemplate(TEST);
    }

    private void createGlobalReferenceToTemplate(String id) throws InterruptedException, ExecutionException {
        Producer<String, Command> producer;
        Command command;
        ProducerRecord<String, Command> producerRecord;

        producer = createProducer();
        command = new Command();
        TemplateReference value = new TemplateReference();
        value.setIsGlobal(true);
        value.setTemplateId(id);
        command.setCommandBody(CommandBody.reference(value));
        command.setCommandType(com.rbkmoney.damsel.fraudbusters.CommandType.CREATE);
        producerRecord = new ProducerRecord<>(referenceTopic,
                TemplateLevel.GLOBAL.name(), command);
        producer.send(producerRecord).get();
        producer.close();

        Consumer<String, Object> consumer = createConsumer(CommandDeserializer.class);
        consumer.subscribe(List.of(referenceTopic));

        recurPolling(consumer);

        consumer.close();
    }

    @Test
    public void inspectPaymentTest() throws URISyntaxException, TException, ExecutionException, InterruptedException {
        Thread.sleep(4000L);

        THClientBuilder clientBuilder = new THClientBuilder()
                .withAddress(new URI(String.format(SERVICE_URL, serverPort)))
                .withNetworkTimeout(300000);
        client = clientBuilder.build(InspectorProxySrv.Iface.class);
        createGlobalReferenceToTemplate(TEST);

        Context context = BeanUtil.createContext();
        RiskScore riskScore = client.inspectPayment(context);

        Assert.assertEquals(RiskScore.low, riskScore);
    }

    @Test
    public void aggregateStreamTest() throws ExecutionException, InterruptedException {
        produceMessageToEventSink(createMessageCreateInvoice());
        produceMessageToEventSink(createMessagePaymentStared());
        produceMessageToEventSink(createMessageInvoiceCaptured());

        KafkaStreams kafkaStreams = eventSinkAggregationStreamFactory.create(eventSinkStreamProperties);

        Consumer<String, MgEventSinkRow> consumer = createConsumer(MgEventSinkRowDeserializer.class);
        consumer.subscribe(List.of(aggregatedEventSink));

        ConsumerRecords<String, MgEventSinkRow> poll = pollWithWaitingTimeout(consumer, Duration.ofSeconds(6L));

        Assert.assertFalse(poll.isEmpty());
        Iterator<ConsumerRecord<String, MgEventSinkRow>> iterator = poll.iterator();
        ConsumerRecord<String, MgEventSinkRow> record = iterator.next();
        MgEventSinkRow value = record.value();
        Assert.assertEquals(ResultStatus.CAPTURED.name(), value.getResultStatus());
        Assert.assertEquals(BIN, value.getBin());

        kafkaStreams.close();
    }

    @NotNull
    private ConsumerRecords<String, MgEventSinkRow> pollWithWaitingTimeout(Consumer<String, MgEventSinkRow> consumer, Duration duration) {
        long startTime = System.currentTimeMillis();
        ConsumerRecords<String, MgEventSinkRow> poll = consumer.poll(Duration.ofSeconds(5L));
        while (poll.isEmpty()) {
            if (System.currentTimeMillis() - startTime > duration.toMillis()) {
                throw new RuntimeException("Timeout error in pollWithWaitingTimeout!");
            }
            poll = consumer.poll(Duration.ofSeconds(5L));
        }
        return poll;
    }

    private void produceMessageToEventSink(MachineEvent machineEvent) throws InterruptedException, ExecutionException {
        ProducerRecord<String, SinkEvent> producerRecord;
        Producer<String, SinkEvent> producer = createProducerAggr();
        SinkEvent sinkEvent = new SinkEvent();
        sinkEvent.setEvent(machineEvent);
        producerRecord = new ProducerRecord<>(eventSinkTopic,
                sinkEvent.getEvent().getSourceId(), sinkEvent);
        producer.send(producerRecord).get();
        producer.close();
    }

    public static final String SOURCE_ID = "source_id";
    public static final String SOURCE_NS = "source_ns";

    private MachineEvent createMessageCreateInvoice() {
        InvoiceCreated invoiceCreated = createInvoiceCreate();
        InvoiceChange invoiceChange = new InvoiceChange();
        invoiceChange.setInvoiceCreated(invoiceCreated);
        return createMachineEvent(invoiceChange);
    }

    private MachineEvent createMessageInvoiceCaptured() {
        InvoiceChange invoiceCaptured = createInvoiceCaptured();
        return createMachineEvent(invoiceCaptured);
    }

    private MachineEvent createMessagePaymentStared() {
        InvoiceChange invoiceCaptured = createPaymentStarted();
        return createMachineEvent(invoiceCaptured);
    }

    @NotNull
    private MachineEvent createMachineEvent(InvoiceChange invoiceChange) {
        MachineEvent message = new MachineEvent();
        EventPayload payload = new EventPayload();
        ArrayList<InvoiceChange> invoiceChanges = new ArrayList<>();
        invoiceChanges.add(invoiceChange);
        payload.setInvoiceChanges(invoiceChanges);

        message.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        message.setEventId(1L);
        message.setSourceNs(SOURCE_NS);
        message.setSourceId(SOURCE_ID);

        ThriftSerializer<EventPayload> eventPayloadThriftSerializer = new ThriftSerializer<>();
        Value data = new Value();
        data.setBin(eventPayloadThriftSerializer.serialize("", payload));
        message.setData(data);
        return message;
    }

    @NotNull
    private InvoiceCreated createInvoiceCreate() {
        Invoice invoice = new Invoice();

        invoice.setId(SOURCE_ID);
        invoice.setOwnerId("owner_id");
        invoice.setShopId(SHOP_ID);
        invoice.setCreatedAt("2016-08-10T16:07:18Z");
        invoice.setStatus(InvoiceStatus.unpaid(new InvoiceUnpaid()));
        invoice.setDue("2016-08-10T16:07:23Z");
        invoice.setCost(new Cash(12L, new CurrencyRef("RUB")));
        invoice.setDetails(new InvoiceDetails("product"));

        InvoiceCreated invoiceCreated = new InvoiceCreated();
        invoiceCreated.setInvoice(invoice);

        Content content = new Content();
        content.setType("contentType");
        content.setData("test".getBytes());
        invoice.setContext(content);
        return invoiceCreated;
    }

    @NotNull
    private InvoiceChange createInvoiceCaptured() {
        InvoiceChange invoiceChange = new InvoiceChange();
        InvoicePaymentChange invoicePaymentChange = new InvoicePaymentChange();
        invoicePaymentChange.setId("1");
        InvoicePaymentChangePayload payload = new InvoicePaymentChangePayload();
        InvoicePaymentStatusChanged invoicePaymentStatusChanged = new InvoicePaymentStatusChanged();
        invoicePaymentStatusChanged.setStatus(InvoicePaymentStatus.captured(new InvoicePaymentCaptured()));
        payload.setInvoicePaymentStatusChanged(invoicePaymentStatusChanged);
        invoicePaymentChange.setPayload(payload);
        invoiceChange.setInvoicePaymentChange(invoicePaymentChange);
        return invoiceChange;
    }

    @NotNull
    private InvoiceChange createPaymentStarted() {
        InvoiceChange invoiceChange = new InvoiceChange();
        InvoicePaymentChange invoicePaymentChange = new InvoicePaymentChange();
        InvoicePaymentChangePayload invoicePaymentChangePayload = new InvoicePaymentChangePayload();
        invoicePaymentChange.setId(PAYMENT_ID);
        InvoicePaymentStarted payload = new InvoicePaymentStarted();
        InvoicePayment payment = new InvoicePayment();
        Cash cost = new Cash();
        cost.setAmount(123L);
        cost.setCurrency(new CurrencyRef().setSymbolicCode("RUB"));
        payment.setCost(cost);
        payment.setCreatedAt("2016-08-10T16:07:18Z");
        payment.setId(PAYMENT_ID);
        payment.setStatus(InvoicePaymentStatus.processed(new InvoicePaymentProcessed()));
        Payer payer = new Payer();
        CustomerPayer value = createCustomerPayer();
        payer.setCustomer(value);
        PaymentResourcePayer payerResource = new PaymentResourcePayer();
        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setEmail(TEST_MAIL_RU);
        DisposablePaymentResource resource = new DisposablePaymentResource();
        ClientInfo clientInfo = createClientInfo();
        resource.setClientInfo(clientInfo);
        resource.setPaymentTool(PaymentTool.bank_card(createBankCard()));
        payerResource.setResource(resource);
        payerResource.setContactInfo(contactInfo);
        payer.setPaymentResource(payerResource);
        payment.setPayer(payer);
        InvoicePaymentFlow flow = new InvoicePaymentFlow();
        InvoicePaymentFlowHold invoicePaymentFlowHold = new InvoicePaymentFlowHold();
        invoicePaymentFlowHold.setOnHoldExpiration(OnHoldExpiration.capture);
        invoicePaymentFlowHold.setHeldUntil("werwer");

        flow.setHold(invoicePaymentFlowHold);

        payment.setFlow(flow);
        payload.setPayment(payment);

        invoicePaymentChangePayload.setInvoicePaymentStarted(payload);
        invoicePaymentChange.setPayload(invoicePaymentChangePayload);
        invoiceChange.setInvoicePaymentChange(invoicePaymentChange);
        return invoiceChange;
    }

    @NotNull
    private ClientInfo createClientInfo() {
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setFingerprint("finger");
        clientInfo.setIpAddress("123.123.123.123");
        return clientInfo;
    }

    @NotNull
    private CustomerPayer createCustomerPayer() {
        CustomerPayer value = new CustomerPayer();
        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setEmail(TEST_MAIL_RU);
        value.setContactInfo(contactInfo);
        BankCard bankCard = createBankCard();

        value.setPaymentTool(PaymentTool.bank_card(bankCard));
        return value;
    }

    @NotNull
    private BankCard createBankCard() {
        BankCard bankCard = new BankCard();
        bankCard.setBankName("bank_name");
        bankCard.setBin(BIN);
        bankCard.setMaskedPan("***123");
        bankCard.setIssuerCountry(Residence.RUS);
        bankCard.setPaymentSystem(BankCardPaymentSystem.mastercard);
        bankCard.setToken("token");
        return bankCard;
    }
}