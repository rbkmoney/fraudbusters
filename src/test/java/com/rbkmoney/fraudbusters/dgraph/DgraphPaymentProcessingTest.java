package com.rbkmoney.fraudbusters.dgraph;

import com.rbkmoney.damsel.fraudbusters.Payment;
import com.rbkmoney.fraudbusters.factory.properties.PaymentProperties;
import com.rbkmoney.fraudbusters.serde.PaymentDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.rbkmoney.fraudbusters.factory.TestDgraphObjectFactory.generatePayment;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@ActiveProfiles("full-prod")
public class DgraphPaymentProcessingTest extends DgraphAbstractIntegrationTest {

    private static final String KAFKA_PAYMENT_TOPIC = "payment_event";

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
        waitingTopic(KAFKA_PAYMENT_TOPIC, PaymentDeserializer.class);

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

}
