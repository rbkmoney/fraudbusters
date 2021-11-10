package com.rbkmoney.fraudbusters.dgraph.aggregates;

import com.rbkmoney.fraudbusters.config.dgraph.TemplateConfig;
import com.rbkmoney.fraudbusters.fraud.constant.DgraphEntity;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.payment.aggregator.dgraph.DgraphAggregationQueryBuilderService;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.DgraphEntityResolver;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.DgraphQueryConditionResolver;
import com.rbkmoney.fraudbusters.service.TemplateService;
import com.rbkmoney.fraudbusters.service.TemplateServiceImpl;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

import static com.rbkmoney.fraudbusters.dgraph.aggregates.data.DgraphPaymentCountQueryBuilderServiceTestData.*;
import static com.rbkmoney.fraudbusters.util.DgraphTestAggregationUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DgraphPaymentUniqueQueryBuilderServiceTest {

    private TemplateService templateService = new TemplateServiceImpl(new TemplateConfig().velocityEngine());

    private DgraphAggregationQueryBuilderService aggregationQueryBuilderService =
            new DgraphAggregationQueryBuilderService(
                    new DgraphEntityResolver(),
                    new DgraphQueryConditionResolver(),
                    templateService
            );

    @Test
    public void getPaymentsCountQueryByTokenRootWithMinimalDataTest() {
        String query = getPaymentsUniqueQuery(
                DgraphEntity.TOKEN,
                DgraphEntity.PARTY_SHOP,
                Map.of(DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN))
        );
        assertNotNull(query);
        assertEquals(PAYMENTS_COUNT_QUERY_BY_TOKEN_ROOT_WITH_MINIMAL_DATA, query);
    }
//
//    @Test
//    public void getPaymentsCountQueryByTokenRootWithUsualDatasetTest() {
//        String query = getPaymentsUniqueQuery(
//                DgraphEntity.TOKEN,
//                createTestUsualDgraphEntityMap(DgraphEntity.TOKEN, PaymentCheckedField.CARD_TOKEN)
//        );
//        assertNotNull(query);
//        assertEquals(PAYMENTS_COUNT_QUERY_BY_TOKEN_ROOT_WITH_USUAL_DATASET, query);
//    }
//
//    @Test
//    public void getPaymentsCountQueryByTokenRootWithFullDatasetTest() {
//        String query = getPaymentsUniqueQuery(DgraphEntity.TOKEN, createTestFullDgraphEntityMap());
//        assertNotNull(query);
//        assertEquals(PAYMENTS_COUNT_QUERY_BY_TOKEN_ROOT_WITH_FULL_DATASET, query);
//    }
//
//    @Test
//    public void getPaymentsCountQueryByEmailRootWithMinimalDataTest() {
//        String query = getPaymentsUniqueQuery(
//                DgraphEntity.EMAIL,
//                Map.of(DgraphEntity.EMAIL, Set.of(PaymentCheckedField.EMAIL))
//        );
//        assertNotNull(query);
//        assertEquals(PAYMENTS_COUNT_QUERY_BY_EMAIL_ROOT_WITH_MINIMAL_DATA, query);
//    }
//
//    @Test
//    public void getPaymentsCountQueryByEmailRootWithUsualDatasetTest() {
//        String query = getPaymentsUniqueQuery(
//                DgraphEntity.EMAIL,
//                createTestUsualDgraphEntityMap(DgraphEntity.EMAIL, PaymentCheckedField.EMAIL)
//        );
//        assertNotNull(query);
//        assertEquals(PAYMENTS_COUNT_QUERY_BY_EMAIL_ROOT_WITH_USUAL_DATASET, query);
//    }
//
//    @Test
//    public void getPaymentsCountQueryByEmailRootWithFullDatasetTest() {
//        String query = getPaymentsUniqueQuery(DgraphEntity.EMAIL, createTestFullDgraphEntityMap());
//        assertNotNull(query);
//        assertEquals(PAYMENTS_COUNT_QUERY_BY_EMAIL_ROOT_WITH_FULL_DATASET, query);
//    }
//
//    @Test
//    public void getPaymentsCountQueryByFingerprintRootWithMinimalDataTest() {
//        String query = getPaymentsUniqueQuery(
//                DgraphEntity.FINGERPRINT,
//                Map.of(DgraphEntity.FINGERPRINT, Set.of(PaymentCheckedField.FINGERPRINT))
//        );
//        assertNotNull(query);
//        assertEquals(PAYMENTS_COUNT_QUERY_BY_FINGERPRINT_ROOT_WITH_MINIMAL_DATA, query);
//    }
//
//    @Test
//    public void getPaymentsCountQueryByFingerprintRootWithUsualDatasetTest() {
//        String query = getPaymentsUniqueQuery(
//                DgraphEntity.FINGERPRINT,
//                createTestUsualDgraphEntityMap(DgraphEntity.FINGERPRINT, PaymentCheckedField.FINGERPRINT)
//        );
//        assertNotNull(query);
//        assertEquals(PAYMENTS_COUNT_QUERY_BY_FINGERPRINT_ROOT_WITH_USUAL_DATASET, query);
//    }
//
//    @Test
//    public void getPaymentsCountQueryByFingerprintRootWithFullDatasetTest() {
//        String query = getPaymentsUniqueQuery(DgraphEntity.FINGERPRINT, createTestFullDgraphEntityMap());
//        assertNotNull(query);
//        assertEquals(PAYMENTS_COUNT_QUERY_BY_FINGERPRINT_ROOT_WITH_FULL_DATASET, query);
//    }
//
//    @Test
//    public void getPaymentsCountQueryByIpRootWithMinimalDatasetTest() {
//        String query = getPaymentsUniqueQuery(
//                DgraphEntity.IP,
//                Map.of(DgraphEntity.IP, Set.of(PaymentCheckedField.IP))
//        );
//        assertNotNull(query);
//        assertEquals(PAYMENTS_COUNT_QUERY_BY_IP_ROOT_WITH_MINIMAL_DATASET, query);
//    }
//
//    @Test
//    public void getPaymentsCountQueryByIpRootWithUsualDatasetTest() {
//        String query = getPaymentsUniqueQuery(
//                DgraphEntity.IP,
//                createTestUsualDgraphEntityMap(DgraphEntity.IP, PaymentCheckedField.IP)
//        );
//        assertNotNull(query);
//        assertEquals(PAYMENTS_COUNT_QUERY_BY_IP_ROOT_WITH_USUAL_DATASET, query);
//    }
//
//    @Test
//    public void getPaymentsCountQueryByIpRootWithFullDatasetTest() {
//        String query = getPaymentsUniqueQuery(DgraphEntity.IP, createTestFullDgraphEntityMap());
//        assertNotNull(query);
//        assertEquals(PAYMENTS_COUNT_QUERY_BY_IP_ROOT_WITH_FULL_DATASET, query);
//    }
//
//    @Test
//    public void getPaymentsCountQueryByPanRootWithMinimalDatasetTest() {
//        String query = getPaymentsUniqueQuery(
//                DgraphEntity.BIN,
//                Map.of(DgraphEntity.BIN, Set.of(PaymentCheckedField.PAN))
//        );
//        assertNotNull(query);
//        assertEquals(PAYMENTS_COUNT_QUERY_BY_PAN_ROOT_WITH_MINIMAL_DATASET, query);
//    }
//
//    @Test
//    public void getPaymentsCountQueryByPanRootWithUsualDatasetTest() {
//        String query = getPaymentsUniqueQuery(
//                DgraphEntity.BIN,
//                createTestUsualDgraphEntityMap(DgraphEntity.BIN, PaymentCheckedField.PAN)
//        );
//        assertNotNull(query);
//        assertEquals(PAYMENTS_COUNT_QUERY_BY_PAN_ROOT_WITH_USUAL_DATASET, query);
//    }
//
//    @Test
//    public void getPaymentsCountQueryByPanRootWithFullDatasetTest() {
//        var dgraphMap = createTestFullDgraphEntityMap();
//        dgraphMap.put(DgraphEntity.TOKEN, Set.of(PaymentCheckedField.PAN));
//        String query = getPaymentsUniqueQuery(DgraphEntity.TOKEN, dgraphMap);
//        assertNotNull(query);
//        assertEquals(PAYMENTS_COUNT_QUERY_BY_PAN_ROOT_WITH_FULL_DATASET, query);
//    }
//
//    @Test
//    public void getPaymentsCountQueryByCurrencyRootWithMinimalDatasetTest() {
//        String query = getPaymentsUniqueQuery(
//                DgraphEntity.PAYMENT,
//                Map.of(DgraphEntity.PAYMENT, Set.of(PaymentCheckedField.CURRENCY))
//        );
//        assertNotNull(query);
//        assertEquals(PAYMENTS_COUNT_QUERY_BY_CURRENCY_ROOT_WITH_MINIMAL_DATASET, query);
//    }
//
//    @Test
//    public void getPaymentsCountQueryByCurrencyRootWithUsualDatasetTest() {
//        String query = getPaymentsUniqueQuery(
//                DgraphEntity.PAYMENT,
//                createTestUsualDgraphEntityMap(DgraphEntity.PAYMENT, PaymentCheckedField.CURRENCY)
//        );
//        assertNotNull(query);
//        assertEquals(PAYMENTS_COUNT_QUERY_BY_CURRENCY_ROOT_WITH_USUAL_DATASET, query);
//    }
//
//    @Test
//    public void getPaymentsCountQueryByCurrencyRootWithFullDatasetTest() {
//        var dgraphMap = createTestFullDgraphEntityMap();
//        dgraphMap.put(DgraphEntity.PAYMENT, Set.of(PaymentCheckedField.CURRENCY));
//        String query = getPaymentsUniqueQuery(DgraphEntity.PAYMENT, dgraphMap);
//        assertNotNull(query);
//        assertEquals(PAYMENTS_COUNT_QUERY_BY_CURRENCY_ROOT_WITH_FULL_DATASET, query);
//    }
//
//    @Test
//    public void getPaymentsCountQueryByShopIdRootWithMinimalDatasetTest() {
//        String query = getPaymentsUniqueQuery(
//                DgraphEntity.PARTY_SHOP,
//                Map.of(DgraphEntity.PARTY_SHOP, Set.of(PaymentCheckedField.SHOP_ID))
//        );
//        assertNotNull(query);
//        assertEquals(PAYMENTS_COUNT_QUERY_BY_SHOP_ID_ROOT_WITH_MINIMAL_DATASET, query);
//    }
//
//    @Test
//    public void getPaymentsCountQueryByShopIdRootWithUsualDatasetTest() {
//        String query = getPaymentsUniqueQuery(
//                DgraphEntity.PARTY_SHOP,
//                Map.of(DgraphEntity.BIN, Set.of(PaymentCheckedField.BIN))
//        );
//        assertNotNull(query);
//        assertEquals(PAYMENTS_COUNT_QUERY_BY_SHOP_ID_ROOT_WITH_USUAL_DATASET, query);
//    }
//
//    @Test
//    public void getPaymentsCountQueryByShopIdRootWithFullDatasetTest() {
//        var dgraphMap = createTestFullDgraphEntityMap();
//        dgraphMap.put(DgraphEntity.PARTY_SHOP, Set.of(PaymentCheckedField.SHOP_ID));
//        String query = getPaymentsUniqueQuery(DgraphEntity.PARTY_SHOP, dgraphMap);
//        assertNotNull(query);
//        assertEquals(PAYMENTS_COUNT_QUERY_BY_SHOP_ID_ROOT_WITH_FULL_DATASET, query);
//    }
//
//    @Test
//    public void getPaymentsCountQueryByPartyIdRootWithMinimalDatasetTest() {
//        String query = getPaymentsUniqueQuery(
//                DgraphEntity.PARTY_SHOP,
//                Map.of(DgraphEntity.PARTY_SHOP, Set.of(PaymentCheckedField.PARTY_ID))
//        );
//        assertNotNull(query);
//        assertEquals(PAYMENTS_COUNT_QUERY_BY_PARTY_ID_ROOT_WITH_MINIMAL_DATASET, query);
//    }
//
//    @Test
//    public void getPaymentsCountQueryByPartyIdRootWithUsualDatasetTest() {
//        String query = getPaymentsUniqueQuery(
//                DgraphEntity.PARTY_SHOP,
//                Map.of(
//                        DgraphEntity.BIN, Set.of(PaymentCheckedField.BIN),
//                        DgraphEntity.PARTY_SHOP, Set.of(PaymentCheckedField.PARTY_ID)
//                )
//        );
//        assertNotNull(query);
//        assertEquals(PAYMENTS_COUNT_QUERY_BY_PARTY_ID_ROOT_WITH_USUAL_DATASET, query);
//    }
//
//    @Test
//    public void getPaymentsCountQueryByPartyIdRootWithFullDatasetTest() {
//        String query = getPaymentsUniqueQuery(DgraphEntity.PARTY_SHOP, createTestFullDgraphEntityMap());
//        assertNotNull(query);
//        assertEquals(PAYMENTS_COUNT_QUERY_BY_PARTY_ID_ROOT_WITH_FULL_DATASET, query);
//    }
//
//    @Test
//    public void getPaymentsCountQueryByCountryBankRootWithMinimalDatasetTest() {
//        String query = getPaymentsUniqueQuery(
//                DgraphEntity.PAYMENT,
//                Map.of(DgraphEntity.PAYMENT, Set.of(PaymentCheckedField.COUNTRY_BANK))
//        );
//        assertNotNull(query);
//        assertEquals(PAYMENTS_COUNT_QUERY_BY_COUNTRY_BANK_ROOT_WITH_MINIMAL_DATASET, query);
//    }
//
//    @Test
//    public void getPaymentsCountQueryByCountryBankRootWithUsualDatasetTest() {
//        String query = getPaymentsUniqueQuery(
//                DgraphEntity.PAYMENT,
//                createTestUsualDgraphEntityMap(DgraphEntity.PAYMENT, PaymentCheckedField.COUNTRY_BANK)
//        );
//        assertNotNull(query);
//        assertEquals(PAYMENTS_COUNT_QUERY_BY_COUNTRY_BANK_ROOT_WITH_USUAL_DATASET, query);
//    }
//
//    @Test
//    public void getPaymentsCountQueryByCountryBankRootWithFullDatasetTest() {
//        var dgraphMap = createTestFullDgraphEntityMap();
//        dgraphMap.put(DgraphEntity.PAYMENT, Set.of(PaymentCheckedField.COUNTRY_BANK));
//        String query = getPaymentsUniqueQuery(DgraphEntity.PAYMENT, dgraphMap);
//        assertNotNull(query);
//        assertEquals(PAYMENTS_COUNT_QUERY_BY_COUNTRY_BANK_ROOT_WITH_FULL_DATASET, query);
//    }
//
//    @Test
//    public void getPaymentsCountQueryByCountryIpRootWithMinimalDatasetTest() {
//        String query = getPaymentsUniqueQuery(
//                DgraphEntity.IP,
//                Map.of(DgraphEntity.IP, Set.of(PaymentCheckedField.COUNTRY_IP))
//        );
//        assertNotNull(query);
//        assertEquals(PAYMENTS_COUNT_QUERY_BY_COUNTRY_IP_ROOT_WITH_MINIMAL_DATASET, query);
//    }
//
//    @Test
//    public void getPaymentsCountQueryByCountryIpRootWithUsualDatasetTest() {
//        String query = getPaymentsUniqueQuery(
//                DgraphEntity.IP,
//                createTestUsualDgraphEntityMap(DgraphEntity.IP, PaymentCheckedField.COUNTRY_IP)
//        );
//        assertNotNull(query);
//        assertEquals(PAYMENTS_COUNT_QUERY_BY_COUNTRY_IP_ROOT_WITH_USUAL_DATASET, query);
//    }
//
//    @Test
//    public void getPaymentsCountQueryByCountryIpRootWithFullDatasetTest() {
//        var dgraphMap = createTestFullDgraphEntityMap();
//        dgraphMap.put(DgraphEntity.IP, Set.of(PaymentCheckedField.COUNTRY_IP));
//        String query = getPaymentsUniqueQuery(DgraphEntity.IP, dgraphMap);
//        assertNotNull(query);
//        assertEquals(PAYMENTS_COUNT_QUERY_BY_COUNTRY_IP_ROOT_WITH_FULL_DATASET, query);
//    }
//
//    @Test
//    public void getPaymentsCountQueryByMobileRootWithMinimalDatasetTest() {
//        String query = getPaymentsUniqueQuery(
//                DgraphEntity.PAYMENT,
//                Map.of(DgraphEntity.PAYMENT, Set.of(PaymentCheckedField.MOBILE))
//        );
//        assertNotNull(query);
//        assertEquals(PAYMENTS_COUNT_QUERY_BY_MOBILE_ROOT_WITH_MINIMAL_DATASET, query);
//    }
//
//    @Test
//    public void getPaymentsCountQueryByMobileRootWithUsualDatasetTest() {
//        String query = getPaymentsUniqueQuery(
//                DgraphEntity.PAYMENT,
//                createTestUsualDgraphEntityMap(DgraphEntity.PAYMENT, PaymentCheckedField.MOBILE)
//        );
//        assertNotNull(query);
//        assertEquals(PAYMENTS_COUNT_QUERY_BY_MOBILE_ROOT_WITH_USUAL_DATASET, query);
//    }
//
//    @Test
//    public void getPaymentsCountQueryByMobileRootWithFullDatasetTest() {
//        var dgraphMap = createTestFullDgraphEntityMap();
//        dgraphMap.put(DgraphEntity.PAYMENT, Set.of(PaymentCheckedField.MOBILE, PaymentCheckedField.RECURRENT));
//        String query = getPaymentsUniqueQuery(DgraphEntity.PAYMENT, dgraphMap);
//        assertNotNull(query);
//        assertEquals(PAYMENTS_COUNT_QUERY_BY_MOBILE_ROOT_WITH_FULL_DATASET, query);
//    }
//
//    @Test
//    public void getPaymentsCountQueryByRecurrentRootWithMinimalDatasetTest() {
//        String query = getPaymentsUniqueQuery(
//                DgraphEntity.PAYMENT,
//                Map.of(DgraphEntity.PAYMENT, Set.of(PaymentCheckedField.RECURRENT))
//        );
//        assertNotNull(query);
//        assertEquals(PAYMENTS_COUNT_QUERY_BY_RECURRENT_ROOT_WITH_MINIMAL_DATASET, query);
//    }
//
//    @Test
//    public void getPaymentsCountQueryByRecurrentRootWithUsualDatasetTest() {
//        String query = getPaymentsUniqueQuery(
//                DgraphEntity.PAYMENT,
//                createTestUsualDgraphEntityMap(DgraphEntity.PAYMENT, PaymentCheckedField.RECURRENT)
//        );
//        assertNotNull(query);
//        assertEquals(PAYMENTS_COUNT_QUERY_BY_RECURRENT_ROOT_WITH_USUAL_DATASET, query);
//    }
//
//    @Test
//    public void getPaymentsCountQueryByRecurrentRootWithFullDatasetTest() {
//        var dgraphMap = createTestFullDgraphEntityMap();
//        dgraphMap.put(DgraphEntity.PAYMENT, Set.of(PaymentCheckedField.MOBILE, PaymentCheckedField.RECURRENT));
//        String query = getPaymentsUniqueQuery(DgraphEntity.PAYMENT, dgraphMap);
//        assertNotNull(query);
//        assertEquals(PAYMENTS_COUNT_QUERY_BY_RECURRENT_ROOT_WITH_FULL_DATASET, query);
//    }

    private String getPaymentsUniqueQuery(DgraphEntity rootEntity,
                                          DgraphEntity onField,
                                          Map<DgraphEntity, Set<PaymentCheckedField>> dgraphEntitySetMap) {
        return aggregationQueryBuilderService.getUniqueQuery(
                rootEntity,
                onField,
                dgraphEntitySetMap,
                createTestPaymentModel(),
                Instant.parse("2021-10-28T19:40:54.000000Z"),
                Instant.parse("2021-10-28T19:47:54.000000Z"),
                "captured"
        );
    }

}
