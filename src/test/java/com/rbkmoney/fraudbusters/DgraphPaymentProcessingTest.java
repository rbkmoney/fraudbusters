package com.rbkmoney.fraudbusters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.damsel.fraudbusters.ClientInfo;
import com.rbkmoney.damsel.geo_ip.GeoIpServiceSrv;
import com.rbkmoney.damsel.wb_list.WbListServiceSrv;
import com.rbkmoney.fraudbusters.exception.DgraphException;
import com.rbkmoney.fraudbusters.listener.events.dgraph.DgraphPaymentEventListener;
import com.rbkmoney.fraudbusters.repository.clickhouse.impl.PaymentRepositoryImpl;
import com.rbkmoney.fraudbusters.service.CardPoolManagementService;
import com.rbkmoney.fraudbusters.service.ShopManagementService;
import io.dgraph.DgraphClient;
import io.dgraph.DgraphProto;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@ActiveProfiles("full-prod")
public class DgraphPaymentProcessingTest extends DgraphAbstractIntegrationTest {

    private static final String KAFKA_PAYMENT_TOPIC = "payment_event";

    @Autowired
    private ObjectMapper dgraphObjectMapper;

    @Autowired
    private DgraphClient dgraphClient;

    @Autowired
    private DgraphPaymentEventListener dgraphPaymentEventListener;

    @MockBean
    private GeoIpServiceSrv.Iface geoIpServiceSrv;

    @MockBean
    private WbListServiceSrv.Iface wbListServiceSrv;

    @MockBean
    private ShopManagementService shopManagementService;

    @MockBean
    private CardPoolManagementService cardPoolManagementService;

    @MockBean
    private PaymentRepositoryImpl paymentRepository;

    @Test
    public void processPaymentFromKafkaTest() throws Exception {
        PaymentProperties paymentProperties = PaymentProperties.builder()
                 .tokenId("token1")
                 .email("email1")
                 .fingerprint("finger1")
                 .partyId("party1")
                 .shopId("shop1")
                 .bin("bin1")
                 .ip("ip1")
                 .country("Russia")
                 .build();
        producePayments(KAFKA_PAYMENT_TOPIC, generatePayments(5, paymentProperties));
        waitingTopic(KAFKA_PAYMENT_TOPIC);

        Thread.sleep(15000L);
        assertEquals(1, getCountOfObjects("Token"));
        assertEquals(5, getCountOfObjects("Payment"));
        assertEquals(1, getCountOfObjects("Email"));
        assertEquals(1, getCountOfObjects("Fingerprint"));
        assertEquals(1, getCountOfObjects("IP"));
        assertEquals(1, getCountOfObjects("Bin"));
        assertEquals(1, getCountOfObjects("PartyShop"));
        assertEquals(1, getCountOfObjects("Country"));

        producePayments(KAFKA_PAYMENT_TOPIC, generatePayments(3, paymentProperties));
        Thread.sleep(15000L);

        assertEquals(1, getCountOfObjects("Token"));
        assertEquals(8, getCountOfObjects("Payment"));
        assertEquals(1, getCountOfObjects("Email"));
        assertEquals(1, getCountOfObjects("Fingerprint"));
        assertEquals(1, getCountOfObjects("IP"));
        assertEquals(1, getCountOfObjects("Bin"));
        assertEquals(1, getCountOfObjects("PartyShop"));
        assertEquals(1, getCountOfObjects("Country"));

        PaymentProperties secondPaymentProperties = PaymentProperties.builder()
                .tokenId("token2")
                .email("email2")
                .fingerprint("finger1")
                .partyId("party1")
                .shopId("shop2")
                .bin("bin1")
                .ip("ip1")
                .country("Russia")
                .build();
        producePayments(KAFKA_PAYMENT_TOPIC, generatePayments(6, secondPaymentProperties));
        Thread.sleep(15000L);

        assertEquals(2, getCountOfObjects("Token"));
        assertEquals(14, getCountOfObjects("Payment"));
        assertEquals(2, getCountOfObjects("Email"));
        assertEquals(1, getCountOfObjects("Fingerprint"));
        assertEquals(1, getCountOfObjects("IP"));
        assertEquals(1, getCountOfObjects("Bin"));
        assertEquals(2, getCountOfObjects("PartyShop"));
        assertEquals(1, getCountOfObjects("Country"));

        PaymentProperties thirdPaymentProperties = PaymentProperties.builder()
                .tokenId("token3")
                .email("email3")
                .fingerprint("finger3")
                .partyId("party3")
                .shopId("shop3")
                .bin("bin3")
                .ip("ip3")
                .country("BeloRussia")
                .build();
        producePayments(KAFKA_PAYMENT_TOPIC, generatePayments(10, thirdPaymentProperties));
        Thread.sleep(15000L);

        assertEquals(3, getCountOfObjects("Token"));
        assertEquals(24, getCountOfObjects("Payment"));
        assertEquals(3, getCountOfObjects("Email"));
        assertEquals(2, getCountOfObjects("Fingerprint"));
        assertEquals(2, getCountOfObjects("IP"));
        assertEquals(2, getCountOfObjects("Bin"));
        assertEquals(3, getCountOfObjects("PartyShop"));
        assertEquals(2, getCountOfObjects("Country"));
    }

    private List<Payment> generatePayments(int count, PaymentProperties properties) {
        List<Payment> payments = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            payments.add(generatePayment(properties, i));
        }
        return payments;
    }

    private Payment generatePayment(PaymentProperties properties, int idx) {
        Payment payment = new Payment();
        payment.setId("Pay-" + Instant.now().toEpochMilli() + "-" + idx);
        ReferenceInfo referenceInfo = new ReferenceInfo();
        referenceInfo.setMerchantInfo(
                new MerchantInfo()
                        .setPartyId(properties.getPartyId())
                        .setShopId(properties.getShopId())
        );
        payment.setReferenceInfo(referenceInfo);
        payment.setEventTime(Instant.now().toString());
        payment.setCost(
                new Cash()
                        .setAmount(1000L)
                        .setCurrency(new CurrencyRef().setSymbolicCode("RUB"))
        );
        payment.setStatus(PaymentStatus.captured);
        payment.setPaymentTool(PaymentTool.bank_card(
                new BankCard()
                        .setToken(properties.getTokenId())
                        .setBin(properties.getBin())
                        .setLastDigits("0000")
                        .setPaymentToken(new BankCardTokenServiceRef().setId("PT-111"))
                        .setPaymentSystem(new PaymentSystemRef().setId("PS-111"))
                ));
        payment.setProviderInfo(
                new ProviderInfo()
                        .setProviderId("Provider-1")
                        .setTerminalId("Terminal-001")
                        .setCountry(properties.getCountry())
        );
        payment.setMobile(false);
        payment.setRecurrent(false);
        payment.setError(null);
        payment.setClientInfo(
                new ClientInfo()
                        .setEmail(properties.getEmail())
                        .setFingerprint(properties.getFingerprint())
                        .setIp(properties.getIp())
        );
        return payment;
    }

    public int getCountOfObjects(String objectName) {
        String query = String.format("""
                query all() {
                    aggregates(func: type(%s)) {
                        count(uid)
                    }
                }
                """, objectName);

        return getAggregates(query).getCount();
    }

    protected Aggregates getAggregates(String query) {
        DgraphProto.Response response = processDgraphQuery(query);
        String responseJson = response.getJson().toStringUtf8();
        log.debug("Received json with aggregates (query: {}, vars: {}, period: {}): {}",
                query, responseJson);
        TestQuery testQuery = convertToObject(responseJson, TestQuery.class);
        return testQuery == null || testQuery.getAggregates() == null || testQuery.getAggregates().isEmpty()
                ? new Aggregates() : testQuery.getAggregates().get(0);
    }

    protected <T> T convertToObject(String json, Class<T> clazz) {
        try {
            return dgraphObjectMapper.readValue(json, clazz);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("Received an exception when method was converting json to object", ex);
        }
    }

    protected DgraphProto.Response processDgraphQuery(String query) {
        try {
            return dgraphClient.newTransaction().query(query);
        } catch (RuntimeException ex) {
            throw new DgraphException(String.format("Received exception from dgraph while the service " +
                    "process ro query with args (query: %s)", query), ex);
        }
    }

    void producePayments(String topicName, List<Payment> payments)
            throws InterruptedException, ExecutionException {
        try (Producer<String, Payment> producer = createPaymentProducer()) {
            for (Payment payment : payments) {
                ProducerRecord<String, Payment> producerRecord =
                        new ProducerRecord<>(topicName, payment.getId(), payment);
                producer.send(producerRecord).get();
            }
        }
    }

    protected void waitingTopic(String topicName) {
        try (Consumer<String, Payment> consumer = createConsumer()) {
            consumer.subscribe(List.of(topicName));
            Unreliables.retryUntilTrue(240, TimeUnit.SECONDS, () -> {
                ConsumerRecords<String, Payment> records = consumer.poll(Duration.ofSeconds(1L));
                return !records.isEmpty();
            });
        }
    }

    @Getter
    @Setter
    @Builder
    static class PaymentProperties {

        private String tokenId;
        private String email;
        private String fingerprint;
        private String partyId;
        private String shopId;
        private String country;
        private String bin;
        private String ip;

    }

    @Data
    static class TestQuery {

        private List<Aggregates> aggregates;

    }

    @Data
    static class Aggregates {

        private int count;
        private long sum;

    }

}
