package com.rbkmoney.fraudbusters.dgraph.aggregates;

import com.rbkmoney.fraudbusters.config.dgraph.TemplateConfig;
import com.rbkmoney.fraudbusters.fraud.constant.DgraphEntity;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.payment.aggregator.dgraph.DgraphAggregationQueryBuilderServiceImpl;
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

public class DgraphPaymentCountQueryBuilderServiceTest {

    private TemplateService templateService = new TemplateServiceImpl(new TemplateConfig().velocityEngine());

    private DgraphAggregationQueryBuilderServiceImpl aggregationQueryBuilderService =
            new DgraphAggregationQueryBuilderServiceImpl(
                    new DgraphEntityResolver(),
                    new DgraphQueryConditionResolver(),
                    templateService
            );

    @Test
    public void getPaymentsCountQueryByTokenRootWithMinimalDataTest() {
        String query = getPaymentsCountQuery(
                DgraphEntity.TOKEN,
                Map.of(DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN))
        );
        assertNotNull(query);
        assertEquals(PAYMENTS_COUNT_QUERY_BY_TOKEN_ROOT_WITH_MINIMAL_DATA, query);
    }

    @Test
    public void getPaymentsCountQueryByTokenRootWithUsualDatasetTest() {
        String query = getPaymentsCountQuery(
                DgraphEntity.TOKEN,
                createTestUsualDgraphEntityMap(DgraphEntity.TOKEN, PaymentCheckedField.CARD_TOKEN)
        );
        assertNotNull(query);
        assertEquals(PAYMENTS_COUNT_QUERY_BY_TOKEN_ROOT_WITH_USUAL_DATASET, query);
    }

    @Test
    public void getPaymentsCountQueryByTokenRootWithFullDatasetTest() {
        String query = getPaymentsCountQuery(DgraphEntity.TOKEN, createTestFullDgraphEntityMap());
        assertNotNull(query);
        assertEquals(PAYMENTS_COUNT_QUERY_BY_TOKEN_ROOT_WITH_FULL_DATASET, query);
    }

    @Test
    public void getPaymentsCountQueryByEmailRootWithMinimalDataTest() {
        String query = getPaymentsCountQuery(
                DgraphEntity.EMAIL,
                Map.of(DgraphEntity.EMAIL, Set.of(PaymentCheckedField.EMAIL))
        );
        assertNotNull(query);
        assertEquals(PAYMENTS_COUNT_QUERY_BY_EMAIL_ROOT_WITH_MINIMAL_DATA, query);
    }

    @Test
    public void getPaymentsCountQueryByEmailRootWithUsualDatasetTest() {
        String query = getPaymentsCountQuery(
                DgraphEntity.EMAIL,
                createTestUsualDgraphEntityMap(DgraphEntity.EMAIL, PaymentCheckedField.EMAIL)
        );
        assertNotNull(query);
        assertEquals(PAYMENTS_COUNT_QUERY_BY_EMAIL_ROOT_WITH_USUAL_DATASET, query);
    }

    @Test
    public void getPaymentsCountQueryByEmailRootWithFullDatasetTest() {
        String query = getPaymentsCountQuery(DgraphEntity.EMAIL, createTestFullDgraphEntityMap());
        assertNotNull(query);
        assertEquals(PAYMENTS_COUNT_QUERY_BY_EMAIL_ROOT_WITH_FULL_DATASET, query);
    }

    @Test
    public void getPaymentsCountQueryByFingerprintRootWithMinimalDataTest() {
        String query = getPaymentsCountQuery(
                DgraphEntity.FINGERPRINT,
                Map.of(DgraphEntity.FINGERPRINT, Set.of(PaymentCheckedField.FINGERPRINT))
        );
        assertNotNull(query);
        assertEquals(PAYMENTS_COUNT_QUERY_BY_FINGERPRINT_ROOT_WITH_MINIMAL_DATA, query);
    }

    @Test
    public void getPaymentsCountQueryByFingerprintRootWithUsualDatasetTest() {
        String query = getPaymentsCountQuery(
                DgraphEntity.FINGERPRINT,
                createTestUsualDgraphEntityMap(DgraphEntity.FINGERPRINT, PaymentCheckedField.FINGERPRINT)
        );
        assertNotNull(query);
        assertEquals(PAYMENTS_COUNT_QUERY_BY_FINGERPRINT_ROOT_WITH_USUAL_DATASET, query);
    }

    @Test
    public void getPaymentsCountQueryByFingerprintRootWithFullDatasetTest() {
        String query = getPaymentsCountQuery(DgraphEntity.FINGERPRINT, createTestFullDgraphEntityMap());
        assertNotNull(query);
        assertEquals(PAYMENTS_COUNT_QUERY_BY_FINGERPRINT_ROOT_WITH_FULL_DATASET, query);
    }

    @Test
    public void getPaymentsCountQueryByIpRootWithMinimalDatasetTest() {
        String query = getPaymentsCountQuery(
                DgraphEntity.IP,
                Map.of(DgraphEntity.IP, Set.of(PaymentCheckedField.IP))
        );
        assertNotNull(query);
        assertEquals(PAYMENTS_COUNT_QUERY_BY_IP_ROOT_WITH_MINIMAL_DATASET, query);
    }

    @Test
    public void getPaymentsCountQueryByIpRootWithUsualDatasetTest() {
        String query = getPaymentsCountQuery(
                DgraphEntity.IP,
                createTestUsualDgraphEntityMap(DgraphEntity.IP, PaymentCheckedField.IP)
        );
        assertNotNull(query);
        assertEquals(PAYMENTS_COUNT_QUERY_BY_IP_ROOT_WITH_USUAL_DATASET, query);
    }

    @Test
    public void getPaymentsCountQueryByIpRootWithFullDatasetTest() {
        String query = getPaymentsCountQuery(DgraphEntity.IP, createTestFullDgraphEntityMap());
        assertNotNull(query);
        assertEquals(PAYMENTS_COUNT_QUERY_BY_IP_ROOT_WITH_FULL_DATASET, query);
    }

    @Test
    public void getPaymentsCountQueryByPanRootWithMinimalDatasetTest() {
        String query = getPaymentsCountQuery(
                DgraphEntity.BIN,
                Map.of(DgraphEntity.BIN, Set.of(PaymentCheckedField.PAN))
        );
        assertNotNull(query);
        assertEquals(PAYMENTS_COUNT_QUERY_BY_PAN_ROOT_WITH_MINIMAL_DATASET, query);
    }

    @Test
    public void getPaymentsCountQueryByPanRootWithUsualDatasetTest() {
        String query = getPaymentsCountQuery(
                DgraphEntity.BIN,
                createTestUsualDgraphEntityMap(DgraphEntity.BIN, PaymentCheckedField.PAN)
        );
        assertNotNull(query);
        assertEquals(PAYMENTS_COUNT_QUERY_BY_PAN_ROOT_WITH_USUAL_DATASET, query);
    }

    @Test
    public void getPaymentsCountQueryByPanRootWithFullDatasetTest() {
        var dgraphMap = createTestFullDgraphEntityMap();
        dgraphMap.put(DgraphEntity.TOKEN, Set.of(PaymentCheckedField.PAN));
        String query = getPaymentsCountQuery(DgraphEntity.TOKEN, dgraphMap);
        assertNotNull(query);
        assertEquals(PAYMENTS_COUNT_QUERY_BY_PAN_ROOT_WITH_FULL_DATASET, query);
    }

    @Test
    public void getPaymentsCountQueryByCurrencyRootWithMinimalDatasetTest() {
        String query = getPaymentsCountQuery(
                DgraphEntity.CURRENCY,
                Map.of(DgraphEntity.CURRENCY, Set.of(PaymentCheckedField.CURRENCY))
        );
        assertNotNull(query);
        assertEquals(PAYMENTS_COUNT_QUERY_BY_CURRENCY_ROOT_WITH_MINIMAL_DATASET, query);
    }

    @Test
    public void getPaymentsCountQueryByCurrencyRootWithUsualDatasetTest() {
        String query = getPaymentsCountQuery(
                DgraphEntity.CURRENCY,
                createTestUsualDgraphEntityMap(DgraphEntity.CURRENCY, PaymentCheckedField.CURRENCY)
        );
        assertNotNull(query);
        assertEquals(PAYMENTS_COUNT_QUERY_BY_CURRENCY_ROOT_WITH_USUAL_DATASET, query);
    }

    @Test
    public void getPaymentsCountQueryByCurrencyRootWithFullDatasetTest() {
        var dgraphMap = createTestFullDgraphEntityMap();
        dgraphMap.put(DgraphEntity.CURRENCY, Set.of(PaymentCheckedField.CURRENCY));
        String query = getPaymentsCountQuery(DgraphEntity.CURRENCY, dgraphMap);
        assertNotNull(query);
        assertEquals(PAYMENTS_COUNT_QUERY_BY_CURRENCY_ROOT_WITH_FULL_DATASET, query);
    }

    @Test
    public void getPaymentsCountQueryByShopIdRootWithMinimalDatasetTest() {
        String query = getPaymentsCountQuery(
                DgraphEntity.SHOP,
                Map.of(DgraphEntity.SHOP, Set.of(PaymentCheckedField.SHOP_ID))
        );
        assertNotNull(query);
        assertEquals(PAYMENTS_COUNT_QUERY_BY_SHOP_ID_ROOT_WITH_MINIMAL_DATASET, query);
    }

    @Test
    public void getPaymentsCountQueryByShopIdRootWithUsualDatasetTest() {
        String query = getPaymentsCountQuery(
                DgraphEntity.SHOP,
                Map.of(DgraphEntity.BIN, Set.of(PaymentCheckedField.BIN))
        );
        assertNotNull(query);
        assertEquals(PAYMENTS_COUNT_QUERY_BY_SHOP_ID_ROOT_WITH_USUAL_DATASET, query);
    }

    @Test
    public void getPaymentsCountQueryByShopIdRootWithFullDatasetTest() {
        String query = getPaymentsCountQuery(DgraphEntity.SHOP, createTestFullDgraphEntityMap());
        assertNotNull(query);
        assertEquals(PAYMENTS_COUNT_QUERY_BY_SHOP_ID_ROOT_WITH_FULL_DATASET, query);
    }

    @Test
    public void getPaymentsCountQueryByPartyIdRootWithMinimalDatasetTest() {
        String query = getPaymentsCountQuery(
                DgraphEntity.PARTY,
                Map.of(DgraphEntity.PARTY, Set.of(PaymentCheckedField.PARTY_ID))
        );
        assertNotNull(query);
        assertEquals(PAYMENTS_COUNT_QUERY_BY_PARTY_ID_ROOT_WITH_MINIMAL_DATASET, query);
    }

    @Test
    public void getPaymentsCountQueryByPartyIdRootWithUsualDatasetTest() {
        String query = getPaymentsCountQuery(
                DgraphEntity.PARTY,
                Map.of(
                        DgraphEntity.BIN, Set.of(PaymentCheckedField.BIN),
                        DgraphEntity.PARTY, Set.of(PaymentCheckedField.PARTY_ID)
                )
        );
        assertNotNull(query);
        assertEquals(PAYMENTS_COUNT_QUERY_BY_PARTY_ID_ROOT_WITH_USUAL_DATASET, query);
    }

    @Test
    public void getPaymentsCountQueryByPartyIdRootWithFullDatasetTest() {
        String query = getPaymentsCountQuery(DgraphEntity.PARTY, createTestFullDgraphEntityMap());
        assertNotNull(query);
        assertEquals(PAYMENTS_COUNT_QUERY_BY_PARTY_ID_ROOT_WITH_FULL_DATASET, query);
    }

    @Test
    public void getPaymentsCountQueryByCountryBankRootWithMinimalDatasetTest() {
        String query = getPaymentsCountQuery(
                DgraphEntity.COUNTRY,
                Map.of(DgraphEntity.COUNTRY, Set.of(PaymentCheckedField.COUNTRY_BANK))
        );
        assertNotNull(query);
        assertEquals(PAYMENTS_COUNT_QUERY_BY_COUNTRY_BANK_ROOT_WITH_MINIMAL_DATASET, query);
    }

    @Test
    public void getPaymentsCountQueryByCountryBankRootWithUsualDatasetTest() {
        String query = getPaymentsCountQuery(
                DgraphEntity.COUNTRY,
                createTestUsualDgraphEntityMap(DgraphEntity.COUNTRY, PaymentCheckedField.COUNTRY_BANK)
        );
        assertNotNull(query);
        assertEquals(PAYMENTS_COUNT_QUERY_BY_COUNTRY_BANK_ROOT_WITH_USUAL_DATASET, query);
    }

    @Test
    public void getPaymentsCountQueryByCountryBankRootWithFullDatasetTest() {
        var dgraphMap = createTestFullDgraphEntityMap();
        dgraphMap.put(DgraphEntity.COUNTRY, Set.of(PaymentCheckedField.COUNTRY_BANK));
        String query = getPaymentsCountQuery(DgraphEntity.COUNTRY, dgraphMap);
        assertNotNull(query);
        assertEquals(PAYMENTS_COUNT_QUERY_BY_COUNTRY_BANK_ROOT_WITH_FULL_DATASET, query);
    }

    @Test
    public void getPaymentsCountQueryByCountryIpRootWithMinimalDatasetTest() {
        String query = getPaymentsCountQuery(
                DgraphEntity.IP,
                Map.of(DgraphEntity.IP, Set.of(PaymentCheckedField.COUNTRY_IP))
        );
        assertNotNull(query);
        assertEquals(PAYMENTS_COUNT_QUERY_BY_COUNTRY_IP_ROOT_WITH_MINIMAL_DATASET, query);
    }

    @Test
    public void getPaymentsCountQueryByCountryIpRootWithUsualDatasetTest() {
        String query = getPaymentsCountQuery(
                DgraphEntity.IP,
                createTestUsualDgraphEntityMap(DgraphEntity.IP, PaymentCheckedField.COUNTRY_IP)
        );
        assertNotNull(query);
        assertEquals(PAYMENTS_COUNT_QUERY_BY_COUNTRY_IP_ROOT_WITH_USUAL_DATASET, query);
    }

    @Test
    public void getPaymentsCountQueryByCountryIpRootWithFullDatasetTest() {
        var dgraphMap = createTestFullDgraphEntityMap();
        dgraphMap.put(DgraphEntity.IP, Set.of(PaymentCheckedField.COUNTRY_IP));
        String query = getPaymentsCountQuery(DgraphEntity.IP, dgraphMap);
        assertNotNull(query);
        assertEquals(PAYMENTS_COUNT_QUERY_BY_COUNTRY_IP_ROOT_WITH_FULL_DATASET, query);
    }

    @Test
    public void getPaymentsCountQueryByMobileRootWithMinimalDatasetTest() {
        String query = getPaymentsCountQuery(
                DgraphEntity.PAYMENT,
                Map.of(DgraphEntity.PAYMENT, Set.of(PaymentCheckedField.MOBILE))
        );
        assertNotNull(query);
        assertEquals(PAYMENTS_COUNT_QUERY_BY_MOBILE_ROOT_WITH_MINIMAL_DATASET, query);
    }

    @Test
    public void getPaymentsCountQueryByMobileRootWithUsualDatasetTest() {
        String query = getPaymentsCountQuery(
                DgraphEntity.PAYMENT,
                createTestUsualDgraphEntityMap(DgraphEntity.PAYMENT, PaymentCheckedField.MOBILE)
        );
        assertNotNull(query);
        assertEquals(PAYMENTS_COUNT_QUERY_BY_MOBILE_ROOT_WITH_USUAL_DATASET, query);
    }

    @Test
    public void getPaymentsCountQueryByMobileRootWithFullDatasetTest() {
        var dgraphMap = createTestFullDgraphEntityMap();
        dgraphMap.put(DgraphEntity.PAYMENT, Set.of(PaymentCheckedField.MOBILE, PaymentCheckedField.RECURRENT));
        String query = getPaymentsCountQuery(DgraphEntity.PAYMENT, dgraphMap);
        assertNotNull(query);
        assertEquals(PAYMENTS_COUNT_QUERY_BY_MOBILE_ROOT_WITH_FULL_DATASET, query);
    }

    @Test
    public void getPaymentsCountQueryByRecurrentRootWithMinimalDatasetTest() {
        String query = getPaymentsCountQuery(
                DgraphEntity.PAYMENT,
                Map.of(DgraphEntity.PAYMENT, Set.of(PaymentCheckedField.RECURRENT))
        );
        assertNotNull(query);
        assertEquals(PAYMENTS_COUNT_QUERY_BY_RECURRENT_ROOT_WITH_MINIMAL_DATASET, query);
    }

    @Test
    public void getPaymentsCountQueryByRecurrentRootWithUsualDatasetTest() {
        String query = getPaymentsCountQuery(
                DgraphEntity.PAYMENT,
                createTestUsualDgraphEntityMap(DgraphEntity.PAYMENT, PaymentCheckedField.RECURRENT)
        );
        assertNotNull(query);
        assertEquals(PAYMENTS_COUNT_QUERY_BY_RECURRENT_ROOT_WITH_USUAL_DATASET, query);
    }

    @Test
    public void getPaymentsCountQueryByRecurrentRootWithFullDatasetTest() {
        var dgraphMap = createTestFullDgraphEntityMap();
        dgraphMap.put(DgraphEntity.PAYMENT, Set.of(PaymentCheckedField.MOBILE, PaymentCheckedField.RECURRENT));
        String query = getPaymentsCountQuery(DgraphEntity.PAYMENT, dgraphMap);
        assertNotNull(query);
        assertEquals(PAYMENTS_COUNT_QUERY_BY_RECURRENT_ROOT_WITH_FULL_DATASET, query);
    }

    private String getPaymentsCountQuery(DgraphEntity rootEntity,
                                         Map<DgraphEntity, Set<PaymentCheckedField>> dgraphEntitySetMap) {
        return aggregationQueryBuilderService.getCountQuery(
                rootEntity,
                DgraphEntity.PAYMENT,
                dgraphEntitySetMap,
                createTestPaymentModel(),
                Instant.parse("2021-10-28T19:40:54.000000Z"),
                Instant.parse("2021-10-28T19:47:54.000000Z"),
                "captured"
        );
    }

}
