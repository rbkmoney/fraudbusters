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
        checkCountOfObjects("Token", 1);
        checkCountOfObjects("Payment", 1);
        checkCountOfObjects("Refund", 5);
        checkCountOfObjects("Email", 1);
        checkCountOfObjects("Fingerprint", 1);
        checkCountOfObjects("IP", 1);
        checkCountOfObjects("Bin", 1);
        checkCountOfObjects("PartyShop", 1);
        checkCountOfObjects("Country", 0);

        producePayments(KAFKA_REFUND_TOPIC, generateRefunds(3, operationProperties));
        checkCountOfObjects("Token", 1);
        checkCountOfObjects("Payment", 1);
        checkCountOfObjects("Refund", 5);
        checkCountOfObjects("Email", 1);
        checkCountOfObjects("Fingerprint", 1);
        checkCountOfObjects("IP", 1);
        checkCountOfObjects("Bin", 1);
        checkCountOfObjects("PartyShop", 1);
        checkCountOfObjects("Country", 0);

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
        checkCountOfObjects("Token", 2);
        checkCountOfObjects("Payment", 2);
        checkCountOfObjects("Refund", 11);
        checkCountOfObjects("Email", 2);
        checkCountOfObjects("Fingerprint", 1);
        checkCountOfObjects("IP", 1);
        checkCountOfObjects("Bin", 1);
        checkCountOfObjects("PartyShop", 2);
        checkCountOfObjects("Country", 0);

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
        checkCountOfObjects("Token", 3);
        checkCountOfObjects("Payment", 3);
        checkCountOfObjects("Refund", 21);
        checkCountOfObjects("Email", 3);
        checkCountOfObjects("Fingerprint", 2);
        checkCountOfObjects("IP", 2);
        checkCountOfObjects("Bin", 2);
        checkCountOfObjects("PartyShop", 3);
        checkCountOfObjects("Country", 0);
    }

    private List<Refund> generateRefunds(int count, OperationProperties properties) {
        List<Refund> refunds = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            refunds.add(generateRefund(properties, i));
        }
        return refunds;
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
