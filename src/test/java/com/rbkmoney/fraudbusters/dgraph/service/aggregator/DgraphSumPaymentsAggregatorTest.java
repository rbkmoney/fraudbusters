package com.rbkmoney.fraudbusters.dgraph.service.aggregator;

import com.rbkmoney.fraudbusters.factory.properties.OperationProperties;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudo.payment.aggregator.SumPaymentAggregator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;

import static com.rbkmoney.fraudbusters.util.DgraphTestAggregationUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@ActiveProfiles("full-prod")
public class DgraphSumPaymentsAggregatorTest extends AbstractDgraphPaymentAggregatorTest {

    @Autowired
    private SumPaymentAggregator<PaymentModel, PaymentCheckedField> dgraphSumAggregator;

    @Test
    public void paymentSumAggregationTest() throws Exception {
        PaymentModel testPaymentModel = createTestPaymentModel();
        prepareGraphDb(createDefaultOperationProperties(testPaymentModel));

        testSumAggregatesByDefaultModel(testPaymentModel);

        OperationProperties operationPropertiesTwo = createDefaultOperationProperties();
        insertPayments(operationPropertiesTwo, 5);

        testSumAggregatesByDefaultModel(testPaymentModel);
        testSumAggregatesByCustomModel(
                operationPropertiesTwo.getTokenId(),
                operationPropertiesTwo.getEmail(),
                operationPropertiesTwo.getFingerprint()
        );
    }

    private void testSumAggregatesByDefaultModel(PaymentModel testPaymentModel) {
        testSumOfPaymentsByToken(testPaymentModel.getCardToken(), StatusEnum.EMPTY, 15000);
        testSumOfPaymentsByToken(testPaymentModel.getCardToken(), StatusEnum.CAPTURED, 15000);
        testSumOfPaymentsByToken(testPaymentModel.getCardToken(), StatusEnum.FAILED, 0);

        testSumOfPaymentsByEmail(testPaymentModel.getEmail(), StatusEnum.EMPTY, 22000);
        testSumOfPaymentsByEmail(testPaymentModel.getEmail(), StatusEnum.CAPTURED, 22000);
        testSumOfPaymentsByEmail(testPaymentModel.getEmail(), StatusEnum.FAILED, 0);

        testSumOfPaymentsByFingerprint(testPaymentModel.getFingerprint(), StatusEnum.EMPTY, 22000);
        testSumOfPaymentsByFingerprint(testPaymentModel.getFingerprint(), StatusEnum.CAPTURED, 22000);
        testSumOfPaymentsByFingerprint(testPaymentModel.getFingerprint(), StatusEnum.FAILED, 0);
    }

    private void testSumAggregatesByCustomModel(String tokenId, String email, String fingerprint) {
        testSumOfPaymentsByToken(tokenId, StatusEnum.EMPTY, 5000);
        testSumOfPaymentsByToken(tokenId, StatusEnum.CAPTURED, 5000);
        testSumOfPaymentsByToken(tokenId, StatusEnum.FAILED, 0);

        testSumOfPaymentsByEmail(email, StatusEnum.EMPTY, 5000);
        testSumOfPaymentsByEmail(email, StatusEnum.CAPTURED, 5000);
        testSumOfPaymentsByEmail(email, StatusEnum.FAILED, 0);

        testSumOfPaymentsByFingerprint(fingerprint, StatusEnum.EMPTY, 5000);
        testSumOfPaymentsByFingerprint(fingerprint, StatusEnum.CAPTURED, 5000);
        testSumOfPaymentsByFingerprint(fingerprint, StatusEnum.FAILED, 0);
    }

    private void testSumOfPaymentsByToken(String tokenId, StatusEnum statusEnum, double expectedSum) {
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setCardToken(tokenId);

        Double sum = testSumOfPayments(PaymentCheckedField.CARD_TOKEN, paymentModel, statusEnum);
        assertEquals(expectedSum, sum, "Sum of payments by a card token is not equal to expected");
    }

    private void testSumOfPaymentsByEmail(String email, StatusEnum statusEnum, double expectedSum) {
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setEmail(email);

        Double sum = testSumOfPayments(PaymentCheckedField.EMAIL, paymentModel, statusEnum);
        assertEquals(expectedSum, sum, "Sum of payments by an email is not equal to expected");
    }

    private void testSumOfPaymentsByFingerprint(String fingerprint, StatusEnum statusEnum, double expectedSum) {
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setFingerprint(fingerprint);

        Double sum = testSumOfPayments(PaymentCheckedField.FINGERPRINT, paymentModel, statusEnum);
        assertEquals(expectedSum, sum, "Sum of payments by a fingerprint is not equal to expected");
    }

    private Double testSumOfPayments(PaymentCheckedField field, PaymentModel paymentModel, StatusEnum statusEnum) {
        return switch (statusEnum) {
            case CAPTURED -> dgraphSumAggregator.sumSuccess(
                    field,
                    paymentModel,
                    createTestTimeWindow(),
                    new ArrayList<>()
            );
            case FAILED -> dgraphSumAggregator.sumError(
                    field,
                    paymentModel,
                    createTestTimeWindow(),
                    null,
                    new ArrayList<>()
            );
            default -> dgraphSumAggregator.sum(
                    field,
                    paymentModel,
                    createTestTimeWindow(),
                    new ArrayList<>()
            );
        };
    }

}
