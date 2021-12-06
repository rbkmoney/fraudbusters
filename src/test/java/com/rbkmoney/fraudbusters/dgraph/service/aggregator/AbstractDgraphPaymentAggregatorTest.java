package com.rbkmoney.fraudbusters.dgraph.service.aggregator;

import com.rbkmoney.damsel.fraudbusters.Payment;
import com.rbkmoney.fraudbusters.dgraph.DgraphAbstractIntegrationTest;
import com.rbkmoney.fraudbusters.factory.properties.OperationProperties;
import com.rbkmoney.fraudbusters.serde.PaymentDeserializer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.rbkmoney.fraudbusters.factory.TestDgraphObjectFactory.generatePayments;

public abstract class AbstractDgraphPaymentAggregatorTest extends DgraphAbstractIntegrationTest {

    private static final String KAFKA_PAYMENT_TOPIC = "payment_event";

    void prepareGraphDb(OperationProperties properties) throws Exception {
        producePayments(KAFKA_PAYMENT_TOPIC, generatePayments(5, properties));
        waitingTopic(KAFKA_PAYMENT_TOPIC, PaymentDeserializer.class);

        properties.setShopId(properties.getShopId() + "-2");
        producePayments(KAFKA_PAYMENT_TOPIC, generatePayments(5, properties));

        properties.setBin(properties.getBin() + "-2");
        properties.setIp(properties.getIp() + "-2");
        producePayments(KAFKA_PAYMENT_TOPIC, generatePayments(5, properties));

        properties.setTokenId(properties.getTokenId() + "-2");
        properties.setShopId(properties.getShopId() + "-3");
        producePayments(KAFKA_PAYMENT_TOPIC, generatePayments(7, properties));

        long currentTimeMillis = System.currentTimeMillis();
        while (getCountOfObjects("Payment") < 22
                && System.currentTimeMillis() - currentTimeMillis < 30_000L) {
        }
    }

    void insertPayments(OperationProperties properties, int count) throws Exception {
        int treshold = getCountOfObjects("Payment") + count;
        producePayments(KAFKA_PAYMENT_TOPIC, generatePayments(count, properties));
        long currentTimeMillis = System.currentTimeMillis();
        while (getCountOfObjects("Payment") < treshold
                && System.currentTimeMillis() - currentTimeMillis < 30_000L) {
        }
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

    enum StatusEnum {

        EMPTY,
        CAPTURED,
        FAILED

    }

}
