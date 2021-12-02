package com.rbkmoney.fraudbusters.dgraph.service.aggregator;

import com.rbkmoney.fraudbusters.factory.properties.OperationProperties;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudo.aggregator.UniqueValueAggregator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;

import static com.rbkmoney.fraudbusters.util.DgraphTestAggregationUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@ActiveProfiles("full-prod")
public class DgraphUniqueAggregatorTest extends AbstractDgraphPaymentAggregatorTest {

    @Autowired
    private UniqueValueAggregator<PaymentModel, PaymentCheckedField> dgraphUniqueAggregator;

    @Test
    public void getCountOfUniqueTest() throws Exception {
        PaymentModel testPaymentModel = createTestPaymentModel();
        prepareGraphDb(createDefaultOperationProperties(testPaymentModel));

        testUniqueAggregatesByDefaultModel(testPaymentModel);

        OperationProperties operationPropertiesTwo = createDefaultOperationProperties();
        insertPayments(operationPropertiesTwo, 5);

        testUniqueAggregatesByDefaultModel(testPaymentModel);
        testUniqueAggregatesByCustomData(operationPropertiesTwo.getEmail(), operationPropertiesTwo.getPartyId());
    }

    private void testUniqueAggregatesByDefaultModel(PaymentModel testPaymentModel) {
        testUniqCardTokensByEmail(testPaymentModel.getEmail(), 3);
        testUniqCardTokensByFingerprint(testPaymentModel.getFingerprint(), 3);
        testUniqCardTokensByShop(testPaymentModel.getShopId(), 2);
        testUniqCardTokensByIp(testPaymentModel.getIp(), 2);

        testUniqFingerprintsByEmail(testPaymentModel.getEmail(), 2);
        testUniqFingerprintsByIp(testPaymentModel.getIp(), 2);
        testUniqFingerprintsByShop(testPaymentModel.getShopId(), 2);
        testUniqFingerprintsByCardToken(testPaymentModel.getCardToken(), 2);

        testUniqEmailsByFingerprint(testPaymentModel.getFingerprint(), 2);
        testUniqEmailsByIp(testPaymentModel.getIp(), 2);
        testUniqEmailsByShop(testPaymentModel.getShopId(), 2);
        testUniqEmailsByCardToken(testPaymentModel.getCardToken(), 2);

        testUniqShopsByParty(testPaymentModel.getPartyId(), 4);
    }

    private void testUniqueAggregatesByCustomData(String email, String partyId) {
        testUniqCardTokensByEmail(email, 2);
        testUniqShopsByParty(partyId, 2);
    }

    private void testUniqCardTokensByEmail(String email, int expectedCount) {
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setEmail(email);

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

    private void testUniqCardTokensByFingerprint(String fingerprint, int expectedCount) {
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setFingerprint(fingerprint);

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

    private void testUniqCardTokensByShop(String shop, int expectedCount) {
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setShopId(shop);

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

    private void testUniqCardTokensByMobile(boolean isMobile, int expectedCount) {
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setMobile(isMobile);

        Integer uniqCardTokensByFingerprint = dgraphUniqueAggregator.countUniqueValue(
                PaymentCheckedField.MOBILE,
                paymentModel,
                PaymentCheckedField.CARD_TOKEN,
                createTestTimeWindow(),
                new ArrayList<>()
        );
        assertEquals(expectedCount, uniqCardTokensByFingerprint,
                "Count of unique card tokens for the mobile parameter is not equal to expected");
    }

    private void testUniqCardTokensByIp(String ip, int expectedCount) {
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setIp(ip);

        Integer uniqCardTokensByFingerprint = dgraphUniqueAggregator.countUniqueValue(
                PaymentCheckedField.COUNTRY_IP,
                paymentModel,
                PaymentCheckedField.CARD_TOKEN,
                createTestTimeWindow(),
                new ArrayList<>()
        );
        assertEquals(expectedCount, uniqCardTokensByFingerprint,
                "Count of unique card tokens for the ip is not equal to expected");
    }

    private void testUniqFingerprintsByEmail(String email, int expectedCount) {
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setEmail(email);

        Integer uniqCardTokensByFingerprint = dgraphUniqueAggregator.countUniqueValue(
                PaymentCheckedField.EMAIL,
                paymentModel,
                PaymentCheckedField.FINGERPRINT,
                createTestTimeWindow(),
                new ArrayList<>()
        );
        assertEquals(expectedCount, uniqCardTokensByFingerprint,
                "Count of unique fingerprints for the email is not equal to expected");
    }

    private void testUniqFingerprintsByIp(String ip, int expectedCount) {
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setIp(ip);

        Integer uniqCardTokensByFingerprint = dgraphUniqueAggregator.countUniqueValue(
                PaymentCheckedField.COUNTRY_IP,
                paymentModel,
                PaymentCheckedField.FINGERPRINT,
                createTestTimeWindow(),
                new ArrayList<>()
        );
        assertEquals(expectedCount, uniqCardTokensByFingerprint,
                "Count of unique fingerprints for the ip is not equal to expected");
    }

    private void testUniqFingerprintsByShop(String shopId, int expectedCount) {
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setShopId(shopId);

        Integer uniqCardTokensByFingerprint = dgraphUniqueAggregator.countUniqueValue(
                PaymentCheckedField.SHOP_ID,
                paymentModel,
                PaymentCheckedField.FINGERPRINT,
                createTestTimeWindow(),
                new ArrayList<>()
        );
        assertEquals(expectedCount, uniqCardTokensByFingerprint,
                "Count of unique fingerprints for the shop is not equal to expected");
    }

    private void testUniqFingerprintsByCardToken(String tokenId, int expectedCount) {
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setCardToken(tokenId);

        Integer uniqCardTokensByFingerprint = dgraphUniqueAggregator.countUniqueValue(
                PaymentCheckedField.CARD_TOKEN,
                paymentModel,
                PaymentCheckedField.FINGERPRINT,
                createTestTimeWindow(),
                new ArrayList<>()
        );
        assertEquals(expectedCount, uniqCardTokensByFingerprint,
                "Count of unique fingerprints for the card token is not equal to expected");
    }

    private void testUniqEmailsByFingerprint(String fingerprint, int expectedCount) {
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setFingerprint(fingerprint);

        Integer uniqCardTokensByFingerprint = dgraphUniqueAggregator.countUniqueValue(
                PaymentCheckedField.FINGERPRINT,
                paymentModel,
                PaymentCheckedField.EMAIL,
                createTestTimeWindow(),
                new ArrayList<>()
        );
        assertEquals(expectedCount, uniqCardTokensByFingerprint,
                "Count of unique emails for the fingerprint is not equal to expected");
    }

    private void testUniqEmailsByIp(String ip, int expectedCount) {
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setIp(ip);

        Integer uniqCardTokensByFingerprint = dgraphUniqueAggregator.countUniqueValue(
                PaymentCheckedField.IP,
                paymentModel,
                PaymentCheckedField.EMAIL,
                createTestTimeWindow(),
                new ArrayList<>()
        );
        assertEquals(expectedCount, uniqCardTokensByFingerprint,
                "Count of unique emails for the ip is not equal to expected");
    }

    private void testUniqEmailsByShop(String shopId, int expectedCount) {
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setShopId(shopId);

        Integer uniqCardTokensByFingerprint = dgraphUniqueAggregator.countUniqueValue(
                PaymentCheckedField.SHOP_ID,
                paymentModel,
                PaymentCheckedField.EMAIL,
                createTestTimeWindow(),
                new ArrayList<>()
        );
        assertEquals(expectedCount, uniqCardTokensByFingerprint,
                "Count of unique emails for the shop id is not equal to expected");
    }

    private void testUniqEmailsByCardToken(String tokenId, int expectedCount) {
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setCardToken(tokenId);

        Integer uniqCardTokensByFingerprint = dgraphUniqueAggregator.countUniqueValue(
                PaymentCheckedField.CARD_TOKEN,
                paymentModel,
                PaymentCheckedField.EMAIL,
                createTestTimeWindow(),
                new ArrayList<>()
        );
        assertEquals(expectedCount, uniqCardTokensByFingerprint,
                "Count of unique emails for the card token is not equal to expected");
    }

    private void testUniqShopsByParty(String partyId, int expectedCount) {
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setPartyId(partyId);

        Integer uniqCardTokensByFingerprint = dgraphUniqueAggregator.countUniqueValue(
                PaymentCheckedField.PARTY_ID,
                paymentModel,
                PaymentCheckedField.SHOP_ID,
                createTestTimeWindow(),
                new ArrayList<>()
        );
        assertEquals(expectedCount, uniqCardTokensByFingerprint,
                "Count of unique shops for the party id is not equal to expected");
    }

}
