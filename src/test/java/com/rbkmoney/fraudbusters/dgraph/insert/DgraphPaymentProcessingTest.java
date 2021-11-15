package com.rbkmoney.fraudbusters.dgraph.insert;

import com.rbkmoney.damsel.fraudbusters.Payment;
import com.rbkmoney.fraudbusters.dgraph.DgraphAbstractIntegrationTest;
import com.rbkmoney.fraudbusters.factory.properties.OperationProperties;
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

@Slf4j
@ActiveProfiles("full-prod")
public class DgraphPaymentProcessingTest extends DgraphAbstractIntegrationTest {

    private static final String KAFKA_PAYMENT_TOPIC = "payment_event";

    @Test
    public void processPaymentFromKafkaTest() throws Exception {
        OperationProperties operationProperties = OperationProperties.builder()
                 .tokenId("token1")
                 .email("email1")
                 .fingerprint("finger1")
                 .partyId("party1")
                 .shopId("shop1")
                 .bin("bin1")
                 .ip("ip1")
                 .country("Russia")
                 .maskedPan("0101")
                 .build();
        producePayments(KAFKA_PAYMENT_TOPIC, generatePayments(5, operationProperties));
        waitingTopic(KAFKA_PAYMENT_TOPIC, PaymentDeserializer.class);
        checkCountOfObjects("Token", 1);
        checkCountOfObjects("Payment", 5);
        checkCountOfObjects("Email", 1);
        checkCountOfObjects("Fingerprint", 1);
        checkCountOfObjects("IP", 1);
        checkCountOfObjects("Bin", 1);
        checkCountOfObjects("Party", 1);
        checkCountOfObjects("Shop", 1);
        checkCountOfObjects("Country", 1);

        producePayments(KAFKA_PAYMENT_TOPIC, generatePayments(3, operationProperties));
        checkCountOfObjects("Token", 1);
        checkCountOfObjects("Payment", 8);
        checkCountOfObjects("Email", 1);
        checkCountOfObjects("Fingerprint", 1);
        checkCountOfObjects("IP", 1);
        checkCountOfObjects("Bin", 1);
        checkCountOfObjects("Party", 1);
        checkCountOfObjects("Shop", 1);
        checkCountOfObjects("Country", 1);

        OperationProperties secondOperationProperties = OperationProperties.builder()
                .tokenId("token2")
                .email("email2")
                .fingerprint("finger1")
                .partyId("party1")
                .shopId("shop2")
                .bin("bin1")
                .ip("ip1")
                .country("Russia")
                .maskedPan("0101")
                .build();
        producePayments(KAFKA_PAYMENT_TOPIC, generatePayments(6, secondOperationProperties));
        checkCountOfObjects("Token", 2);
        checkCountOfObjects("Payment", 14);
        checkCountOfObjects("Email", 2);
        checkCountOfObjects("Fingerprint", 1);
        checkCountOfObjects("IP", 1);
        checkCountOfObjects("Bin", 1);
        checkCountOfObjects("Party", 1);
        checkCountOfObjects("Shop", 2);
        checkCountOfObjects("Country", 1);

        OperationProperties thirdOperationProperties = OperationProperties.builder()
                .tokenId("token3")
                .email("email3")
                .fingerprint("finger3")
                .partyId("party3")
                .shopId("shop3")
                .bin("bin3")
                .ip("ip3")
                .country("BeloRussia")
                .maskedPan("0101")
                .build();
        producePayments(KAFKA_PAYMENT_TOPIC, generatePayments(10, thirdOperationProperties));
        checkCountOfObjects("Token", 3);
        checkCountOfObjects("Payment", 24);
        checkCountOfObjects("Email", 3);
        checkCountOfObjects("Fingerprint", 2);
        checkCountOfObjects("IP", 2);
        checkCountOfObjects("Bin", 2);
        checkCountOfObjects("Party", 2);
        checkCountOfObjects("Shop", 3);
        checkCountOfObjects("Country", 2);
    }

    private List<Payment> generatePayments(int count, OperationProperties properties) {
        List<Payment> payments = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            payments.add(generatePayment(properties, i));
        }
        return payments;
    }

    void producePayments(String topicName, List<Payment> payments)
            throws InterruptedException, ExecutionException {
        try (Producer<String, Payment> producer = createProducer()) {
            for (Payment payment : payments) {
                ProducerRecord<String, Payment> producerRecord =
                        new ProducerRecord<>(topicName, payment.getId(), payment);
                producer.send(producerRecord).get();
            }
        }
    }

}
