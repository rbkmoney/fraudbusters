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

import static com.rbkmoney.fraudbusters.dgraph.aggregates.data.DgraphUniqueQueryBuilderServiceTestData.*;
import static com.rbkmoney.fraudbusters.util.DgraphTestAggregationUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DgraphUniqueQueryBuilderServiceTest {

    private TemplateService templateService = new TemplateServiceImpl(new TemplateConfig().velocityEngine());

    private DgraphAggregationQueryBuilderService aggregationQueryBuilderService =
            new DgraphAggregationQueryBuilderService(
                    new DgraphEntityResolver(),
                    new DgraphQueryConditionResolver(),
                    templateService
            );

    @Test
    public void getUniqueEmailsByEmailQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.EMAIL,
                DgraphEntity.EMAIL,
                Map.of(DgraphEntity.EMAIL, Set.of(PaymentCheckedField.EMAIL))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_EMAILS_BY_EMAIL_TEST_QUERY, query);
    }

    @Test
    public void getUniqueEmailsByIpQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.IP,
                DgraphEntity.EMAIL,
                Map.of(DgraphEntity.IP, Set.of(PaymentCheckedField.IP))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_EMAILS_BY_IP_TEST_QUERY, query);
    }

    @Test
    public void getUniqueEmailsByFingerprintQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.FINGERPRINT,
                DgraphEntity.EMAIL,
                Map.of(DgraphEntity.FINGERPRINT, Set.of(PaymentCheckedField.FINGERPRINT))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_EMAILS_BY_FINGERPRINT_TEST_QUERY, query);
    }

    @Test
    public void getUniqueEmailsByCountryBankQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.COUNTRY,
                DgraphEntity.EMAIL,
                Map.of(DgraphEntity.COUNTRY, Set.of(PaymentCheckedField.COUNTRY_BANK))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_EMAILS_BY_COUNTRY_BANK_TEST_QUERY, query);
    }

    @Test
    public void getUniqueEmailsByCountryIpQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.IP,
                DgraphEntity.EMAIL,
                Map.of(
                        DgraphEntity.IP, Set.of(PaymentCheckedField.COUNTRY_IP)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_EMAILS_BY_COUNTRY_IP_TEST_QUERY, query);
    }

    @Test
    public void getUniqueEmailsByBinQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.BIN,
                DgraphEntity.EMAIL,
                Map.of(
                        DgraphEntity.FINGERPRINT, Set.of(PaymentCheckedField.FINGERPRINT),
                        DgraphEntity.BIN, Set.of(PaymentCheckedField.BIN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_EMAILS_BY_BIN_TEST_QUERY, query);
    }

    @Test
    public void getUniqueEmailsByPanQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.TOKEN,
                DgraphEntity.EMAIL,
                Map.of(
                        DgraphEntity.FINGERPRINT, Set.of(PaymentCheckedField.FINGERPRINT),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.PAN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_EMAILS_BY_PAN_TEST_QUERY, query);
    }

    @Test
    public void getUniqueEmailsByCurrencyQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.CURRENCY,
                DgraphEntity.EMAIL,
                Map.of(
                        DgraphEntity.CURRENCY, Set.of(PaymentCheckedField.CURRENCY),
                        DgraphEntity.FINGERPRINT, Set.of(PaymentCheckedField.FINGERPRINT)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_EMAILS_BY_CURRENCY_TEST_QUERY, query);
    }

    @Test
    public void getUniqueEmailsByShopQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.SHOP,
                DgraphEntity.EMAIL,
                Map.of(
                        DgraphEntity.SHOP, Set.of(PaymentCheckedField.SHOP_ID),
                        DgraphEntity.FINGERPRINT, Set.of(PaymentCheckedField.FINGERPRINT)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_EMAILS_BY_SHOP_TEST_QUERY, query);
    }

    @Test
    public void getUniqueEmailsByPartyQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.PARTY,
                DgraphEntity.EMAIL,
                Map.of(
                        DgraphEntity.PARTY, Set.of(PaymentCheckedField.PARTY_ID),
                        DgraphEntity.FINGERPRINT, Set.of(PaymentCheckedField.FINGERPRINT)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_EMAILS_BY_PARTY_TEST_QUERY, query);
    }

    @Test
    public void getUniqueEmailsByMobileQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.PAYMENT,
                DgraphEntity.EMAIL,
                Map.of(
                        DgraphEntity.PAYMENT, Set.of(PaymentCheckedField.MOBILE),
                        DgraphEntity.FINGERPRINT, Set.of(PaymentCheckedField.FINGERPRINT)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_EMAILS_BY_MOBILE_TEST_QUERY, query);
    }

    @Test
    public void getUniqueEmailsByRecurrentQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.PAYMENT,
                DgraphEntity.EMAIL,
                Map.of(
                        DgraphEntity.PAYMENT, Set.of(PaymentCheckedField.RECURRENT),
                        DgraphEntity.FINGERPRINT, Set.of(PaymentCheckedField.FINGERPRINT)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_EMAILS_BY_RECURRENT_TEST_QUERY, query);
    }

    @Test
    public void getUniqueEmailsByCardTokenQueryTest() {

    }

    @Test
    public void getUniqueIpsByEmailQueryTest() {

    }

    @Test
    public void getUniqueIpsByIpQueryTest() {

    }

    @Test
    public void getUniqueIpsByFingerprintQueryTest() {

    }

    @Test
    public void getUniqueIpsByCountryBankQueryTest() {

    }

    @Test
    public void getUniqueIpsByCountryIpQueryTest() {

    }

    @Test
    public void getUniqueIpsByBinQueryTest() {

    }

    @Test
    public void getUniqueIpsByPanQueryTest() {

    }

    @Test
    public void getUniqueIpsByCurrencyQueryTest() {

    }

    @Test
    public void getUniqueIpsByShopQueryTest() {

    }

    @Test
    public void getUniqueIpsByPartyQueryTest() {

    }

    @Test
    public void getUniqueIpsByMobileQueryTest() {

    }

    @Test
    public void getUniqueIpsByRecurrentQueryTest() {

    }

    @Test
    public void getUniqueIpsByCardTokenQueryTest() {

    }

    @Test
    public void getUniqueFingerprintsByEmailQueryTest() {

    }

    @Test
    public void getUniqueFingerprintsByIpQueryTest() {

    }

    @Test
    public void getUniqueFingerprintsByFingerprintQueryTest() {

    }

    @Test
    public void getUniqueFingerprintsByCountryBankQueryTest() {

    }

    @Test
    public void getUniqueFingerprintsByCountryIpQueryTest() {

    }

    @Test
    public void getUniqueFingerprintsByBinQueryTest() {

    }

    @Test
    public void getUniqueFingerprintsByPanQueryTest() {

    }

    @Test
    public void getUniqueFingerprintsByCurrencyQueryTest() {

    }

    @Test
    public void getUniqueFingerprintsByShopQueryTest() {

    }

    @Test
    public void getUniqueFingerprintsByPartyQueryTest() {

    }

    @Test
    public void getUniqueFingerprintsByMobileQueryTest() {

    }

    @Test
    public void getUniqueFingerprintsByRecurrentQueryTest() {

    }

    @Test
    public void getUniqueFingerprintsByCardTokenQueryTest() {

    }

    @Test
    public void getUniqueCountryBanksByEmailQueryTest() {

    }

    @Test
    public void getUniqueCountryBanksByIpQueryTest() {

    }

    @Test
    public void getUniqueCountryBanksByFingerprintQueryTest() {

    }

    @Test
    public void getUniqueCountryBanksByCountryBankQueryTest() {

    }

    @Test
    public void getUniqueCountryBanksByCountryIpQueryTest() {

    }

    @Test
    public void getUniqueCountryBanksByBinQueryTest() {

    }

    @Test
    public void getUniqueCountryBanksByPanQueryTest() {

    }

    @Test
    public void getUniqueCountryBanksByCurrencyQueryTest() {

    }

    @Test
    public void getUniqueCountryBanksByShopQueryTest() {

    }

    @Test
    public void getUniqueCountryBanksByPartyQueryTest() {

    }

    @Test
    public void getUniqueCountryBanksByMobileQueryTest() {

    }

    @Test
    public void getUniqueCountryBanksByRecurrentQueryTest() {

    }

    @Test
    public void getUniqueCountryBanksByCardTokenQueryTest() {

    }

    @Test
    public void getUniqueCountryIpsByEmailQueryTest() {

    }

    @Test
    public void getUniqueCountryIpsByIpQueryTest() {

    }

    @Test
    public void getUniqueCountryIpsByFingerprintQueryTest() {

    }

    @Test
    public void getUniqueCountryIpsByCountryBankQueryTest() {

    }

    @Test
    public void getUniqueCountryIpsByCountryIpQueryTest() {

    }

    @Test
    public void getUniqueCountryIpsByBinQueryTest() {

    }

    @Test
    public void getUniqueCountryIpsByPanQueryTest() {

    }

    @Test
    public void getUniqueCountryIpsByCurrencyQueryTest() {

    }

    @Test
    public void getUniqueCountryIpsByShopQueryTest() {

    }

    @Test
    public void getUniqueCountryIpsByPartyQueryTest() {

    }

    @Test
    public void getUniqueCountryIpsByMobileQueryTest() {

    }

    @Test
    public void getUniqueCountryIpsByRecurrentQueryTest() {

    }

    @Test
    public void getUniqueCountryIpsByCardTokenQueryTest() {

    }

    @Test
    public void getUniqueBinsIpsByEmailQueryTest() {

    }

    @Test
    public void getUniqueBinsIpsByIpQueryTest() {

    }

    @Test
    public void getUniqueBinsIpsByFingerprintQueryTest() {

    }

    @Test
    public void getUniqueBinsIpsByCountryBankQueryTest() {

    }

    @Test
    public void getUniqueBinsIpsByCountryIpQueryTest() {

    }

    @Test
    public void getUniqueBinsIpsByBinQueryTest() {

    }

    @Test
    public void getUniqueBinsIpsByPanQueryTest() {

    }

    @Test
    public void getUniqueBinsIpsByCurrencyQueryTest() {

    }

    @Test
    public void getUniqueBinsIpsByShopQueryTest() {

    }

    @Test
    public void getUniqueBinsIpsByPartyQueryTest() {

    }

    @Test
    public void getUniqueBinsIpsByMobileQueryTest() {

    }

    @Test
    public void getUniqueBinsIpsByRecurrentQueryTest() {

    }

    @Test
    public void getUniqueBinsIpsByCardTokenQueryTest() {

    }

    @Test
    public void getUniquePansIpsByEmailQueryTest() {

    }

    @Test
    public void getUniquePansIpsByIpQueryTest() {

    }

    @Test
    public void getUniquePansIpsByFingerprintQueryTest() {

    }

    @Test
    public void getUniquePansIpsByCountryBankQueryTest() {

    }

    @Test
    public void getUniquePansIpsByCountryIpQueryTest() {

    }

    @Test
    public void getUniquePansIpsByBinQueryTest() {

    }

    @Test
    public void getUniquePansIpsByPanQueryTest() {

    }

    @Test
    public void getUniquePansIpsByCurrencyQueryTest() {

    }

    @Test
    public void getUniquePansIpsByShopQueryTest() {

    }

    @Test
    public void getUniquePansIpsByPartyQueryTest() {

    }

    @Test
    public void getUniquePansIpsByMobileQueryTest() {

    }

    @Test
    public void getUniquePansIpsByRecurrentQueryTest() {

    }

    @Test
    public void getUniquePansIpsByCardTokenQueryTest() {

    }

    @Test
    public void getUniqueCurrensiesIpsByEmailQueryTest() {

    }

    @Test
    public void getUniqueCurrensiesIpsByIpQueryTest() {

    }

    @Test
    public void getUniqueCurrensiesIpsByFingerprintQueryTest() {

    }

    @Test
    public void getUniqueCurrensiesIpsByCountryBankQueryTest() {

    }

    @Test
    public void getUniqueCurrensiesIpsByCountryIpQueryTest() {

    }

    @Test
    public void getUniqueCurrensiesIpsByBinQueryTest() {

    }

    @Test
    public void getUniqueCurrensiesIpsByPanQueryTest() {

    }

    @Test
    public void getUniqueCurrensiesIpsByCurrencyQueryTest() {

    }

    @Test
    public void getUniqueCurrensiesIpsByShopQueryTest() {

    }

    @Test
    public void getUniqueCurrensiesIpsByPartyQueryTest() {

    }

    @Test
    public void getUniqueCurrensiesIpsByMobileQueryTest() {

    }

    @Test
    public void getUniqueCurrensiesIpsByRecurrentQueryTest() {

    }

    @Test
    public void getUniqueCurrensiesIpsByCardTokenQueryTest() {

    }

    @Test
    public void getUniqueShopsIpsByEmailQueryTest() {

    }

    @Test
    public void getUniqueShopsIpsByIpQueryTest() {

    }

    @Test
    public void getUniqueShopsIpsByFingerprintQueryTest() {

    }

    @Test
    public void getUniqueShopsIpsByCountryBankQueryTest() {

    }

    @Test
    public void getUniqueShopsIpsByCountryIpQueryTest() {

    }

    @Test
    public void getUniqueShopsIpsByBinQueryTest() {

    }

    @Test
    public void getUniqueShopsIpsByPanQueryTest() {

    }

    @Test
    public void getUniqueShopsIpsByCurrencyQueryTest() {

    }

    @Test
    public void getUniqueShopsIpsByShopQueryTest() {

    }

    @Test
    public void getUniqueShopsIpsByPartyQueryTest() {

    }

    @Test
    public void getUniqueShopsIpsByMobileQueryTest() {

    }

    @Test
    public void getUniqueShopsIpsByRecurrentQueryTest() {

    }

    @Test
    public void getUniqueShopsIpsByCardTokenQueryTest() {

    }

    @Test
    public void getUniquePartiesIpsByEmailQueryTest() {

    }

    @Test
    public void getUniquePartiesIpsByIpQueryTest() {

    }

    @Test
    public void getUniquePartiesIpsByFingerprintQueryTest() {

    }

    @Test
    public void getUniquePartiesIpsByCountryBankQueryTest() {

    }

    @Test
    public void getUniquePartiesIpsByCountryIpQueryTest() {

    }

    @Test
    public void getUniquePartiesIpsByBinQueryTest() {

    }

    @Test
    public void getUniquePartiesIpsByPanQueryTest() {

    }

    @Test
    public void getUniquePartiesIpsByCurrencyQueryTest() {

    }

    @Test
    public void getUniquePartiesIpsByShopQueryTest() {

    }

    @Test
    public void getUniquePartiesIpsByPartyQueryTest() {

    }

    @Test
    public void getUniquePartiesIpsByMobileQueryTest() {

    }

    @Test
    public void getUniquePartiesIpsByRecurrentQueryTest() {

    }

    @Test
    public void getUniquePartiesIpsByCardTokenQueryTest() {

    }

    @Test
    public void getUniqueMobilesIpsByEmailQueryTest() {

    }

    @Test
    public void getUniqueMobilesIpsByIpQueryTest() {

    }

    @Test
    public void getUniqueMobilesIpsByFingerprintQueryTest() {

    }

    @Test
    public void getUniqueMobilesIpsByCountryBankQueryTest() {

    }

    @Test
    public void getUniqueMobilesIpsByCountryIpQueryTest() {

    }

    @Test
    public void getUniqueMobilesIpsByBinQueryTest() {

    }

    @Test
    public void getUniqueMobilesIpsByPanQueryTest() {

    }

    @Test
    public void getUniqueMobilesIpsByCurrencyQueryTest() {

    }

    @Test
    public void getUniqueMobilesIpsByShopQueryTest() {

    }

    @Test
    public void getUniqueMobilesIpsByPartyQueryTest() {

    }

    @Test
    public void getUniqueMobilesIpsByMobileQueryTest() {

    }

    @Test
    public void getUniqueMobilesIpsByRecurrentQueryTest() {

    }

    @Test
    public void getUniqueMobilesIpsByCardTokenQueryTest() {

    }

    @Test
    public void getUniqueRecurrentsIpsByEmailQueryTest() {

    }

    @Test
    public void getUniqueRecurrentsIpsByIpQueryTest() {

    }

    @Test
    public void getUniqueRecurrentsIpsByFingerprintQueryTest() {

    }

    @Test
    public void getUniqueRecurrentsIpsByCountryBankQueryTest() {

    }

    @Test
    public void getUniqueRecurrentsIpsByCountryIpQueryTest() {

    }

    @Test
    public void getUniqueRecurrentsIpsByBinQueryTest() {

    }

    @Test
    public void getUniqueRecurrentsIpsByPanQueryTest() {

    }

    @Test
    public void getUniqueRecurrentsIpsByCurrencyQueryTest() {

    }

    @Test
    public void getUniqueRecurrentsIpsByShopQueryTest() {

    }

    @Test
    public void getUniqueRecurrentsIpsByPartyQueryTest() {

    }

    @Test
    public void getUniqueRecurrentsIpsByMobileQueryTest() {

    }

    @Test
    public void getUniqueRecurrentsIpsByRecurrentQueryTest() {

    }

    @Test
    public void getUniqueRecurrentsIpsByCardTokenQueryTest() {

    }

    @Test
    public void getUniqueCardTokensIpsByEmailQueryTest() {

    }

    @Test
    public void getUniqueCardTokensIpsByIpQueryTest() {

    }

    @Test
    public void getUniqueCardTokensIpsByFingerprintQueryTest() {

    }

    @Test
    public void getUniqueCardTokensIpsByCountryBankQueryTest() {

    }

    @Test
    public void getUniqueCardTokensIpsByCountryIpQueryTest() {

    }

    @Test
    public void getUniqueCardTokensIpsByBinQueryTest() {

    }

    @Test
    public void getUniqueCardTokensIpsByPanQueryTest() {

    }

    @Test
    public void getUniqueCardTokensIpsByCurrencyQueryTest() {

    }

    @Test
    public void getUniqueCardTokensIpsByShopQueryTest() {

    }

    @Test
    public void getUniqueCardTokensIpsByPartyQueryTest() {

    }

    @Test
    public void getUniqueCardTokensIpsByMobileQueryTest() {

    }

    @Test
    public void getUniqueCardTokensIpsByRecurrentQueryTest() {

    }

    @Test
    public void getUniqueCardTokensIpsByCardTokenQueryTest() {

    }

    private String getUniqueQuery(DgraphEntity rootEntity,
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
