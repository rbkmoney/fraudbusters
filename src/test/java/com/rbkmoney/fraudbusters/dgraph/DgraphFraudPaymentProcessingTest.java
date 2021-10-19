package com.rbkmoney.fraudbusters.dgraph;

import com.rbkmoney.damsel.fraudbusters.FraudPayment;
import com.rbkmoney.fraudbusters.serde.FraudPaymentDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.rbkmoney.fraudbusters.factory.TestDgraphObjectFactory.createTestFraudPayment;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@ActiveProfiles("full-prod")
public class DgraphFraudPaymentProcessingTest extends DgraphAbstractIntegrationTest {

    private static final String KAFKA_PAYMENT_TOPIC = "fraud_payment";
    private static final DateTimeFormatter FORMATTER =  DateTimeFormatter.ofPattern("yyyy-MM-dd[ HH:mm:ss]");

    @Test
    public void processPaymentFromKafkaTest() throws Exception {
        List<FraudPayment> fraudPayments = generatePayments(5);
        producePayments(KAFKA_PAYMENT_TOPIC, fraudPayments);
        waitingTopic(KAFKA_PAYMENT_TOPIC, FraudPaymentDeserializer.class);

        Thread.sleep(15000L);
        assertEquals(5, getCountOfObjects("FraudPayment"));

        producePayments(KAFKA_PAYMENT_TOPIC, fraudPayments);
        Thread.sleep(15000L);
        assertEquals(5, getCountOfObjects("FraudPayment"));

        producePayments(KAFKA_PAYMENT_TOPIC, generatePayments(5));
        Thread.sleep(15000L);
        assertEquals(10, getCountOfObjects("FraudPayment"));
    }

    private List<FraudPayment> generatePayments(int count) {
        List<FraudPayment> payments = new ArrayList<>();
        for (int i = 0; i < count; i++) {

            String createdAt = LocalDateTime.now().format(FORMATTER);
            payments.add(createTestFraudPayment("pay-" + createdAt + "-" + i, createdAt));
        }
        return payments;
    }

    void producePayments(String topicName, List<FraudPayment> payments)
            throws InterruptedException, ExecutionException {
        try (Producer<String, FraudPayment> producer = createProducer()) {
            for (FraudPayment payment : payments) {
                ProducerRecord<String, FraudPayment> producerRecord =
                        new ProducerRecord<>(topicName, payment.getId(), payment);
                producer.send(producerRecord).get();
            }
        }
    }

}
