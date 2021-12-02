package com.rbkmoney.fraudbusters.dgraph.service.query;

import com.rbkmoney.fraudbusters.constant.PaymentStatus;
import com.rbkmoney.fraudbusters.fraud.constant.DgraphEntity;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

import static com.rbkmoney.fraudbusters.dgraph.service.data.DgraphUniqueQueryBuilderServiceTestData.*;
import static com.rbkmoney.fraudbusters.util.DgraphTestAggregationUtils.createTestPaymentModel;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DgraphUniqueQueryBuilderServiceTest extends AbstractDgraphQueryBuilderServiceTest {

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
        String query = getUniqueQuery(
                DgraphEntity.TOKEN,
                DgraphEntity.EMAIL,
                Map.of(
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN),
                        DgraphEntity.FINGERPRINT, Set.of(PaymentCheckedField.FINGERPRINT)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_EMAILS_BY_CARD_TOKEN_TEST_QUERY, query);
    }

    @Test
    public void getUniqueIpsByEmailQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.EMAIL,
                DgraphEntity.IP,
                Map.of(
                        DgraphEntity.EMAIL, Set.of(PaymentCheckedField.EMAIL),
                        DgraphEntity.FINGERPRINT, Set.of(PaymentCheckedField.FINGERPRINT)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_IPS_BY_EMAIL_TEST_QUERY, query);
    }

    @Test
    public void getUniqueIpsByIpQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.IP,
                DgraphEntity.IP,
                Map.of(
                        DgraphEntity.IP, Set.of(PaymentCheckedField.IP),
                        DgraphEntity.FINGERPRINT, Set.of(PaymentCheckedField.FINGERPRINT)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_IPS_BY_IP_TEST_QUERY, query);
    }

    @Test
    public void getUniqueIpsByFingerprintQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.FINGERPRINT,
                DgraphEntity.IP,
                Map.of(
                        DgraphEntity.EMAIL, Set.of(PaymentCheckedField.EMAIL),
                        DgraphEntity.FINGERPRINT, Set.of(PaymentCheckedField.FINGERPRINT)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_IPS_BY_FINGERPRINT_TEST_QUERY, query);
    }

    @Test
    public void getUniqueIpsByCountryBankQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.COUNTRY,
                DgraphEntity.IP,
                Map.of(
                        DgraphEntity.COUNTRY, Set.of(PaymentCheckedField.COUNTRY_BANK),
                        DgraphEntity.FINGERPRINT, Set.of(PaymentCheckedField.FINGERPRINT)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_IPS_BY_COUNTRY_BANK_TEST_QUERY, query);
    }

    @Test
    public void getUniqueIpsByCountryIpQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.IP,
                DgraphEntity.IP,
                Map.of(
                        DgraphEntity.IP, Set.of(PaymentCheckedField.COUNTRY_IP),
                        DgraphEntity.FINGERPRINT, Set.of(PaymentCheckedField.FINGERPRINT)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_IPS_BY_COUNTRY_IP_TEST_QUERY, query);
    }

    @Test
    public void getUniqueIpsByBinQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.BIN,
                DgraphEntity.IP,
                Map.of(
                        DgraphEntity.BIN, Set.of(PaymentCheckedField.BIN),
                        DgraphEntity.FINGERPRINT, Set.of(PaymentCheckedField.FINGERPRINT)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_IPS_BY_BIN_TEST_QUERY, query);
    }

    @Test
    public void getUniqueIpsByPanQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.TOKEN,
                DgraphEntity.IP,
                Map.of(
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.PAN),
                        DgraphEntity.FINGERPRINT, Set.of(PaymentCheckedField.FINGERPRINT)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_IPS_BY_PAN_TEST_QUERY, query);
    }

    @Test
    public void getUniqueIpsByCurrencyQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.CURRENCY,
                DgraphEntity.IP,
                Map.of(
                        DgraphEntity.CURRENCY, Set.of(PaymentCheckedField.CURRENCY),
                        DgraphEntity.FINGERPRINT, Set.of(PaymentCheckedField.FINGERPRINT)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_IPS_BY_CURRENCY_TEST_QUERY, query);
    }

    @Test
    public void getUniqueIpsByShopQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.SHOP,
                DgraphEntity.IP,
                Map.of(
                        DgraphEntity.SHOP, Set.of(PaymentCheckedField.SHOP_ID),
                        DgraphEntity.FINGERPRINT, Set.of(PaymentCheckedField.FINGERPRINT)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_IPS_BY_SHOP_TEST_QUERY, query);
    }

    @Test
    public void getUniqueIpsByPartyQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.PARTY,
                DgraphEntity.IP,
                Map.of(
                        DgraphEntity.PARTY, Set.of(PaymentCheckedField.PARTY_ID),
                        DgraphEntity.FINGERPRINT, Set.of(PaymentCheckedField.FINGERPRINT)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_IPS_BY_PARTY_TEST_QUERY, query);
    }

    @Test
    public void getUniqueIpsByMobileQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.PAYMENT,
                DgraphEntity.IP,
                Map.of(
                        DgraphEntity.PAYMENT, Set.of(PaymentCheckedField.MOBILE),
                        DgraphEntity.FINGERPRINT, Set.of(PaymentCheckedField.FINGERPRINT)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_IPS_BY_MOBILE_TEST_QUERY, query);
    }

    @Test
    public void getUniqueIpsByRecurrentQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.PAYMENT,
                DgraphEntity.IP,
                Map.of(
                        DgraphEntity.PAYMENT, Set.of(PaymentCheckedField.RECURRENT),
                        DgraphEntity.FINGERPRINT, Set.of(PaymentCheckedField.FINGERPRINT)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_IPS_BY_RECURRENT_TEST_QUERY, query);
    }

    @Test
    public void getUniqueIpsByCardTokenQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.TOKEN,
                DgraphEntity.IP,
                Map.of(
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN),
                        DgraphEntity.FINGERPRINT, Set.of(PaymentCheckedField.FINGERPRINT)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_IPS_BY_CARD_TOKEN_TEST_QUERY, query);
    }

    @Test
    public void getUniqueFingerprintsByEmailQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.EMAIL,
                DgraphEntity.FINGERPRINT,
                Map.of(
                        DgraphEntity.EMAIL, Set.of(PaymentCheckedField.EMAIL),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_FINGERPRINTS_BY_EMAIL_TEST_QUERY, query);
    }

    @Test
    public void getUniqueFingerprintsByIpQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.IP,
                DgraphEntity.FINGERPRINT,
                Map.of(
                        DgraphEntity.IP, Set.of(PaymentCheckedField.IP),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_FINGERPRINTS_BY_IP_TEST_QUERY, query);
    }

    @Test
    public void getUniqueFingerprintsByFingerprintQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.FINGERPRINT,
                DgraphEntity.FINGERPRINT,
                Map.of(
                        DgraphEntity.FINGERPRINT, Set.of(PaymentCheckedField.FINGERPRINT),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_FINGERPRINTS_BY_FINGERPRINT_TEST_QUERY, query);
    }

    @Test
    public void getUniqueFingerprintsByCountryBankQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.COUNTRY,
                DgraphEntity.FINGERPRINT,
                Map.of(
                        DgraphEntity.COUNTRY, Set.of(PaymentCheckedField.COUNTRY_BANK),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_FINGERPRINTS_BY_COUNTRY_BANK_TEST_QUERY, query);
    }

    @Test
    public void getUniqueFingerprintsByCountryIpQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.IP,
                DgraphEntity.FINGERPRINT,
                Map.of(
                        DgraphEntity.IP, Set.of(PaymentCheckedField.COUNTRY_IP),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_FINGERPRINTS_BY_COUNTRY_IP_TEST_QUERY, query);
    }

    @Test
    public void getUniqueFingerprintsByBinQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.BIN,
                DgraphEntity.FINGERPRINT,
                Map.of(
                        DgraphEntity.BIN, Set.of(PaymentCheckedField.BIN),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_FINGERPRINTS_BY_BIN_TEST_QUERY, query);
    }

    @Test
    public void getUniqueFingerprintsByPanQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.TOKEN,
                DgraphEntity.FINGERPRINT,
                Map.of(DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN, PaymentCheckedField.PAN))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_FINGERPRINTS_BY_PAN_TEST_QUERY, query);
    }

    @Test
    public void getUniqueFingerprintsByCurrencyQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.CURRENCY,
                DgraphEntity.FINGERPRINT,
                Map.of(
                        DgraphEntity.CURRENCY, Set.of(PaymentCheckedField.CURRENCY),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_FINGERPRINTS_BY_CURRENCY_TEST_QUERY, query);
    }

    @Test
    public void getUniqueFingerprintsByShopQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.SHOP,
                DgraphEntity.FINGERPRINT,
                Map.of(
                        DgraphEntity.SHOP, Set.of(PaymentCheckedField.SHOP_ID),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_FINGERPRINTS_BY_SHOP_TEST_QUERY, query);
    }

    @Test
    public void getUniqueFingerprintsByPartyQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.PARTY,
                DgraphEntity.FINGERPRINT,
                Map.of(
                        DgraphEntity.PARTY, Set.of(PaymentCheckedField.PARTY_ID),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_FINGERPRINTS_BY_PARTY_TEST_QUERY, query);
    }

    @Test
    public void getUniqueFingerprintsByMobileQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.PAYMENT,
                DgraphEntity.FINGERPRINT,
                Map.of(
                        DgraphEntity.PAYMENT, Set.of(PaymentCheckedField.MOBILE),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_FINGERPRINTS_BY_MOBILE_TEST_QUERY, query);
    }

    @Test
    public void getUniqueFingerprintsByRecurrentQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.PAYMENT,
                DgraphEntity.FINGERPRINT,
                Map.of(
                        DgraphEntity.PAYMENT, Set.of(PaymentCheckedField.RECURRENT),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_FINGERPRINTS_BY_RECURRENT_TEST_QUERY, query);
    }

    @Test
    public void getUniqueFingerprintsByCardTokenQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.TOKEN,
                DgraphEntity.FINGERPRINT,
                Map.of(
                        DgraphEntity.EMAIL, Set.of(PaymentCheckedField.EMAIL),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_FINGERPRINTS_BY_CARD_TOKEN_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCountryBanksByEmailQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.EMAIL,
                DgraphEntity.COUNTRY,
                Map.of(
                        DgraphEntity.EMAIL, Set.of(PaymentCheckedField.EMAIL),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_COUNTRY_BANKS_BY_EMAIL_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCountryBanksByIpQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.IP,
                DgraphEntity.COUNTRY,
                Map.of(
                        DgraphEntity.IP, Set.of(PaymentCheckedField.IP),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_COUNTRY_BANKS_BY_IP_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCountryBanksByFingerprintQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.FINGERPRINT,
                DgraphEntity.COUNTRY,
                Map.of(
                        DgraphEntity.FINGERPRINT, Set.of(PaymentCheckedField.FINGERPRINT),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_COUNTRY_BANKS_BY_FINGERPRINT_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCountryBanksByCountryBankQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.COUNTRY,
                DgraphEntity.COUNTRY,
                Map.of(
                        DgraphEntity.FINGERPRINT, Set.of(PaymentCheckedField.FINGERPRINT),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_COUNTRY_BANKS_BY_COUNTRY_BANK_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCountryBanksByCountryIpQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.IP,
                DgraphEntity.COUNTRY,
                Map.of(
                        DgraphEntity.IP, Set.of(PaymentCheckedField.COUNTRY_IP),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_COUNTRY_BANKS_BY_COUNTRY_IP_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCountryBanksByBinQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.BIN,
                DgraphEntity.COUNTRY,
                Map.of(
                        DgraphEntity.BIN, Set.of(PaymentCheckedField.BIN),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_COUNTRY_BANKS_BY_BIN_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCountryBanksByPanQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.TOKEN,
                DgraphEntity.COUNTRY,
                Map.of(
                        DgraphEntity.BIN, Set.of(PaymentCheckedField.BIN),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN, PaymentCheckedField.PAN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_COUNTRY_BANKS_BY_PAN_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCountryBanksByCurrencyQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.CURRENCY,
                DgraphEntity.COUNTRY,
                Map.of(
                        DgraphEntity.CURRENCY, Set.of(PaymentCheckedField.CURRENCY),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN, PaymentCheckedField.PAN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_COUNTRY_BANKS_BY_CURRENCY_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCountryBanksByShopQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.SHOP,
                DgraphEntity.COUNTRY,
                Map.of(
                        DgraphEntity.SHOP, Set.of(PaymentCheckedField.SHOP_ID),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_COUNTRY_BANKS_BY_SHOP_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCountryBanksByPartyQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.PARTY,
                DgraphEntity.COUNTRY,
                Map.of(
                        DgraphEntity.PARTY, Set.of(PaymentCheckedField.PARTY_ID),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_COUNTRY_BANKS_BY_PARTY_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCountryBanksByMobileQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.PAYMENT,
                DgraphEntity.COUNTRY,
                Map.of(
                        DgraphEntity.PAYMENT, Set.of(PaymentCheckedField.MOBILE),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_COUNTRY_BANKS_BY_MOBILE_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCountryBanksByRecurrentQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.PAYMENT,
                DgraphEntity.COUNTRY,
                Map.of(
                        DgraphEntity.PAYMENT, Set.of(PaymentCheckedField.RECURRENT),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_COUNTRY_BANKS_BY_RECURRENT_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCountryBanksByCardTokenQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.TOKEN,
                DgraphEntity.COUNTRY,
                Map.of(
                        DgraphEntity.PAYMENT, Set.of(PaymentCheckedField.RECURRENT),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_COUNTRY_BANKS_BY_CARD_TOKEN_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCountryIpsByEmailQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.EMAIL,
                DgraphEntity.IP,
                Map.of(
                        DgraphEntity.IP, Set.of(PaymentCheckedField.COUNTRY_IP),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_COUNTRY_IPS_BY_EMAIL_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCountryIpsByIpQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.IP,
                DgraphEntity.IP,
                Map.of(
                        DgraphEntity.IP, Set.of(PaymentCheckedField.COUNTRY_IP),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_COUNTRY_IPS_BY_IP_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCountryIpsByFingerprintQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.FINGERPRINT,
                DgraphEntity.IP,
                Map.of(
                        DgraphEntity.FINGERPRINT, Set.of(PaymentCheckedField.FINGERPRINT),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_COUNTRY_IPS_BY_FINGERPRINT_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCountryIpsByCountryBankQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.COUNTRY,
                DgraphEntity.IP,
                Map.of(
                        DgraphEntity.COUNTRY, Set.of(PaymentCheckedField.COUNTRY_BANK),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_COUNTRY_IPS_BY_COUNTRY_BANK_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCountryIpsByCountryIpQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.IP,
                DgraphEntity.IP,
                Map.of(
                        DgraphEntity.IP, Set.of(PaymentCheckedField.COUNTRY_IP),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_COUNTRY_IPS_BY_COUNTRY_IP_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCountryIpsByBinQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.BIN,
                DgraphEntity.IP,
                Map.of(
                        DgraphEntity.BIN, Set.of(PaymentCheckedField.BIN),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_COUNTRY_IPS_BY_BIN_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCountryIpsByPanQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.TOKEN,
                DgraphEntity.IP,
                Map.of(
                        DgraphEntity.BIN, Set.of(PaymentCheckedField.BIN),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.PAN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_COUNTRY_IPS_BY_PAN_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCountryIpsByCurrencyQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.CURRENCY,
                DgraphEntity.IP,
                Map.of(
                        DgraphEntity.CURRENCY, Set.of(PaymentCheckedField.CURRENCY),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.PAN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_COUNTRY_IPS_BY_CURRENCY_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCountryIpsByShopQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.SHOP,
                DgraphEntity.IP,
                Map.of(
                        DgraphEntity.SHOP, Set.of(PaymentCheckedField.SHOP_ID),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.PAN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_COUNTRY_IPS_BY_SHOP_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCountryIpsByPartyQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.PARTY,
                DgraphEntity.IP,
                Map.of(
                        DgraphEntity.PARTY, Set.of(PaymentCheckedField.PARTY_ID),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.PAN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_COUNTRY_IPS_BY_PARTY_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCountryIpsByMobileQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.PAYMENT,
                DgraphEntity.IP,
                Map.of(
                        DgraphEntity.PAYMENT, Set.of(PaymentCheckedField.MOBILE),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.PAN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_COUNTRY_IPS_BY_MOBILE_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCountryIpsByRecurrentQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.PAYMENT,
                DgraphEntity.IP,
                Map.of(
                        DgraphEntity.PAYMENT, Set.of(PaymentCheckedField.RECURRENT),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.PAN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_COUNTRY_IPS_BY_RECURRENT_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCountryIpsByCardTokenQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.TOKEN,
                DgraphEntity.IP,
                Map.of(
                        DgraphEntity.PAYMENT, Set.of(PaymentCheckedField.RECURRENT),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_COUNTRY_IPS_BY_CARD_TOKEN_TEST_QUERY, query);
    }

    @Test
    public void getUniqueBinsByEmailQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.EMAIL,
                DgraphEntity.BIN,
                Map.of(
                        DgraphEntity.EMAIL, Set.of(PaymentCheckedField.EMAIL),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_BINS_BY_EMAIL_TEST_QUERY, query);
    }

    @Test
    public void getUniqueBinsByIpQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.IP,
                DgraphEntity.BIN,
                Map.of(
                        DgraphEntity.IP, Set.of(PaymentCheckedField.IP),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_BINS_BY_IP_TEST_QUERY, query);
    }

    @Test
    public void getUniqueBinsByFingerprintQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.FINGERPRINT,
                DgraphEntity.BIN,
                Map.of(
                        DgraphEntity.FINGERPRINT, Set.of(PaymentCheckedField.FINGERPRINT),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_BINS_BY_FINGERPRINT_TEST_QUERY, query);
    }

    @Test
    public void getUniqueBinsByCountryBankQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.COUNTRY,
                DgraphEntity.BIN,
                Map.of(
                        DgraphEntity.COUNTRY, Set.of(PaymentCheckedField.COUNTRY_BANK),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_BINS_BY_COUNTRY_BANK_TEST_QUERY, query);
    }

    @Test
    public void getUniqueBinsByCountryIpQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.IP,
                DgraphEntity.BIN,
                Map.of(
                        DgraphEntity.IP, Set.of(PaymentCheckedField.COUNTRY_IP),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_BINS_BY_COUNTRY_IP_TEST_QUERY, query);
    }

    @Test
    public void getUniqueBinsByBinQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.BIN,
                DgraphEntity.BIN,
                Map.of(
                        DgraphEntity.BIN, Set.of(PaymentCheckedField.BIN),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_BINS_BY_BIN_TEST_QUERY, query);
    }

    @Test
    public void getUniqueBinsByPanQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.TOKEN,
                DgraphEntity.BIN,
                Map.of(
                        DgraphEntity.EMAIL, Set.of(PaymentCheckedField.EMAIL),
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.PAN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_BINS_BY_PAN_TEST_QUERY, query);
    }

    @Test
    public void getUniqueBinsByCurrencyQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.CURRENCY,
                DgraphEntity.BIN,
                Map.of(
                        DgraphEntity.EMAIL, Set.of(PaymentCheckedField.EMAIL),
                        DgraphEntity.CURRENCY, Set.of(PaymentCheckedField.CURRENCY)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_BINS_BY_CURRENCY_TEST_QUERY, query);
    }

    @Test
    public void getUniqueBinsByShopQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.SHOP,
                DgraphEntity.BIN,
                Map.of(
                        DgraphEntity.SHOP, Set.of(PaymentCheckedField.SHOP_ID),
                        DgraphEntity.CURRENCY, Set.of(PaymentCheckedField.CURRENCY)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_BINS_BY_SHOP_TEST_QUERY, query);
    }

    @Test
    public void getUniqueBinsByPartyQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.PARTY,
                DgraphEntity.BIN,
                Map.of(
                        DgraphEntity.PARTY, Set.of(PaymentCheckedField.PARTY_ID),
                        DgraphEntity.CURRENCY, Set.of(PaymentCheckedField.CURRENCY)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_BINS_BY_PARTY_TEST_QUERY, query);
    }

    @Test
    public void getUniqueBinsByMobileQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.PAYMENT,
                DgraphEntity.BIN,
                Map.of(
                        DgraphEntity.PAYMENT, Set.of(PaymentCheckedField.MOBILE),
                        DgraphEntity.CURRENCY, Set.of(PaymentCheckedField.CURRENCY)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_BINS_BY_MOBILE_TEST_QUERY, query);
    }

    @Test
    public void getUniqueBinsByRecurrentQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.PAYMENT,
                DgraphEntity.BIN,
                Map.of(
                        DgraphEntity.PAYMENT, Set.of(PaymentCheckedField.RECURRENT),
                        DgraphEntity.CURRENCY, Set.of(PaymentCheckedField.CURRENCY)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_BINS_BY_RECURRENT_TEST_QUERY, query);
    }

    @Test
    public void getUniqueBinsByCardTokenQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.TOKEN,
                DgraphEntity.BIN,
                Map.of(
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN),
                        DgraphEntity.CURRENCY, Set.of(PaymentCheckedField.CURRENCY)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_BINS_BY_CARD_TOKEN_TEST_QUERY, query);
    }

    @Test
    public void getUniquePansByEmailQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.EMAIL,
                DgraphEntity.TOKEN,
                Map.of(
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.PAN),
                        DgraphEntity.CURRENCY, Set.of(PaymentCheckedField.CURRENCY)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_PANS_BY_EMAIL_TEST_QUERY, query);
    }

    @Test
    public void getUniquePansByIpQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.IP,
                DgraphEntity.TOKEN,
                Map.of(
                        DgraphEntity.IP, Set.of(PaymentCheckedField.IP),
                        DgraphEntity.CURRENCY, Set.of(PaymentCheckedField.CURRENCY)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_PANS_BY_IP_TEST_QUERY, query);
    }

    @Test
    public void getUniquePansByFingerprintQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.FINGERPRINT,
                DgraphEntity.TOKEN,
                Map.of(
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.PAN),
                        DgraphEntity.FINGERPRINT, Set.of(PaymentCheckedField.FINGERPRINT)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_PANS_BY_FINGERPRINT_TEST_QUERY, query);
    }

    @Test
    public void getUniquePansByCountryBankQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.COUNTRY,
                DgraphEntity.TOKEN,
                Map.of(
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.PAN),
                        DgraphEntity.COUNTRY, Set.of(PaymentCheckedField.COUNTRY_BANK)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_PANS_BY_COUNTRY_BANK_TEST_QUERY, query);
    }

    @Test
    public void getUniquePansByCountryIpQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.IP,
                DgraphEntity.TOKEN,
                Map.of(
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.PAN),
                        DgraphEntity.IP, Set.of(PaymentCheckedField.COUNTRY_IP)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_PANS_BY_COUNTRY_IP_TEST_QUERY, query);
    }

    @Test
    public void getUniquePansByBinQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.BIN,
                DgraphEntity.TOKEN,
                Map.of(
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.PAN),
                        DgraphEntity.BIN, Set.of(PaymentCheckedField.BIN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_PANS_BY_BIN_TEST_QUERY, query);
    }

    @Test
    public void getUniquePansByPanQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.TOKEN,
                DgraphEntity.TOKEN,
                Map.of(
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.PAN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_PANS_BY_PAN_TEST_QUERY, query);
    }

    @Test
    public void getUniquePansByCurrencyQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.CURRENCY,
                DgraphEntity.TOKEN,
                Map.of(
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.PAN),
                        DgraphEntity.CURRENCY, Set.of(PaymentCheckedField.CURRENCY)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_PANS_BY_CURRENCY_TEST_QUERY, query);
    }

    @Test
    public void getUniquePansByShopQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.SHOP,
                DgraphEntity.TOKEN,
                Map.of(
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.PAN),
                        DgraphEntity.SHOP, Set.of(PaymentCheckedField.SHOP_ID)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_PANS_BY_SHOP_TEST_QUERY, query);
    }

    @Test
    public void getUniquePansByPartyQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.PARTY,
                DgraphEntity.TOKEN,
                Map.of(
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.PAN),
                        DgraphEntity.PARTY, Set.of(PaymentCheckedField.PARTY_ID)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_PANS_BY_PARTY_TEST_QUERY, query);
    }

    @Test
    public void getUniquePansByMobileQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.PAYMENT,
                DgraphEntity.TOKEN,
                Map.of(
                        DgraphEntity.PAYMENT, Set.of(PaymentCheckedField.MOBILE)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_PANS_BY_MOBILE_TEST_QUERY, query);
    }

    @Test
    public void getUniquePansByRecurrentQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.PAYMENT,
                DgraphEntity.TOKEN,
                Map.of(
                        DgraphEntity.PAYMENT, Set.of(PaymentCheckedField.RECURRENT)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_PANS_BY_RECURRENT_TEST_QUERY, query);
    }

    @Test
    public void getUniquePansByCardTokenQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.TOKEN,
                DgraphEntity.TOKEN,
                Map.of(
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN, PaymentCheckedField.PAN)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_PANS_BY_CARD_TOKEN_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCurrensiesByEmailQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.EMAIL,
                DgraphEntity.CURRENCY,
                Map.of(
                        DgraphEntity.EMAIL, Set.of(PaymentCheckedField.EMAIL),
                        DgraphEntity.CURRENCY, Set.of(PaymentCheckedField.CURRENCY)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_CURRENCIES_BY_EMAIL_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCurrensiesByIpQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.IP,
                DgraphEntity.CURRENCY,
                Map.of(
                        DgraphEntity.IP, Set.of(PaymentCheckedField.IP),
                        DgraphEntity.CURRENCY, Set.of(PaymentCheckedField.CURRENCY)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_CURRENCIES_BY_IP_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCurrensiesByFingerprintQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.FINGERPRINT,
                DgraphEntity.CURRENCY,
                Map.of(
                        DgraphEntity.FINGERPRINT, Set.of(PaymentCheckedField.FINGERPRINT),
                        DgraphEntity.CURRENCY, Set.of(PaymentCheckedField.CURRENCY)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_CURRENCIES_BY_FINGERPRINT_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCurrensiesByCountryBankQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.COUNTRY,
                DgraphEntity.CURRENCY,
                Map.of(
                        DgraphEntity.COUNTRY, Set.of(PaymentCheckedField.COUNTRY_BANK),
                        DgraphEntity.EMAIL, Set.of(PaymentCheckedField.EMAIL)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_CURRENCIES_BY_COUNTRY_BANK_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCurrensiesByCountryIpQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.IP,
                DgraphEntity.CURRENCY,
                Map.of(
                        DgraphEntity.IP, Set.of(PaymentCheckedField.COUNTRY_IP),
                        DgraphEntity.EMAIL, Set.of(PaymentCheckedField.EMAIL)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_CURRENCIES_BY_COUNTRY_IP_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCurrensiesByBinQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.BIN,
                DgraphEntity.CURRENCY,
                Map.of(
                        DgraphEntity.BIN, Set.of(PaymentCheckedField.BIN),
                        DgraphEntity.EMAIL, Set.of(PaymentCheckedField.EMAIL)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_CURRENCIES_BY_BIN_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCurrensiesByPanQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.TOKEN,
                DgraphEntity.CURRENCY,
                Map.of(
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.PAN),
                        DgraphEntity.EMAIL, Set.of(PaymentCheckedField.EMAIL)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_CURRENCIES_BY_PAN_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCurrensiesByCurrencyQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.CURRENCY,
                DgraphEntity.CURRENCY,
                Map.of(
                        DgraphEntity.EMAIL, Set.of(PaymentCheckedField.EMAIL)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_CURRENCIES_BY_CURRENCY_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCurrensiesByShopQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.SHOP,
                DgraphEntity.CURRENCY,
                Map.of(
                        DgraphEntity.SHOP, Set.of(PaymentCheckedField.SHOP_ID),
                        DgraphEntity.EMAIL, Set.of(PaymentCheckedField.EMAIL)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_CURRENCIES_BY_SHOP_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCurrensiesByPartyQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.PARTY,
                DgraphEntity.CURRENCY,
                Map.of(
                        DgraphEntity.PARTY, Set.of(PaymentCheckedField.PARTY_ID),
                        DgraphEntity.EMAIL, Set.of(PaymentCheckedField.EMAIL)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_CURRENCIES_BY_PARTY_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCurrensiesByMobileQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.PAYMENT,
                DgraphEntity.CURRENCY,
                Map.of(
                        DgraphEntity.PAYMENT, Set.of(PaymentCheckedField.MOBILE),
                        DgraphEntity.EMAIL, Set.of(PaymentCheckedField.EMAIL)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_CURRENCIES_BY_MOBILE_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCurrensiesByRecurrentQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.PAYMENT,
                DgraphEntity.CURRENCY,
                Map.of(
                        DgraphEntity.PAYMENT, Set.of(PaymentCheckedField.RECURRENT),
                        DgraphEntity.EMAIL, Set.of(PaymentCheckedField.EMAIL)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_CURRENCIES_BY_RECURRENT_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCurrensiesByCardTokenQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.TOKEN,
                DgraphEntity.CURRENCY,
                Map.of(
                        DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN),
                        DgraphEntity.EMAIL, Set.of(PaymentCheckedField.EMAIL)
                )
        );
        assertNotNull(query);
        assertEquals(UNIQUE_CURRENCIES_BY_CARD_TOKEN_TEST_QUERY, query);
    }

    @Test
    public void getUniqueShopsByEmailQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.EMAIL,
                DgraphEntity.SHOP,
                Map.of(DgraphEntity.EMAIL, Set.of(PaymentCheckedField.EMAIL))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_SHOP_IDS_BY_EMAIL_TEST_QUERY, query);
    }

    @Test
    public void getUniqueShopsByIpQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.IP,
                DgraphEntity.SHOP,
                Map.of(DgraphEntity.IP, Set.of(PaymentCheckedField.IP))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_SHOP_IDS_BY_IP_TEST_QUERY, query);
    }

    @Test
    public void getUniqueShopsByFingerprintQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.FINGERPRINT,
                DgraphEntity.SHOP,
                Map.of(DgraphEntity.FINGERPRINT, Set.of(PaymentCheckedField.FINGERPRINT))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_SHOP_IDS_BY_FINGERPRINT_TEST_QUERY, query);
    }

    @Test
    public void getUniqueShopsByCountryBankQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.COUNTRY,
                DgraphEntity.SHOP,
                Map.of(DgraphEntity.COUNTRY, Set.of(PaymentCheckedField.COUNTRY_BANK))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_SHOP_IDS_BY_COUNTRY_BANK_TEST_QUERY, query);
    }

    @Test
    public void getUniqueShopsByCountryIpQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.IP,
                DgraphEntity.SHOP,
                Map.of(DgraphEntity.IP, Set.of(PaymentCheckedField.COUNTRY_IP))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_SHOP_IDS_BY_COUNTRY_IP_TEST_QUERY, query);
    }

    @Test
    public void getUniqueShopsByBinQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.BIN,
                DgraphEntity.SHOP,
                Map.of(DgraphEntity.BIN, Set.of(PaymentCheckedField.BIN))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_SHOP_IDS_BY_BIN_TEST_QUERY, query);
    }

    @Test
    public void getUniqueShopsByPanQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.TOKEN,
                DgraphEntity.SHOP,
                Map.of(DgraphEntity.TOKEN, Set.of(PaymentCheckedField.PAN))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_SHOP_IDS_BY_PAN_TEST_QUERY, query);
    }

    @Test
    public void getUniqueShopsByCurrencyQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.CURRENCY,
                DgraphEntity.SHOP,
                Map.of(DgraphEntity.CURRENCY, Set.of(PaymentCheckedField.CURRENCY))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_SHOP_IDS_BY_CURRENCY_TEST_QUERY, query);
    }

    @Test
    public void getUniqueShopsByShopQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.SHOP,
                DgraphEntity.SHOP,
                Map.of(DgraphEntity.SHOP, Set.of(PaymentCheckedField.SHOP_ID))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_SHOP_IDS_BY_SHOP_TEST_QUERY, query);
    }

    @Test
    public void getUniqueShopsByPartyQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.PARTY,
                DgraphEntity.SHOP,
                Map.of(DgraphEntity.PARTY, Set.of(PaymentCheckedField.PARTY_ID))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_SHOP_IDS_BY_PARTY_TEST_QUERY, query);
    }

    @Test
    public void getUniqueShopsByMobileQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.PAYMENT,
                DgraphEntity.SHOP,
                Map.of(DgraphEntity.PAYMENT, Set.of(PaymentCheckedField.MOBILE))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_SHOP_IDS_BY_MOBILE_TEST_QUERY, query);
    }

    @Test
    public void getUniqueShopsByRecurrentQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.PAYMENT,
                DgraphEntity.SHOP,
                Map.of(DgraphEntity.PAYMENT, Set.of(PaymentCheckedField.RECURRENT))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_SHOP_IDS_BY_RECURRENT_TEST_QUERY, query);
    }

    @Test
    public void getUniqueShopsByCardTokenQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.TOKEN,
                DgraphEntity.SHOP,
                Map.of(DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_SHOP_IDS_BY_CARD_TOKEN_TEST_QUERY, query);
    }

    @Test
    public void getUniquePartiesByEmailQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.EMAIL,
                DgraphEntity.PARTY,
                Map.of(DgraphEntity.EMAIL, Set.of(PaymentCheckedField.EMAIL))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_PARTY_IDS_BY_EMAIL_TEST_QUERY, query);
    }

    @Test
    public void getUniquePartiesByIpQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.IP,
                DgraphEntity.PARTY,
                Map.of(DgraphEntity.IP, Set.of(PaymentCheckedField.IP))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_PARTY_IDS_BY_IP_TEST_QUERY, query);
    }

    @Test
    public void getUniquePartiesByFingerprintQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.FINGERPRINT,
                DgraphEntity.PARTY,
                Map.of(DgraphEntity.FINGERPRINT, Set.of(PaymentCheckedField.FINGERPRINT))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_PARTY_IDS_BY_FINGERPRINT_TEST_QUERY, query);
    }

    @Test
    public void getUniquePartiesByCountryBankQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.COUNTRY,
                DgraphEntity.PARTY,
                Map.of(DgraphEntity.COUNTRY, Set.of(PaymentCheckedField.COUNTRY_BANK))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_PARTY_IDS_BY_COUNTRY_BANK_TEST_QUERY, query);
    }

    @Test
    public void getUniquePartiesByCountryIpQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.IP,
                DgraphEntity.PARTY,
                Map.of(DgraphEntity.IP, Set.of(PaymentCheckedField.COUNTRY_IP))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_PARTY_IDS_BY_COUNTRY_IP_TEST_QUERY, query);
    }

    @Test
    public void getUniquePartiesByBinQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.BIN,
                DgraphEntity.PARTY,
                Map.of(DgraphEntity.BIN, Set.of(PaymentCheckedField.BIN))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_PARTY_IDS_BY_BIN_TEST_QUERY, query);
    }

    @Test
    public void getUniquePartiesByPanQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.TOKEN,
                DgraphEntity.PARTY,
                Map.of(DgraphEntity.TOKEN, Set.of(PaymentCheckedField.PAN))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_PARTY_IDS_BY_PAN_TEST_QUERY, query);
    }

    @Test
    public void getUniquePartiesByCurrencyQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.CURRENCY,
                DgraphEntity.PARTY,
                Map.of(DgraphEntity.CURRENCY, Set.of(PaymentCheckedField.CURRENCY))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_PARTY_IDS_BY_CURRENCY_TEST_QUERY, query);
    }

    @Test
    public void getUniquePartiesByShopQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.SHOP,
                DgraphEntity.PARTY,
                Map.of(DgraphEntity.SHOP, Set.of(PaymentCheckedField.SHOP_ID))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_PARTY_IDS_BY_SHOP_TEST_QUERY, query);
    }

    @Test
    public void getUniquePartiesByPartyQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.PARTY,
                DgraphEntity.PARTY,
                Map.of(DgraphEntity.PARTY, Set.of(PaymentCheckedField.PARTY_ID))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_PARTY_IDS_BY_PARTY_TEST_QUERY, query);
    }

    @Test
    public void getUniquePartiesByMobileQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.PAYMENT,
                DgraphEntity.PARTY,
                Map.of(DgraphEntity.PAYMENT, Set.of(PaymentCheckedField.MOBILE))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_PARTY_IDS_BY_MOBILE_TEST_QUERY, query);
    }

    @Test
    public void getUniquePartiesByRecurrentQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.PAYMENT,
                DgraphEntity.PARTY,
                Map.of(DgraphEntity.PAYMENT, Set.of(PaymentCheckedField.RECURRENT))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_PARTY_IDS_BY_RECURRENT_TEST_QUERY, query);
    }

    @Test
    public void getUniquePartiesByCardTokenQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.TOKEN,
                DgraphEntity.PARTY,
                Map.of(DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_PARTY_IDS_BY_CARD_TOKEN_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCardTokensByEmailQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.EMAIL,
                DgraphEntity.TOKEN,
                Map.of(DgraphEntity.EMAIL, Set.of(PaymentCheckedField.EMAIL))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_CARD_TOKENS_BY_EMAIL_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCardTokensByIpQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.IP,
                DgraphEntity.TOKEN,
                Map.of(DgraphEntity.IP, Set.of(PaymentCheckedField.IP))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_CARD_TOKENS_BY_IP_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCardTokensByFingerprintQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.FINGERPRINT,
                DgraphEntity.TOKEN,
                Map.of(DgraphEntity.FINGERPRINT, Set.of(PaymentCheckedField.FINGERPRINT))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_CARD_TOKENS_BY_FINGERPRINT_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCardTokensByCountryBankQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.COUNTRY,
                DgraphEntity.TOKEN,
                Map.of(DgraphEntity.COUNTRY, Set.of(PaymentCheckedField.COUNTRY_BANK))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_CARD_TOKENS_BY_COUNTRY_BANK_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCardTokensByCountryIpQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.IP,
                DgraphEntity.TOKEN,
                Map.of(DgraphEntity.IP, Set.of(PaymentCheckedField.COUNTRY_IP))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_CARD_TOKENS_BY_COUNTRY_IP_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCardTokensByBinQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.BIN,
                DgraphEntity.TOKEN,
                Map.of(DgraphEntity.BIN, Set.of(PaymentCheckedField.BIN))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_CARD_TOKENS_BY_BIN_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCardTokensByPanQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.TOKEN,
                DgraphEntity.TOKEN,
                Map.of(DgraphEntity.TOKEN, Set.of(PaymentCheckedField.PAN))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_CARD_TOKENS_BY_PAN_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCardTokensByCurrencyQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.CURRENCY,
                DgraphEntity.TOKEN,
                Map.of(DgraphEntity.CURRENCY, Set.of(PaymentCheckedField.CURRENCY))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_CARD_TOKENS_BY_CURRENCY_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCardTokensByShopQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.SHOP,
                DgraphEntity.TOKEN,
                Map.of(DgraphEntity.SHOP, Set.of(PaymentCheckedField.SHOP_ID))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_CARD_TOKENS_BY_SHOP_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCardTokensByPartyQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.PARTY,
                DgraphEntity.TOKEN,
                Map.of(DgraphEntity.PARTY, Set.of(PaymentCheckedField.PARTY_ID))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_CARD_TOKENS_BY_PARTY_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCardTokensByMobileQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.PAYMENT,
                DgraphEntity.TOKEN,
                Map.of(DgraphEntity.PAYMENT, Set.of(PaymentCheckedField.MOBILE))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_CARD_TOKENS_BY_MOBILE_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCardTokensByRecurrentQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.PAYMENT,
                DgraphEntity.TOKEN,
                Map.of(DgraphEntity.PAYMENT, Set.of(PaymentCheckedField.RECURRENT))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_CARD_TOKENS_BY_RECURRENT_TEST_QUERY, query);
    }

    @Test
    public void getUniqueCardTokensByCardTokenQueryTest() {
        String query = getUniqueQuery(
                DgraphEntity.TOKEN,
                DgraphEntity.TOKEN,
                Map.of(DgraphEntity.TOKEN, Set.of(PaymentCheckedField.CARD_TOKEN))
        );
        assertNotNull(query);
        assertEquals(UNIQUE_CARD_TOKENS_BY_CARD_TOKEN_TEST_QUERY, query);
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
                PaymentStatus.captured.name()
        );
    }

}
