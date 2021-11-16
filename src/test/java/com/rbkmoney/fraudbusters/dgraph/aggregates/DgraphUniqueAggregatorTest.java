package com.rbkmoney.fraudbusters.dgraph.aggregates;

import com.rbkmoney.damsel.fraudbusters.Payment;
import com.rbkmoney.fraudbusters.dgraph.DgraphAbstractIntegrationTest;
import com.rbkmoney.fraudbusters.factory.properties.OperationProperties;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.serde.PaymentDeserializer;
import com.rbkmoney.fraudo.aggregator.UniqueValueAggregator;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.rbkmoney.fraudbusters.factory.TestDgraphObjectFactory.generatePayment;
import static com.rbkmoney.fraudbusters.util.DgraphTestAggregationUtils.createTestPaymentModel;
import static com.rbkmoney.fraudbusters.util.DgraphTestAggregationUtils.createTestTimeWindow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@ActiveProfiles("full-prod")
public class DgraphUniqueAggregatorTest extends DgraphAbstractIntegrationTest {

    @Autowired
    private UniqueValueAggregator<PaymentModel, PaymentCheckedField> dgraphUniqueAggregator;

    private static final String KAFKA_PAYMENT_TOPIC = "payment_event";

    @Test
    public void paymentCountAggregationTest() throws Exception {
        PaymentModel testPaymentModel = createTestPaymentModel();
        OperationProperties operationProperties = OperationProperties.builder()
                .tokenId(testPaymentModel.getCardToken())
                .maskedPan(testPaymentModel.getPan())
                .email(testPaymentModel.getEmail())
                .fingerprint(testPaymentModel.getFingerprint())
                .partyId(testPaymentModel.getPartyId())
                .shopId(testPaymentModel.getShopId())
                .bin(testPaymentModel.getBin())
                .ip(testPaymentModel.getIp())
                .country(testPaymentModel.getBinCountryCode())
                .eventTimeDispersion(true)
                .build();
        prepareGraphDb(operationProperties);

        testUniqCardTokensByEmail(testPaymentModel, 2);
        testUniqCardTokensByFingerprint(testPaymentModel, 2);
        testUniqCardTokensByShop(testPaymentModel, 1);

        System.out.println("qwe  ");
    }

    private void testUniqCardTokensByEmail(PaymentModel testPaymentModel, int expectedCount) {
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setCardToken(testPaymentModel.getCardToken());
        paymentModel.setEmail(testPaymentModel.getEmail());

        Integer uniqCardTokensByEmail = dgraphUniqueAggregator.countUniqueValue(
                PaymentCheckedField.EMAIL,
                paymentModel,
                PaymentCheckedField.CARD_TOKEN,
                createTestTimeWindow(),
                new ArrayList<>()
        );
        assertEquals(expectedCount, uniqCardTokensByEmail,
                "Count of unique card tokens for the email is not equal to expected");
    }

    private void testUniqCardTokensByFingerprint(PaymentModel testPaymentModel, int expectedCount) {
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setCardToken(testPaymentModel.getCardToken());
        paymentModel.setFingerprint(testPaymentModel.getFingerprint());

        Integer uniqCardTokensByFingerprint = dgraphUniqueAggregator.countUniqueValue(
                PaymentCheckedField.FINGERPRINT,
                paymentModel,
                PaymentCheckedField.CARD_TOKEN,
                createTestTimeWindow(),
                new ArrayList<>()
        );
        assertEquals(expectedCount, uniqCardTokensByFingerprint,
                "Count of unique card tokens for the fingerprint is not equal to expected");
    }

    private void testUniqCardTokensByShop(PaymentModel testPaymentModel, int expectedCount) {
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setCardToken(testPaymentModel.getCardToken());
        paymentModel.setShopId(testPaymentModel.getShopId());

        Integer uniqCardTokensByFingerprint = dgraphUniqueAggregator.countUniqueValue(
                PaymentCheckedField.SHOP_ID,
                paymentModel,
                PaymentCheckedField.CARD_TOKEN,
                createTestTimeWindow(),
                new ArrayList<>()
        );
        assertEquals(expectedCount, uniqCardTokensByFingerprint,
                "Count of unique card tokens for the fingerprint is not equal to expected");
    }

    private void prepareGraphDb(OperationProperties properties) throws Exception {
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
        while (getCountOfObjects("Payment") < 17
                || System.currentTimeMillis() - currentTimeMillis < 10_000L) {
        }
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
