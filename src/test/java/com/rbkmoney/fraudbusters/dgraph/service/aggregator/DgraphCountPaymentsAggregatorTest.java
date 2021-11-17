package com.rbkmoney.fraudbusters.dgraph.service.aggregator;

import com.rbkmoney.fraudbusters.factory.properties.OperationProperties;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudo.payment.aggregator.CountPaymentAggregator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;

import static com.rbkmoney.fraudbusters.util.DgraphTestAggregationUtils.*;
import static com.rbkmoney.fraudbusters.util.DgraphTestAggregationUtils.createDefaultOperationProperties;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@ActiveProfiles("full-prod")
public class DgraphCountPaymentsAggregatorTest extends AbstractDgraphPaymentAggregatorTest {

    @Autowired
    private CountPaymentAggregator<PaymentModel, PaymentCheckedField> dgraphCountAggregator;

    @Test
    public void paymentCountAggregationTest() throws Exception {
        PaymentModel testPaymentModel = createTestPaymentModel();
        prepareGraphDb(createDefaultOperationProperties(testPaymentModel));

        testCountAggregatesByDefaultModel(testPaymentModel);

        OperationProperties operationPropertiesTwo = createDefaultOperationProperties();
        insertPayments(operationPropertiesTwo, 5);

        testCountAggregatesByDefaultModel(testPaymentModel);
        testCountAggregatesByCustomModel(
                operationPropertiesTwo.getTokenId(),
                operationPropertiesTwo.getEmail(),
                operationPropertiesTwo.getFingerprint()
        );
    }

    private void testCountAggregatesByDefaultModel(PaymentModel testPaymentModel) {
        testCountOfPaymentsByToken(testPaymentModel.getCardToken(), StatusEnum.EMPTY, 15);
        testCountOfPaymentsByToken(testPaymentModel.getCardToken(), StatusEnum.CAPTURED, 15);
        testCountOfPaymentsByToken(testPaymentModel.getCardToken(), StatusEnum.FAILED, 0);

        testCountOfPaymentsByEmail(testPaymentModel.getEmail(), StatusEnum.EMPTY, 22);
        testCountOfPaymentsByEmail(testPaymentModel.getEmail(), StatusEnum.CAPTURED, 22);
        testCountOfPaymentsByEmail(testPaymentModel.getEmail(), StatusEnum.FAILED, 0);

        testCountOfPaymentsByFingerprint(testPaymentModel.getFingerprint(), StatusEnum.EMPTY, 22);
        testCountOfPaymentsByFingerprint(testPaymentModel.getFingerprint(), StatusEnum.CAPTURED, 22);
        testCountOfPaymentsByFingerprint(testPaymentModel.getFingerprint(), StatusEnum.FAILED, 0);
    }

    private void testCountAggregatesByCustomModel(String tokenId, String email, String fingerprint) {
        testCountOfPaymentsByToken(tokenId, StatusEnum.EMPTY, 5);
        testCountOfPaymentsByToken(tokenId, StatusEnum.CAPTURED, 5);
        testCountOfPaymentsByToken(tokenId, StatusEnum.FAILED, 0);

        testCountOfPaymentsByEmail(email, StatusEnum.EMPTY, 5);
        testCountOfPaymentsByEmail(email, StatusEnum.CAPTURED, 5);
        testCountOfPaymentsByEmail(email, StatusEnum.FAILED, 0);

        testCountOfPaymentsByFingerprint(fingerprint, StatusEnum.EMPTY, 5);
        testCountOfPaymentsByFingerprint(fingerprint, StatusEnum.CAPTURED, 5);
        testCountOfPaymentsByFingerprint(fingerprint, StatusEnum.FAILED, 0);
    }


    private void testCountOfPaymentsByToken(String tokenId, StatusEnum statusEnum, int expectedCount) {
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setCardToken(tokenId);

        Integer count = testCountOfPayments(PaymentCheckedField.CARD_TOKEN, paymentModel, statusEnum);
        assertEquals(expectedCount, count, "Count of payments by a card token is not equal to expected");
    }

    private void testCountOfPaymentsByEmail(String email, StatusEnum statusEnum, int expectedCount) {
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setEmail(email);

        Integer count = testCountOfPayments(PaymentCheckedField.EMAIL, paymentModel, statusEnum);
        assertEquals(expectedCount, count, "Count of payments by an email is not equal to expected");
    }

    private void testCountOfPaymentsByFingerprint(String fingerprint, StatusEnum statusEnum, int expectedCount) {
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setFingerprint(fingerprint);

        Integer count = testCountOfPayments(PaymentCheckedField.FINGERPRINT, paymentModel, statusEnum);
        assertEquals(expectedCount, count, "Count of payments by a fingerprint is not equal to expected");
    }

    private Integer testCountOfPayments(PaymentCheckedField field, PaymentModel paymentModel, StatusEnum statusEnum) {
        return switch (statusEnum) {
            case CAPTURED -> dgraphCountAggregator.countSuccess(
                    field,
                    paymentModel,
                    createTestTimeWindow(),
                    new ArrayList<>()
            );
            case FAILED -> dgraphCountAggregator.countError(
                    field,
                    paymentModel,
                    createTestTimeWindow(),
                    null,
                    new ArrayList<>()
            );
            default -> dgraphCountAggregator.count(
                    field,
                    paymentModel,
                    createTestTimeWindow(),
                    new ArrayList<>()
            );
        };
    }

}
