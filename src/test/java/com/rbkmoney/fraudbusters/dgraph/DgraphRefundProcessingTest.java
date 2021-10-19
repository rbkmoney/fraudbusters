package com.rbkmoney.fraudbusters.dgraph;

import com.rbkmoney.damsel.fraudbusters.Refund;
import com.rbkmoney.fraudbusters.factory.properties.OperationProperties;
import com.rbkmoney.fraudbusters.serde.RefundDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.rbkmoney.fraudbusters.factory.TestDgraphObjectFactory.generateRefund;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@ActiveProfiles("full-prod")
public class DgraphRefundProcessingTest extends DgraphAbstractIntegrationTest {

    private static final String KAFKA_REFUND_TOPIC = "refund_event";

    @Test
    public void processRefundFromKafkaTest() throws Exception {
        OperationProperties operationProperties = OperationProperties.builder()
                .tokenId("token1")
                .email("email1")
                .fingerprint("finger1")
                .partyId("party1")
                .shopId("shop1")
                .bin("bin1")
                .ip("ip1")
                .country("Russia")
                .paymentId("Payment-" + Instant.now().toEpochMilli())
                .build();
        producePayments(KAFKA_REFUND_TOPIC, generateRefunds(5, operationProperties));
        waitingTopic(KAFKA_REFUND_TOPIC, RefundDeserializer.class);

        Thread.sleep(15000L);
        assertEquals(1, getCountOfObjects("Token"));
        assertEquals(1, getCountOfObjects("Payment"));
        assertEquals(5, getCountOfObjects("Refund"));
        assertEquals(1, getCountOfObjects("Email"));
        assertEquals(1, getCountOfObjects("Fingerprint"));
        assertEquals(1, getCountOfObjects("IP"));
        assertEquals(1, getCountOfObjects("Bin"));
        assertEquals(1, getCountOfObjects("PartyShop"));
        assertEquals(0, getCountOfObjects("Country"));

        producePayments(KAFKA_REFUND_TOPIC, generateRefunds(3, operationProperties));
        Thread.sleep(15000L);

        assertEquals(1, getCountOfObjects("Token"));
        assertEquals(1, getCountOfObjects("Payment"));
        assertEquals(5, getCountOfObjects("Refund"));
        assertEquals(1, getCountOfObjects("Email"));
        assertEquals(1, getCountOfObjects("Fingerprint"));
        assertEquals(1, getCountOfObjects("IP"));
        assertEquals(1, getCountOfObjects("Bin"));
        assertEquals(1, getCountOfObjects("PartyShop"));
        assertEquals(0, getCountOfObjects("Country"));

        OperationProperties secondOperationProperties = OperationProperties.builder()
                .tokenId("token2")
                .email("email2")
                .fingerprint("finger1")
                .partyId("party1")
                .shopId("shop2")
                .bin("bin1")
                .ip("ip1")
                .country("Russia")
                .paymentId("Payment-" + Instant.now().toEpochMilli())
                .build();
        producePayments(KAFKA_REFUND_TOPIC, generateRefunds(6, secondOperationProperties));
        Thread.sleep(15000L);

        assertEquals(2, getCountOfObjects("Token"));
        assertEquals(2, getCountOfObjects("Payment"));
        assertEquals(11, getCountOfObjects("Refund"));
        assertEquals(2, getCountOfObjects("Email"));
        assertEquals(1, getCountOfObjects("Fingerprint"));
        assertEquals(1, getCountOfObjects("IP"));
        assertEquals(1, getCountOfObjects("Bin"));
        assertEquals(2, getCountOfObjects("PartyShop"));
        assertEquals(0, getCountOfObjects("Country"));

        OperationProperties thirdOperationProperties = OperationProperties.builder()
                .tokenId("token3")
                .email("email3")
                .fingerprint("finger3")
                .partyId("party3")
                .shopId("shop3")
                .bin("bin3")
                .ip("ip3")
                .country("BeloRussia")
                .paymentId("Payment-" + Instant.now().toEpochMilli())
                .build();
        producePayments(KAFKA_REFUND_TOPIC, generateRefunds(10, thirdOperationProperties));
        Thread.sleep(15000L);

        assertEquals(3, getCountOfObjects("Token"));
        assertEquals(3, getCountOfObjects("Payment"));
        assertEquals(21, getCountOfObjects("Refund"));
        assertEquals(3, getCountOfObjects("Email"));
        assertEquals(2, getCountOfObjects("Fingerprint"));
        assertEquals(2, getCountOfObjects("IP"));
        assertEquals(2, getCountOfObjects("Bin"));
        assertEquals(3, getCountOfObjects("PartyShop"));
        assertEquals(0, getCountOfObjects("Country"));
    }

    private List<Refund> generateRefunds(int count, OperationProperties properties) {
        List<Refund> payments = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            payments.add(generateRefund(properties, i));
        }
        return payments;
    }

    void producePayments(String topicName, List<Refund> refunds)
            throws InterruptedException, ExecutionException {
        try (Producer<String, Refund> producer = createProducer()) {
            for (Refund refund : refunds) {
                ProducerRecord<String, Refund> producerRecord =
                        new ProducerRecord<>(topicName, refund.getId(), refund);
                producer.send(producerRecord).get();
            }
        }
    }

}
