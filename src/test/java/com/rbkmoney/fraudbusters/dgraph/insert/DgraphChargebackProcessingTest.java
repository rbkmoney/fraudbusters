package com.rbkmoney.fraudbusters.dgraph.insert;

import com.rbkmoney.damsel.fraudbusters.Chargeback;
import com.rbkmoney.fraudbusters.dgraph.DgraphAbstractIntegrationTest;
import com.rbkmoney.fraudbusters.factory.properties.OperationProperties;
import com.rbkmoney.fraudbusters.serde.ChargebackDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.rbkmoney.fraudbusters.factory.TestDgraphObjectFactory.generateChargeback;

@Slf4j
@ActiveProfiles("full-prod")
public class DgraphChargebackProcessingTest extends DgraphAbstractIntegrationTest {

    private static final String KAFKA_CHARGEBACK_TOPIC = "chargeback_event";

    @Test
    public void processChargebackFromKafkaTest() throws Exception {
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
        producePayments(KAFKA_CHARGEBACK_TOPIC, generateChargebacks(5, operationProperties));
        waitingTopic(KAFKA_CHARGEBACK_TOPIC, ChargebackDeserializer.class);
        checkCountOfObjects("Token", 1);
        checkCountOfObjects("Payment", 1);
        checkCountOfObjects("Chargeback", 5);
        checkCountOfObjects("Email", 1);
        checkCountOfObjects("Fingerprint", 1);
        checkCountOfObjects("IP", 1);
        checkCountOfObjects("Bin", 1);
        checkCountOfObjects("PartyShop", 1);
        checkCountOfObjects("Country", 0);

        producePayments(KAFKA_CHARGEBACK_TOPIC, generateChargebacks(3, operationProperties));
        checkCountOfObjects("Token", 1);
        checkCountOfObjects("Payment", 1);
        checkCountOfObjects("Chargeback", 5);
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
        producePayments(KAFKA_CHARGEBACK_TOPIC, generateChargebacks(6, secondOperationProperties));
        checkCountOfObjects("Token", 2);
        checkCountOfObjects("Payment", 2);
        checkCountOfObjects("Chargeback", 11);
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
        producePayments(KAFKA_CHARGEBACK_TOPIC, generateChargebacks(10, thirdOperationProperties));
        checkCountOfObjects("Token", 3);
        checkCountOfObjects("Payment", 3);
        checkCountOfObjects("Chargeback", 21);
        checkCountOfObjects("Email", 3);
        checkCountOfObjects("Fingerprint", 2);
        checkCountOfObjects("IP", 2);
        checkCountOfObjects("Bin", 2);
        checkCountOfObjects("PartyShop", 3);
        checkCountOfObjects("Country", 0);
    }

    private List<Chargeback> generateChargebacks(int count, OperationProperties properties) {
        List<Chargeback> payments = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            payments.add(generateChargeback(properties, i));
        }
        return payments;
    }

    void producePayments(String topicName, List<Chargeback> chargebacks)
            throws InterruptedException, ExecutionException {
        try (Producer<String, Chargeback> producer = createProducer()) {
            for (Chargeback chargeback : chargebacks) {
                ProducerRecord<String, Chargeback> producerRecord =
                        new ProducerRecord<>(topicName, chargeback.getId(), chargeback);
                producer.send(producerRecord).get();
            }
        }
    }

}
