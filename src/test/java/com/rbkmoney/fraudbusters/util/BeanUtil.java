package com.rbkmoney.fraudbusters.util;

import com.rbkmoney.damsel.base.Content;
import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.damsel.p2p_insp.Identity;
import com.rbkmoney.damsel.p2p_insp.Raw;
import com.rbkmoney.damsel.p2p_insp.Transfer;
import com.rbkmoney.damsel.p2p_insp.TransferInfo;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.damsel.proxy_inspector.InvoicePayment;
import com.rbkmoney.damsel.proxy_inspector.Party;
import com.rbkmoney.damsel.proxy_inspector.Shop;
import com.rbkmoney.damsel.proxy_inspector.*;
import com.rbkmoney.fraudbusters.constant.ClickhouseUtilsValue;
import com.rbkmoney.fraudbusters.domain.Payment;
import com.rbkmoney.fraudbusters.domain.TimeProperties;
import com.rbkmoney.fraudbusters.fraud.model.P2PModel;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.kafka.common.serialization.ThriftSerializer;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.msgpack.Value;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BeanUtil {

    public static final String FINGERPRINT = "fingerprint";
    public static final String SHOP_ID = "shopId";
    public static final String PARTY_ID = "partyId";
    public static final String IP = "ip";
    public static final String EMAIL = "email";
    public static final String BIN = "666";
    public static final String SUFIX = "_2";
    public static final Long AMOUNT_SECOND = 1000L;
    public static final Long AMOUNT_FIRST = 10500L;
    public static final String P_ID = "pId";
    public static final String ID_VALUE_SHOP = "2035728";
    public static final String BIN_COUNTRY_CODE = "RUS";

    public static final String SOURCE_ID = "source_id";
    public static final String SOURCE_NS = "source_ns";

    public static final String PAYMENT_ID = "1";
    public static final String TEST_MAIL_RU = "test@mail.ru";
    public static final String TRANSFER_ID = "transferId";
    public static final String IDENTITY_ID = "identityId";
    public static final String RUB = "RUB";

    public static Context createContext() {
        String pId = P_ID;
        return createContext(pId);
    }

    public static com.rbkmoney.damsel.p2p_insp.Context createP2PContext(String identityId, String idTransfer) {
        ContactInfo contact_info = new ContactInfo();
        contact_info.setEmail(EMAIL);
        TransferInfo transferInfo = new TransferInfo(
                new Transfer()
                        .setCost(new Cash(
                                9000L,
                                new CurrencyRef("RUB")
                        ))
                        .setSender(com.rbkmoney.damsel.p2p_insp.Payer.raw(
                                new Raw(com.rbkmoney.damsel.domain.Payer.customer(
                                        new CustomerPayer("custId", "1", "rec_paym_tool", createBankCard(),
                                                contact_info)))))
                        .setCreatedAt(TypeUtil.temporalToString(Instant.now()))
                        .setReceiver(com.rbkmoney.damsel.p2p_insp.Payer.raw(
                                new Raw(com.rbkmoney.damsel.domain.Payer.customer(
                                        new CustomerPayer("custId_2", "2", "rec_paym_tool", createBankCard(),
                                                contact_info)))))
                        .setIdentity(new Identity(identityId))
                        .setId(idTransfer)
        );
        return new com.rbkmoney.damsel.p2p_insp.Context(
                transferInfo
        );

    }

    public static Context createContext(String pId) {
        ContactInfo contact_info = new ContactInfo();
        contact_info.setEmail(EMAIL);
        PaymentInfo payment = new PaymentInfo(
                new Shop(ID_VALUE_SHOP,
                        new Category("pizza", "no category"),
                        new ShopDetails("pizza-sushi"),
                        new ShopLocation() {{
                            setUrl("http://www.pizza-sushi.com/");
                        }}
                ),
                new InvoicePayment(pId,
                        TypeUtil.temporalToString(Instant.now()),
                        Payer.customer(
                                new CustomerPayer("custId", "1", "rec_paym_tool", createBankCard(),
                                        contact_info)),
                        new Cash(
                                9000L,
                                new CurrencyRef("RUB")
                        )),
                new com.rbkmoney.damsel.proxy_inspector.Invoice(
                        "iId",
                        TypeUtil.temporalToString(Instant.now()),
                        "",
                        new InvoiceDetails("drugs guns murder")),
                new Party(pId)
        );
        return new Context(
                payment
        );

    }

    private static Payer createCustomerPayer() {
        return Payer.customer(new CustomerPayer("custId", "1", "rec_paym_tool", createBankCard(), new ContactInfo()));
    }

    private static PaymentTool createBankCard() {
        return new PaymentTool() {{
            BankCard value = new BankCard(
                    "477bba133c182267fe5f086924abdc5db71f77bfc27f01f2843f2cdc69d89f05",
                    BankCardPaymentSystem.mastercard,
                    BIN,
                    "4242"
            );
            value.setIssuerCountry(Residence.RUS);
            setBankCard(value);
        }};
    }

    public static PaymentModel createPaymentModel() {
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setFingerprint(FINGERPRINT);
        paymentModel.setShopId(SHOP_ID);
        paymentModel.setPartyId(PARTY_ID);
        paymentModel.setIp(IP);
        paymentModel.setEmail(EMAIL);
        paymentModel.setBin(BIN);
        paymentModel.setAmount(AMOUNT_FIRST);
        paymentModel.setBinCountryCode(BIN_COUNTRY_CODE);
        return paymentModel;
    }

    public static P2PModel createP2PModel() {
        P2PModel p2PModel = new P2PModel();
        p2PModel.setFingerprint(FINGERPRINT);
        p2PModel.setCurrency(RUB);
        p2PModel.setTransferId(TRANSFER_ID);
        p2PModel.setIdentityId(IDENTITY_ID);
        p2PModel.setIp(IP);
        p2PModel.setEmail(EMAIL);
        p2PModel.setTimestamp(Instant.now().toEpochMilli());

        com.rbkmoney.fraudbusters.fraud.model.Payer sender = new com.rbkmoney.fraudbusters.fraud.model.Payer();

        sender.setBin(BIN);
        sender.setBinCountryCode(BIN_COUNTRY_CODE);

        p2PModel.setSender(sender);
        p2PModel.setAmount(AMOUNT_FIRST);

        com.rbkmoney.fraudbusters.fraud.model.Payer receiver = new com.rbkmoney.fraudbusters.fraud.model.Payer();
        receiver.setBin(BIN);
        receiver.setBinCountryCode(BIN_COUNTRY_CODE);

        p2PModel.setReceiver(receiver);
        return p2PModel;
    }

    public static P2PModel createP2PModelSecond() {
        P2PModel p2PModel = new P2PModel();
        p2PModel.setFingerprint(FINGERPRINT + SUFIX);
        p2PModel.setTransferId(TRANSFER_ID + SUFIX);
        p2PModel.setIdentityId(IDENTITY_ID + SUFIX);
        p2PModel.setIp(IP + SUFIX);
        p2PModel.setEmail(EMAIL + SUFIX);
        p2PModel.setTimestamp(Instant.now().toEpochMilli());

        com.rbkmoney.fraudbusters.fraud.model.Payer sender = new com.rbkmoney.fraudbusters.fraud.model.Payer();

        sender.setBin(BIN + SUFIX);
        sender.setBinCountryCode(BIN_COUNTRY_CODE + SUFIX);

        p2PModel.setSender(sender);
        p2PModel.setCurrency(RUB);
        p2PModel.setAmount(AMOUNT_SECOND);

        com.rbkmoney.fraudbusters.fraud.model.Payer receiver = new com.rbkmoney.fraudbusters.fraud.model.Payer();
        receiver.setBin(BIN + SUFIX);
        receiver.setBinCountryCode(BIN_COUNTRY_CODE + SUFIX);

        p2PModel.setReceiver(receiver);
        return p2PModel;
    }

    public static PaymentModel createFraudModelSecond() {
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setFingerprint(FINGERPRINT + SUFIX);
        paymentModel.setShopId(SHOP_ID + SUFIX);
        paymentModel.setPartyId(PARTY_ID + SUFIX);
        paymentModel.setIp(IP + SUFIX);
        paymentModel.setEmail(EMAIL + SUFIX);
        paymentModel.setBin(BIN + SUFIX);
        paymentModel.setAmount(AMOUNT_SECOND);
        paymentModel.setBinCountryCode(BIN_COUNTRY_CODE);
        return paymentModel;
    }

    @NotNull
    public static Command createGroupCommand(String localId, List<PriorityId> priorityIds) {
        Command command = new Command();
        Group group = new Group();
        group.setGroupId(localId);
        group.setTemplateIds(priorityIds);
        command.setCommandBody(CommandBody.group(group));
        command.setCommandType(com.rbkmoney.damsel.fraudbusters.CommandType.CREATE);
        return command;
    }

    @NotNull
    public static Command deleteGroupCommand(String localId, List<PriorityId> priorityIds) {
        Command command = new Command();
        Group group = new Group();
        group.setGroupId(localId);
        group.setTemplateIds(priorityIds);
        command.setCommandBody(CommandBody.group(group));
        command.setCommandType(CommandType.DELETE);
        return command;
    }

    @NotNull
    public static Command createGroupReferenceCommand(String party, String shopId, String idGroup) {
        Command command = new Command();
        command.setCommandType(CommandType.CREATE);
        command.setCommandBody(CommandBody.group_reference(new GroupReference()
                .setGroupId(idGroup)
                .setPartyId(party)
                .setShopId(shopId)));
        return command;
    }

    @NotNull
    public static Command createP2PGroupReferenceCommand(String identityId, String idGroup) {
        Command command = new Command();
        command.setCommandType(CommandType.CREATE);
        command.setCommandBody(CommandBody.p2p_group_reference(new P2PGroupReference()
                .setGroupId(idGroup)
                .setIdentityId(identityId))
        );
        return command;
    }

    @NotNull
    public static Command createP2PTemplateReferenceCommand(String identityId, String templateId) {
        Command command = new Command();
        command.setCommandType(CommandType.CREATE);
        command.setCommandBody(CommandBody.p2p_reference(new P2PReference()
                .setTemplateId(templateId)
                .setIdentityId(identityId))
        );
        return command;
    }

    @NotNull
    public static Command createDeleteGroupReferenceCommand(String party, String shopId, String idGroup) {
        Command command = new Command();
        command.setCommandType(CommandType.DELETE);
        command.setCommandBody(CommandBody.group_reference(new GroupReference()
                .setGroupId(idGroup)
                .setPartyId(party)
                .setShopId(shopId)));
        return command;
    }

    public static MachineEvent createMessageCreateInvoice(String sourceId) {
        InvoiceCreated invoiceCreated = createInvoiceCreate(sourceId);
        InvoiceChange invoiceChange = new InvoiceChange();
        invoiceChange.setInvoiceCreated(invoiceCreated);
        return createMachineEvent(invoiceChange, sourceId);
    }

    public static MachineEvent createMessageInvoiceCaptured(String sourceId) {
        InvoiceChange invoiceCaptured = createInvoiceCaptured();
        return createMachineEvent(invoiceCaptured, sourceId);
    }

    public static MachineEvent createMessagePaymentStared(String sourceId) {
        InvoiceChange invoiceCaptured = createPaymentStarted();
        return createMachineEvent(invoiceCaptured, sourceId);
    }

    @NotNull
    public static MachineEvent createMachineEvent(InvoiceChange invoiceChange, String sourceId) {
        MachineEvent message = new MachineEvent();
        EventPayload payload = new EventPayload();
        ArrayList<InvoiceChange> invoiceChanges = new ArrayList<>();
        invoiceChanges.add(invoiceChange);
        payload.setInvoiceChanges(invoiceChanges);

        message.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        message.setEventId(1L);
        message.setSourceNs(SOURCE_NS);
        message.setSourceId(sourceId);

        ThriftSerializer<EventPayload> eventPayloadThriftSerializer = new ThriftSerializer<>();
        Value data = new Value();
        data.setBin(eventPayloadThriftSerializer.serialize("", payload));
        message.setData(data);
        return message;
    }

    @NotNull
    public static InvoiceCreated createInvoiceCreate(String sourceId) {
        com.rbkmoney.damsel.domain.Invoice invoice = new com.rbkmoney.damsel.domain.Invoice();

        invoice.setId(sourceId);
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
    public static InvoiceChange createInvoiceCaptured() {
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
    public static InvoiceChange createPaymentStarted() {
        InvoiceChange invoiceChange = new InvoiceChange();
        InvoicePaymentChange invoicePaymentChange = new InvoicePaymentChange();
        InvoicePaymentChangePayload invoicePaymentChangePayload = new InvoicePaymentChangePayload();
        invoicePaymentChange.setId(PAYMENT_ID);
        InvoicePaymentStarted payload = new InvoicePaymentStarted();
        com.rbkmoney.damsel.domain.InvoicePayment payment = new com.rbkmoney.damsel.domain.InvoicePayment();
        Cash cost = new Cash();
        cost.setAmount(123L);
        cost.setCurrency(new CurrencyRef().setSymbolicCode("RUB"));
        payment.setCost(cost);
        payment.setCreatedAt("2016-08-10T16:07:18Z");
        payment.setId(PAYMENT_ID);
        payment.setStatus(InvoicePaymentStatus.processed(new InvoicePaymentProcessed()));
        Payer payer = createCustomerPayer();
        PaymentResourcePayer payerResource = new PaymentResourcePayer();
        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setEmail(TEST_MAIL_RU);
        DisposablePaymentResource resource = new DisposablePaymentResource();
        ClientInfo clientInfo = createClientInfo();
        resource.setClientInfo(clientInfo);
        resource.setPaymentTool(createBankCard());
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
    public static ClientInfo createClientInfo() {
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setFingerprint("finger");
        clientInfo.setIpAddress("123.123.123.123");
        return clientInfo;
    }

    @NotNull
    public static Payment convertContextToPayment(Context context, String status) {
        Payment payment = new Payment();
        TimeProperties timeProperties = TimestampUtil.generateTimeProperties();
        payment.setTimestamp(timeProperties.getTimestamp());
        payment.setEventTime(timeProperties.getEventTime());
        payment.setEventTimeHour(timeProperties.getEventTimeHour());
        Cash cost = context.getPayment().getPayment().getCost();
        payment.setAmount(cost.getAmount());
        payment.setCurrency(cost.getCurrency().getSymbolicCode());
        payment.setStatus(status);
        Payer payer = context.getPayment().getPayment().getPayer();
        PayerFieldExtractor.getBankCard(payer)
                .ifPresent(bankCard -> {
                    payment.setBin(bankCard.getBin());
                    payment.setBankCountry(bankCard.isSetIssuerCountry() ? bankCard.getIssuerCountry().name() : ClickhouseUtilsValue.UNKNOWN);
                    payment.setCardToken(bankCard.getToken());
                });
        PayerFieldExtractor.getClientInfo(payer)
                .ifPresent(clientInfo -> {
                    payment.setFingerprint(clientInfo.getFingerprint());
                    payment.setIp(clientInfo.getIpAddress());
                });
        PayerFieldExtractor.getContactInfo(payer)
                .ifPresent(contactInfo -> {
                    payment.setEmail(contactInfo.getEmail());
                });
        payment.setPartyId(context.getPayment().getParty().getPartyId());
        payment.setShopId(context.getPayment().getShop().getId());
        return payment;
    }
}