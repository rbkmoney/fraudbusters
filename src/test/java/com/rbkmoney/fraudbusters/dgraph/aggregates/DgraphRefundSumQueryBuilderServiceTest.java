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

import static com.rbkmoney.fraudbusters.dgraph.aggregates.data.DgraphRefundsSumQueryBuilderServiceTestData.*;
import static com.rbkmoney.fraudbusters.util.DgraphTestAggregationUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DgraphRefundSumQueryBuilderServiceTest {

    private TemplateService templateService = new TemplateServiceImpl(new TemplateConfig().velocityEngine());

    private DgraphAggregationQueryBuilderService aggregationQueryBuilderService =
            new DgraphAggregationQueryBuilderService(
                    new DgraphEntityResolver(),
                    new DgraphQueryConditionResolver(),
                    templateService
            );

    @Test
    public void getRefundsSumQueryByTokenRootWithMinimalDataTest() {
        String query = getRefundsSumQuery(
                DgraphEntity.TOKEN,
                Map.of(DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN))
        );
        assertNotNull(query);
        assertEquals(REFUNDS_SUM_QUERY_BY_TOKEN_ROOT_WITH_MINIMAL_DATASET, query);
    }

    @Test
    public void getRefundsSumQueryByTokenRootWithUsualDatasetTest() {
        String query = getRefundsSumQuery(
                DgraphEntity.TOKEN,
                createTestUsualDgraphEntityMap(DgraphEntity.TOKEN, PaymentCheckedField.CARD_TOKEN)
        );
        assertNotNull(query);
        assertEquals(REFUNDS_SUM_QUERY_BY_TOKEN_ROOT_WITH_USUAL_DATASET, query);
    }

    @Test
    public void getRefundsSumQueryByTokenRootWithFullDatasetTest() {
        String query = getRefundsSumQuery(DgraphEntity.TOKEN, createTestFullDgraphEntityMap());
        assertNotNull(query);
        assertEquals(REFUNDS_SUM_QUERY_BY_TOKEN_ROOT_WITH_FULL_DATASET, query);
    }

    @Test
    public void getRefundsSumQueryByEmailRootWithMinimalDataTest() {
        String query = getRefundsSumQuery(
                DgraphEntity.EMAIL,
                Map.of(DgraphEntity.EMAIL, Set.of(PaymentCheckedField.EMAIL))
        );
        assertNotNull(query);
        assertEquals(REFUNDS_SUM_QUERY_BY_EMAIL_ROOT_WITH_MINIMAL_DATASET, query);
    }

    @Test
    public void getRefundsSumQueryByEmailRootWithUsualDatasetTest() {
        String query = getRefundsSumQuery(
                DgraphEntity.EMAIL,
                createTestUsualDgraphEntityMap(DgraphEntity.EMAIL, PaymentCheckedField.EMAIL)
        );
        assertNotNull(query);
        assertEquals(REFUNDS_SUM_QUERY_BY_EMAIL_ROOT_WITH_USUAL_DATASET, query);
    }

    @Test
    public void getRefundsSumQueryByEmailRootWithFullDatasetTest() {
        String query = getRefundsSumQuery(DgraphEntity.EMAIL, createTestFullDgraphEntityMap());
        assertNotNull(query);
        assertEquals(REFUNDS_SUM_QUERY_BY_EMAIL_ROOT_WITH_FULL_DATASET, query);
    }

    @Test
    public void getRefundsSumQueryByIpRootWithMinimalDataTest() {
        String query = getRefundsSumQuery(
                DgraphEntity.IP,
                Map.of(DgraphEntity.IP, Set.of(PaymentCheckedField.IP))
        );
        assertNotNull(query);
        assertEquals(REFUNDS_SUM_QUERY_BY_IP_ROOT_WITH_MINIMAL_DATASET, query);
    }

    @Test
    public void getRefundsSumQueryByIpRootWithUsualDatasetTest() {
        String query = getRefundsSumQuery(
                DgraphEntity.IP,
                createTestUsualDgraphEntityMap(DgraphEntity.IP, PaymentCheckedField.IP)
        );
        assertNotNull(query);
        assertEquals(REFUNDS_SUM_QUERY_BY_IP_ROOT_WITH_USUAL_DATASET, query);
    }

    @Test
    public void getRefundsSumQueryByIpRootWithFullDatasetTest() {
        String query = getRefundsSumQuery(DgraphEntity.IP, createTestFullDgraphEntityMap());
        assertNotNull(query);
        assertEquals(REFUNDS_SUM_QUERY_BY_IP_ROOT_WITH_FULL_DATASET, query);
    }

    @Test
    public void getRefundsSumQueryByFingerprintRootWithMinimalDataTest() {
        String query = getRefundsSumQuery(
                DgraphEntity.FINGERPRINT,
                Map.of(DgraphEntity.FINGERPRINT, Set.of(PaymentCheckedField.FINGERPRINT))
        );
        assertNotNull(query);
        assertEquals(REFUNDS_SUM_QUERY_BY_FINGERPRINT_ROOT_WITH_MINIMAL_DATASET, query);
    }

    @Test
    public void getRefundsSumQueryByFingerprintRootWithUsualDatasetTest() {
        String query = getRefundsSumQuery(
                DgraphEntity.FINGERPRINT,
                createTestUsualDgraphEntityMap(DgraphEntity.FINGERPRINT, PaymentCheckedField.FINGERPRINT)
        );
        assertNotNull(query);
        assertEquals(REFUNDS_SUM_QUERY_BY_FINGERPRINT_ROOT_WITH_USUAL_DATASET, query);
    }

    @Test
    public void getRefundsSumQueryByFingerprintRootWithFullDatasetTest() {
        String query = getRefundsSumQuery(DgraphEntity.FINGERPRINT, createTestFullDgraphEntityMap());
        assertNotNull(query);
        assertEquals(REFUNDS_SUM_QUERY_BY_FINGERPRINT_ROOT_WITH_FULL_DATASET, query);
    }

    @Test
    public void getRefundsSumQueryByCountryBankRootWithMinimalDataTest() {
        String query = getRefundsSumQuery(
                DgraphEntity.COUNTRY,
                Map.of(DgraphEntity.COUNTRY, Set.of(PaymentCheckedField.COUNTRY_BANK))
        );
        assertNotNull(query);
        assertEquals(REFUNDS_SUM_QUERY_BY_COUNTRY_BANK_ROOT_WITH_MINIMAL_DATASET, query);
    }

    @Test
    public void getRefundsSumQueryByCountryBankRootWithUsualDatasetTest() {
        String query = getRefundsSumQuery(
                DgraphEntity.COUNTRY,
                createTestUsualDgraphEntityMap(DgraphEntity.COUNTRY, PaymentCheckedField.COUNTRY_BANK)
        );
        assertNotNull(query);
        assertEquals(REFUNDS_SUM_QUERY_BY_COUNTRY_BANK_ROOT_WITH_USUAL_DATASET, query);
    }

    @Test
    public void getRefundsSumQueryByCountryBankRootWithFullDatasetTest() {
        String query = getRefundsSumQuery(DgraphEntity.COUNTRY, createTestFullDgraphEntityMap());
        assertNotNull(query);
        assertEquals(REFUNDS_SUM_QUERY_BY_COUNTRY_BANK_ROOT_WITH_FULL_DATASET, query);
    }

    @Test
    public void getRefundsSumQueryByCountryIpRootWithMinimalDataTest() {
        String query = getRefundsSumQuery(
                DgraphEntity.IP,
                Map.of(DgraphEntity.IP, Set.of(PaymentCheckedField.COUNTRY_IP))
        );
        assertNotNull(query);
        assertEquals(REFUNDS_SUM_QUERY_BY_COUNTRY_IP_ROOT_WITH_MINIMAL_DATASET, query);
    }

    @Test
    public void getRefundsSumQueryByCountryIpRootWithUsualDatasetTest() {
        String query = getRefundsSumQuery(
                DgraphEntity.IP,
                createTestUsualDgraphEntityMap(DgraphEntity.IP, PaymentCheckedField.COUNTRY_IP)
        );
        assertNotNull(query);
        assertEquals(REFUNDS_SUM_QUERY_BY_COUNTRY_IP_ROOT_WITH_USUAL_DATASET, query);
    }

    @Test
    public void getRefundsSumQueryByCountryIpRootWithFullDatasetTest() {
        String query = getRefundsSumQuery(DgraphEntity.IP, createTestFullDgraphEntityMap());
        assertNotNull(query);
        assertEquals(REFUNDS_SUM_QUERY_BY_COUNTRY_IP_ROOT_WITH_FULL_DATASET, query);
    }

    @Test
    public void getRefundsSumQueryByPanRootWithMinimalDataTest() {
        String query = getRefundsSumQuery(
                DgraphEntity.TOKEN,
                Map.of(DgraphEntity.TOKEN, Set.of(PaymentCheckedField.PAN))
        );
        assertNotNull(query);
        assertEquals(REFUNDS_SUM_QUERY_BY_PAN_ROOT_WITH_MINIMAL_DATASET, query);
    }

    @Test
    public void getRefundsSumQueryByPanRootWithUsualDatasetTest() {
        String query = getRefundsSumQuery(
                DgraphEntity.TOKEN,
                createTestUsualDgraphEntityMap(DgraphEntity.TOKEN, PaymentCheckedField.PAN)
        );
        assertNotNull(query);
        assertEquals(REFUNDS_SUM_QUERY_BY_PAN_ROOT_WITH_USUAL_DATASET, query);
    }

    @Test
    public void getRefundsSumQueryByPanRootWithFullDatasetTest() {
        String query = getRefundsSumQuery(DgraphEntity.TOKEN, createTestFullDgraphEntityMap());
        assertNotNull(query);
        assertEquals(REFUNDS_SUM_QUERY_BY_PAN_ROOT_WITH_FULL_DATASET, query);
    }

    @Test
    public void getRefundsSumQueryByBinRootWithMinimalDataTest() {
        String query = getRefundsSumQuery(
                DgraphEntity.BIN,
                Map.of(DgraphEntity.BIN, Set.of(PaymentCheckedField.BIN))
        );
        assertNotNull(query);
        assertEquals(REFUNDS_SUM_QUERY_BY_BIN_ROOT_WITH_MINIMAL_DATASET, query);
    }

    @Test
    public void getRefundsSumQueryByBinRootWithUsualDatasetTest() {
        String query = getRefundsSumQuery(
                DgraphEntity.BIN,
                createTestUsualDgraphEntityMap(DgraphEntity.BIN, PaymentCheckedField.BIN)
        );
        assertNotNull(query);
        assertEquals(REFUNDS_SUM_QUERY_BY_BIN_ROOT_WITH_USUAL_DATASET, query);
    }

    @Test
    public void getRefundsSumQueryByBinRootWithFullDatasetTest() {
        String query = getRefundsSumQuery(DgraphEntity.BIN, createTestFullDgraphEntityMap());
        assertNotNull(query);
        assertEquals(REFUNDS_SUM_QUERY_BY_BIN_ROOT_WITH_FULL_DATASET, query);
    }

    @Test
    public void getRefundsSumQueryByCurrencyRootWithMinimalDataTest() {
        String query = getRefundsSumQuery(
                DgraphEntity.CURRENCY,
                Map.of(DgraphEntity.CURRENCY, Set.of(PaymentCheckedField.CURRENCY))
        );
        assertNotNull(query);
        assertEquals(REFUNDS_SUM_QUERY_BY_CURRENCY_ROOT_WITH_MINIMAL_DATASET, query);
    }

    @Test
    public void getRefundsSumQueryByCurrencyRootWithUsualDatasetTest() {
        String query = getRefundsSumQuery(
                DgraphEntity.CURRENCY,
                createTestUsualDgraphEntityMap(DgraphEntity.CURRENCY, PaymentCheckedField.CURRENCY)
        );
        assertNotNull(query);
        assertEquals(REFUNDS_SUM_QUERY_BY_CURRENCY_ROOT_WITH_USUAL_DATASET, query);
    }

    @Test
    public void getRefundsSumQueryByCurrencyRootWithFullDatasetTest() {
        var dgraphEntityMap = createTestFullDgraphEntityMap();
        dgraphEntityMap.put(DgraphEntity.CURRENCY, Set.of(PaymentCheckedField.CURRENCY));
        String query = getRefundsSumQuery(DgraphEntity.CURRENCY, dgraphEntityMap);
        assertNotNull(query);
        assertEquals(REFUNDS_SUM_QUERY_BY_CURRENCY_ROOT_WITH_FULL_DATASET, query);
    }

    @Test
    public void getRefundsSumQueryByShopIdRootWithMinimalDataTest() {
        String query = getRefundsSumQuery(
                DgraphEntity.SHOP,
                Map.of(DgraphEntity.SHOP, Set.of(PaymentCheckedField.SHOP_ID))
        );
        assertNotNull(query);
        assertEquals(REFUNDS_SUM_QUERY_BY_SHOP_ID_ROOT_WITH_MINIMAL_DATASET, query);
    }

    @Test
    public void getRefundsSumQueryByShopIdRootWithUsualDatasetTest() {
        String query = getRefundsSumQuery(
                DgraphEntity.SHOP,
                createTestUsualDgraphEntityMap(DgraphEntity.BIN, PaymentCheckedField.BIN)
        );
        assertNotNull(query);
        assertEquals(REFUNDS_SUM_QUERY_BY_SHOP_ID_ROOT_WITH_USUAL_DATASET, query);
    }

    @Test
    public void getRefundsSumQueryByShopIdRootWithFullDatasetTest() {
        String query = getRefundsSumQuery(DgraphEntity.SHOP, createTestFullDgraphEntityMap());
        assertNotNull(query);
        assertEquals(REFUNDS_SUM_QUERY_BY_SHOP_ID_ROOT_WITH_FULL_DATASET, query);
    }

    @Test
    public void getRefundsSumQueryByPartyIdRootWithMinimalDataTest() {
        String query = getRefundsSumQuery(
                DgraphEntity.PARTY,
                Map.of(DgraphEntity.PARTY, Set.of(PaymentCheckedField.PARTY_ID))
        );
        assertNotNull(query);
        assertEquals(REFUNDS_SUM_QUERY_BY_PARTY_ID_ROOT_WITH_MINIMAL_DATASET, query);
    }

    @Test
    public void getRefundsSumQueryByPartyIdRootWithUsualDatasetTest() {
        String query = getRefundsSumQuery(
                DgraphEntity.PARTY,
                Map.of(
                        DgraphEntity.PARTY, Set.of(PaymentCheckedField.PARTY_ID),
                        DgraphEntity.BIN, Set.of(PaymentCheckedField.BIN)
                )
        );
        assertNotNull(query);
        assertEquals(REFUNDS_SUM_QUERY_BY_PARTY_ID_ROOT_WITH_USUAL_DATASET, query);
    }

    @Test
    public void getRefundsSumQueryByPartyIdRootWithFullDatasetTest() {
        String query = getRefundsSumQuery(DgraphEntity.PARTY, createTestFullDgraphEntityMap());
        assertNotNull(query);
        assertEquals(REFUNDS_SUM_QUERY_BY_PARTY_ID_ROOT_WITH_FULL_DATASET, query);
    }

    @Test
    public void getRefundsSumQueryByMobileRootWithMinimalDataTest() {
        String query = getRefundsSumQuery(
                DgraphEntity.PAYMENT,
                Map.of(DgraphEntity.PAYMENT, Set.of(PaymentCheckedField.MOBILE))
        );
        assertNotNull(query);
        assertEquals(REFUNDS_SUM_QUERY_BY_MOBILE_ROOT_WITH_MINIMAL_DATASET, query);
    }

    @Test
    public void getRefundsSumQueryByMobileRootWithUsualDatasetTest() {
        String query = getRefundsSumQuery(
                DgraphEntity.PAYMENT,
                createTestUsualDgraphEntityMap(DgraphEntity.PAYMENT, PaymentCheckedField.MOBILE)
        );
        assertNotNull(query);
        assertEquals(REFUNDS_SUM_QUERY_BY_MOBILE_ROOT_WITH_USUAL_DATASET, query);
    }

    @Test
    public void getRefundsSumQueryByMobileRootWithFullDatasetTest() {
        String query = getRefundsSumQuery(DgraphEntity.PAYMENT, createTestFullDgraphEntityMap());
        assertNotNull(query);
        assertEquals(REFUNDS_SUM_QUERY_BY_MOBILE_ROOT_WITH_FULL_DATASET, query);
    }

    @Test
    public void getRefundsSumQueryByRecurrentRootWithMinimalDataTest() {
        String query = getRefundsSumQuery(
                DgraphEntity.PAYMENT,
                Map.of(DgraphEntity.PAYMENT, Set.of(PaymentCheckedField.RECURRENT))
        );
        assertNotNull(query);
        assertEquals(REFUNDS_SUM_QUERY_BY_RECURRENT_ROOT_WITH_MINIMAL_DATASET, query);
    }

    @Test
    public void getRefundsSumQueryByRecurrentRootWithUsualDatasetTest() {
        String query = getRefundsSumQuery(
                DgraphEntity.PAYMENT,
                createTestUsualDgraphEntityMap(DgraphEntity.PAYMENT, PaymentCheckedField.RECURRENT)
        );
        assertNotNull(query);
        assertEquals(REFUNDS_SUM_QUERY_BY_RECURRENT_ROOT_WITH_USUAL_DATASET, query);
    }

    @Test
    public void getRefundsSumQueryByRecurrentRootWithFullDatasetTest() {
        String query = getRefundsSumQuery(DgraphEntity.PAYMENT, createTestFullDgraphEntityMap());
        assertNotNull(query);
        assertEquals(REFUNDS_SUM_QUERY_BY_RECURRENT_ROOT_WITH_FULL_DATASET, query);
    }

    private String getRefundsSumQuery(DgraphEntity rootEntity,
                                        Map<DgraphEntity, Set<PaymentCheckedField>> dgraphEntitySetMap) {
        return aggregationQueryBuilderService.getSumQuery(
                rootEntity,
                DgraphEntity.REFUND,
                dgraphEntitySetMap,
                createTestPaymentModel(),
                Instant.parse("2021-10-28T19:40:54.000000Z"),
                Instant.parse("2021-10-28T19:47:54.000000Z"),
                "successful"
        );
    }

}
