package com.rbkmoney.fraudbusters.dgraph.aggregates.data;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DgraphUniqueQueryBuilderServiceTestData {

    public static final String UNIQUE_EMAILS_BY_EMAIL_TEST_QUERY = """
            query all() {
                        
              aggregates(func: type(Email)) @filter(eq(userEmail, "test@test.ru")) {
                  count: count(uid)
              }
                        
            }
            """;

    public static final String UNIQUE_EMAILS_BY_IP_TEST_QUERY = """
            query all() {
                predicates as var(func: type(IP)) @filter(eq(ipAddress, "localhost")) {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade @normalize {
                        contactEmail @filter(eq(userEmail, "test@test.ru"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_EMAILS_BY_FINGERPRINT_TEST_QUERY = """
            query all() {
                predicates as var(func: type(Fingerprint)) @filter(eq(fingerprintData, "finger001")) {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade @normalize {
                        contactEmail @filter(eq(userEmail, "test@test.ru"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_EMAILS_BY_COUNTRY_BANK_TEST_QUERY = """
            query all() {
                predicates as var(func: type(Country)) @filter(eq(bankCountry, "Russia")) {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade @normalize {
                        contactEmail @filter(eq(userEmail, "test@test.ru"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_EMAILS_BY_COUNTRY_IP_TEST_QUERY = """
            query all() {
                predicates as var(func: type(IP)) @filter(eq(ipAddress, "localhost")) {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade @normalize {
                        contactEmail @filter(eq(userEmail, "test@test.ru"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_EMAILS_BY_BIN_TEST_QUERY = """
            query all() {
                predicates as var(func: type(Bin)) @filter(eq(cardBin, "000000")) {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade @normalize {
                        contactEmail @filter(eq(userEmail, "test@test.ru"))
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_EMAILS_BY_PAN_TEST_QUERY = """
            query all() {
                predicates as var(func: type(Token)) @filter(eq(maskedPan, "2424")) {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade @normalize {
                        contactEmail @filter(eq(userEmail, "test@test.ru"))
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_EMAILS_BY_CURRENCY_TEST_QUERY = """
            query all() {
                predicates as var(func: type(Payment)) @filter(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured")) {
                    cardToken @filter(eq(maskedPan, "2424"))
                    contactEmail @filter(eq(userEmail, "test@test.ru"))
                    fingerprint @filter(eq(fingerprintData, "finger001"))
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_EMAILS_BY_SHOP_TEST_QUERY = """
            """;

    public static final String UNIQUE_EMAILS_BY_PARTY_TEST_QUERY = """
            """;

    public static final String UNIQUE_EMAILS_BY_MOBILE_TEST_QUERY = """
            """;

    public static final String UNIQUE_EMAILS_BY_RECURRENT_TEST_QUERY = """
            """;

    public static final String UNIQUE_EMAILS_BY_CARD_TOKEN_TEST_QUERY = """
            """;

    public static final String UNIQUE_IPS_BY_EMAIL_TEST_QUERY = """
            """;

    public static final String UNIQUE_IPS_BY_IP_TEST_QUERY = """
            """;

    public static final String UNIQUE_IPS_BY_FINGERPRINT_TEST_QUERY = """
            """;

    public static final String UNIQUE_IPS_BY_COUNTRY_BANK_TEST_QUERY = """
            """;

    public static final String UNIQUE_IPS_BY_COUNTRY_IP_TEST_QUERY = """
            """;

    public static final String UNIQUE_IPS_BY_BIN_TEST_QUERY = """
            """;

    public static final String UNIQUE_IPS_BY_PAN_TEST_QUERY = """
            """;

    public static final String UNIQUE_IPS_BY_CURRENCY_TEST_QUERY = """
            """;

    public static final String UNIQUE_IPS_BY_SHOP_TEST_QUERY = """
            """;

    public static final String UNIQUE_IPS_BY_PARTY_TEST_QUERY = """
            """;

    public static final String UNIQUE_IPS_BY_MOBILE_TEST_QUERY = """
            """;

    public static final String UNIQUE_IPS_BY_RECURRENT_TEST_QUERY = """
            """;

    public static final String UNIQUE_IPS_BY_CARD_TOKEN_TEST_QUERY = """
            """;

    public static final String UNIQUE_FINGERPRINTS_BY_EMAIL_TEST_QUERY = """
            """;

    public static final String UNIQUE_FINGERPRINTS_BY_IP_TEST_QUERY = """
            """;

    public static final String UNIQUE_FINGERPRINTS_BY_FINGERPRINT_TEST_QUERY = """
            """;

    public static final String UNIQUE_FINGERPRINTS_BY_COUNTRY_BANK_TEST_QUERY = """
            """;

    public static final String UNIQUE_FINGERPRINTS_BY_COUNTRY_IP_TEST_QUERY = """
            """;

    public static final String UNIQUE_FINGERPRINTS_BY_BIN_TEST_QUERY = """
            """;

    public static final String UNIQUE_FINGERPRINTS_BY_PAN_TEST_QUERY = """
            """;

    public static final String UNIQUE_FINGERPRINTS_BY_CURRENCY_TEST_QUERY = """
            """;

    public static final String UNIQUE_FINGERPRINTS_BY_SHOP_TEST_QUERY = """
            """;

    public static final String UNIQUE_FINGERPRINTS_BY_PARTY_TEST_QUERY = """
            """;

    public static final String UNIQUE_FINGERPRINTS_BY_MOBILE_TEST_QUERY = """
            """;

    public static final String UNIQUE_FINGERPRINTS_BY_RECURRENT_TEST_QUERY = """
            """;

    public static final String UNIQUE_FINGERPRINTS_BY_CARD_TOKEN_TEST_QUERY = """
            """;

    public static final String UNIQUE_COUNTRY_BANKS_BY_EMAIL_TEST_QUERY = """
            """;

    public static final String UNIQUE_COUNTRY_BANKS_BY_IP_TEST_QUERY = """
            """;

    public static final String UNIQUE_COUNTRY_BANKS_BY_FINGERPRINT_TEST_QUERY = """
            """;

    public static final String UNIQUE_COUNTRY_BANKS_BY_COUNTRY_BANK_TEST_QUERY = """
            """;

    public static final String UNIQUE_COUNTRY_BANKS_BY_COUNTRY_IP_TEST_QUERY = """
            """;

    public static final String UNIQUE_COUNTRY_BANKS_BY_BIN_TEST_QUERY = """
            """;

    public static final String UNIQUE_COUNTRY_BANKS_BY_PAN_TEST_QUERY = """
            """;

    public static final String UNIQUE_COUNTRY_BANKS_BY_CURRENCY_TEST_QUERY = """
            """;

    public static final String UNIQUE_COUNTRY_BANKS_BY_SHOP_TEST_QUERY = """
            """;

    public static final String UNIQUE_COUNTRY_BANKS_BY_PARTY_TEST_QUERY = """
            """;

    public static final String UNIQUE_COUNTRY_BANKS_BY_MOBILE_TEST_QUERY = """
            """;

    public static final String UNIQUE_COUNTRY_BANKS_BY_RECURRENT_TEST_QUERY = """
            """;

    public static final String UNIQUE_COUNTRY_BANKS_BY_CARD_TOKEN_TEST_QUERY = """
            """;

    public static final String UNIQUE_COUNTRY_IPS_BY_EMAIL_TEST_QUERY = """
            """;

    public static final String UNIQUE_COUNTRY_IPS_BY_IP_TEST_QUERY = """
            """;

    public static final String UNIQUE_COUNTRY_IPS_BY_FINGERPRINT_TEST_QUERY = """
            """;

    public static final String UNIQUE_COUNTRY_IPS_BY_COUNTRY_BANK_TEST_QUERY = """
            """;

    public static final String UNIQUE_COUNTRY_IPS_BY_COUNTRY_IP_TEST_QUERY = """
            """;

    public static final String UNIQUE_COUNTRY_IPS_BY_BIN_TEST_QUERY = """
            """;

    public static final String UNIQUE_COUNTRY_IPS_BY_PAN_TEST_QUERY = """
            """;

    public static final String UNIQUE_COUNTRY_IPS_BY_CURRENCY_TEST_QUERY = """
            """;

    public static final String UNIQUE_COUNTRY_IPS_BY_SHOP_TEST_QUERY = """
            """;

    public static final String UNIQUE_COUNTRY_IPS_BY_PARTY_TEST_QUERY = """
            """;

    public static final String UNIQUE_COUNTRY_IPS_BY_MOBILE_TEST_QUERY = """
            """;

    public static final String UNIQUE_COUNTRY_IPS_BY_RECURRENT_TEST_QUERY = """
            """;

    public static final String UNIQUE_COUNTRY_IPS_BY_CARD_TOKEN_TEST_QUERY = """
            """;

    public static final String UNIQUE_BINS_BY_EMAIL_TEST_QUERY = """
            """;

    public static final String UNIQUE_BINS_BY_IP_TEST_QUERY = """
            """;

    public static final String UNIQUE_BINS_BY_FINGERPRINT_TEST_QUERY = """
            """;

    public static final String UNIQUE_BINS_BY_COUNTRY_BANK_TEST_QUERY = """
            """;

    public static final String UNIQUE_BINS_BY_COUNTRY_IP_TEST_QUERY = """
            """;

    public static final String UNIQUE_BINS_BY_BIN_TEST_QUERY = """
            """;

    public static final String UNIQUE_BINS_BY_PAN_TEST_QUERY = """
            """;

    public static final String UNIQUE_BINS_BY_CURRENCY_TEST_QUERY = """
            """;

    public static final String UNIQUE_BINS_BY_SHOP_TEST_QUERY = """
            """;

    public static final String UNIQUE_BINS_BY_PARTY_TEST_QUERY = """
            """;

    public static final String UNIQUE_BINS_BY_MOBILE_TEST_QUERY = """
            """;

    public static final String UNIQUE_BINS_BY_RECURRENT_TEST_QUERY = """
            """;

    public static final String UNIQUE_BINS_BY_CARD_TOKEN_TEST_QUERY = """
            """;

    public static final String UNIQUE_PANS_BY_EMAIL_TEST_QUERY = """
            """;

    public static final String UNIQUE_PANS_BY_IP_TEST_QUERY = """
            """;

    public static final String UNIQUE_PANS_BY_FINGERPRINT_TEST_QUERY = """
            """;

    public static final String UNIQUE_PANS_BY_COUNTRY_BANK_TEST_QUERY = """
            """;

    public static final String UNIQUE_PANS_BY_COUNTRY_IP_TEST_QUERY = """
            """;

    public static final String UNIQUE_PANS_BY_BIN_TEST_QUERY = """
            """;

    public static final String UNIQUE_PANS_BY_PAN_TEST_QUERY = """
            """;

    public static final String UNIQUE_PANS_BY_CURRENCY_TEST_QUERY = """
            """;

    public static final String UNIQUE_PANS_BY_SHOP_TEST_QUERY = """
            """;

    public static final String UNIQUE_PANS_BY_PARTY_TEST_QUERY = """
            """;

    public static final String UNIQUE_PANS_BY_MOBILE_TEST_QUERY = """
            """;

    public static final String UNIQUE_PANS_BY_RECURRENT_TEST_QUERY = """
            """;

    public static final String UNIQUE_PANS_BY_CARD_TOKEN_TEST_QUERY = """
            """;

    public static final String UNIQUE_CURRENCIES_BY_EMAIL_TEST_QUERY = """
            """;

    public static final String UNIQUE_CURRENCIES_BY_IP_TEST_QUERY = """
            """;

    public static final String UNIQUE_CURRENCIES_BY_FINGERPRINT_TEST_QUERY = """
            """;

    public static final String UNIQUE_CURRENCIES_BY_COUNTRY_BANK_TEST_QUERY = """
            """;

    public static final String UNIQUE_CURRENCIES_BY_COUNTRY_IP_TEST_QUERY = """
            """;

    public static final String UNIQUE_CURRENCIES_BY_BIN_TEST_QUERY = """
            """;

    public static final String UNIQUE_CURRENCIES_BY_PAN_TEST_QUERY = """
            """;

    public static final String UNIQUE_CURRENCIES_BY_CURRENCY_TEST_QUERY = """
            """;

    public static final String UNIQUE_CURRENCIES_BY_SHOP_TEST_QUERY = """
            """;

    public static final String UNIQUE_CURRENCIES_BY_PARTY_TEST_QUERY = """
            """;

    public static final String UNIQUE_CURRENCIES_BY_MOBILE_TEST_QUERY = """
            """;

    public static final String UNIQUE_CURRENCIES_BY_RECURRENT_TEST_QUERY = """
            """;

    public static final String UNIQUE_CURRENCIES_BY_CARD_TOKEN_TEST_QUERY = """
            """;

    public static final String UNIQUE_SHOP_IDS_BY_EMAIL_TEST_QUERY = """
            """;

    public static final String UNIQUE_SHOP_IDS_BY_IP_TEST_QUERY = """
            """;

    public static final String UNIQUE_SHOP_IDS_BY_FINGERPRINT_TEST_QUERY = """
            """;

    public static final String UNIQUE_SHOP_IDS_BY_COUNTRY_BANK_TEST_QUERY = """
            """;

    public static final String UNIQUE_SHOP_IDS_BY_COUNTRY_IP_TEST_QUERY = """
            """;

    public static final String UNIQUE_SHOP_IDS_BY_BIN_TEST_QUERY = """
            """;

    public static final String UNIQUE_SHOP_IDS_BY_PAN_TEST_QUERY = """
            """;

    public static final String UNIQUE_SHOP_IDS_BY_CURRENCY_TEST_QUERY = """
            """;

    public static final String UNIQUE_SHOP_IDS_BY_SHOP_TEST_QUERY = """
            """;

    public static final String UNIQUE_SHOP_IDS_BY_PARTY_TEST_QUERY = """
            """;

    public static final String UNIQUE_SHOP_IDS_BY_MOBILE_TEST_QUERY = """
            """;

    public static final String UNIQUE_SHOP_IDS_BY_RECURRENT_TEST_QUERY = """
            """;

    public static final String UNIQUE_SHOP_IDS_BY_CARD_TOKEN_TEST_QUERY = """
            """;

    public static final String UNIQUE_PARTY_IDS_BY_EMAIL_TEST_QUERY = """
            """;

    public static final String UNIQUE_PARTY_IDS_BY_IP_TEST_QUERY = """
            """;

    public static final String UNIQUE_PARTY_IDS_BY_FINGERPRINT_TEST_QUERY = """
            """;

    public static final String UNIQUE_PARTY_IDS_BY_COUNTRY_BANK_TEST_QUERY = """
            """;

    public static final String UNIQUE_PARTY_IDS_BY_COUNTRY_IP_TEST_QUERY = """
            """;

    public static final String UNIQUE_PARTY_IDS_BY_BIN_TEST_QUERY = """
            """;

    public static final String UNIQUE_PARTY_IDS_BY_PAN_TEST_QUERY = """
            """;

    public static final String UNIQUE_PARTY_IDS_BY_CURRENCY_TEST_QUERY = """
            """;

    public static final String UNIQUE_PARTY_IDS_BY_SHOP_TEST_QUERY = """
            """;

    public static final String UNIQUE_PARTY_IDS_BY_PARTY_TEST_QUERY = """
            """;

    public static final String UNIQUE_PARTY_IDS_BY_MOBILE_TEST_QUERY = """
            """;

    public static final String UNIQUE_PARTY_IDS_BY_RECURRENT_TEST_QUERY = """
            """;

    public static final String UNIQUE_PARTY_IDS_BY_CARD_TOKEN_TEST_QUERY = """
            """;

    public static final String UNIQUE_MOBILES_BY_EMAIL_TEST_QUERY = """
            """;

    public static final String UNIQUE_MOBILES_BY_IP_TEST_QUERY = """
            """;

    public static final String UNIQUE_MOBILES_BY_FINGERPRINT_TEST_QUERY = """
            """;

    public static final String UNIQUE_MOBILES_BY_COUNTRY_BANK_TEST_QUERY = """
            """;

    public static final String UNIQUE_MOBILES_BY_COUNTRY_IP_TEST_QUERY = """
            """;

    public static final String UNIQUE_MOBILES_BY_BIN_TEST_QUERY = """
            """;

    public static final String UNIQUE_MOBILES_BY_PAN_TEST_QUERY = """
            """;

    public static final String UNIQUE_MOBILES_BY_CURRENCY_TEST_QUERY = """
            """;

    public static final String UNIQUE_MOBILES_BY_SHOP_TEST_QUERY = """
            """;

    public static final String UNIQUE_MOBILES_BY_PARTY_TEST_QUERY = """
            """;

    public static final String UNIQUE_MOBILES_BY_MOBILE_TEST_QUERY = """
            """;

    public static final String UNIQUE_MOBILES_BY_RECURRENT_TEST_QUERY = """
            """;

    public static final String UNIQUE_MOBILES_BY_CARD_TOKEN_TEST_QUERY = """
            """;

    public static final String UNIQUE_RECURRENTS_BY_EMAIL_TEST_QUERY = """
            """;

    public static final String UNIQUE_RECURRENTS_BY_IP_TEST_QUERY = """
            """;

    public static final String UNIQUE_RECURRENTS_BY_FINGERPRINT_TEST_QUERY = """
            """;

    public static final String UNIQUE_RECURRENTS_BY_COUNTRY_BANK_TEST_QUERY = """
            """;

    public static final String UNIQUE_RECURRENTS_BY_COUNTRY_IP_TEST_QUERY = """
            """;

    public static final String UNIQUE_RECURRENTS_BY_BIN_TEST_QUERY = """
            """;

    public static final String UNIQUE_RECURRENTS_BY_PAN_TEST_QUERY = """
            """;

    public static final String UNIQUE_RECURRENTS_BY_CURRENCY_TEST_QUERY = """
            """;

    public static final String UNIQUE_RECURRENTS_BY_SHOP_TEST_QUERY = """
            """;

    public static final String UNIQUE_RECURRENTS_BY_PARTY_TEST_QUERY = """
            """;

    public static final String UNIQUE_RECURRENTS_BY_MOBILE_TEST_QUERY = """
            """;

    public static final String UNIQUE_RECURRENTS_BY_RECURRENT_TEST_QUERY = """
            """;

    public static final String UNIQUE_RECURRENTS_BY_CARD_TOKEN_TEST_QUERY = """
            """;

    public static final String UNIQUE_CARD_TOKENS_BY_EMAIL_TEST_QUERY = """
            """;

    public static final String UNIQUE_CARD_TOKENS_BY_IP_TEST_QUERY = """
            """;

    public static final String UNIQUE_CARD_TOKENS_BY_FINGERPRINT_TEST_QUERY = """
            """;

    public static final String UNIQUE_CARD_TOKENS_BY_COUNTRY_BANK_TEST_QUERY = """
            """;

    public static final String UNIQUE_CARD_TOKENS_BY_COUNTRY_IP_TEST_QUERY = """
            """;

    public static final String UNIQUE_CARD_TOKENS_BY_BIN_TEST_QUERY = """
            """;

    public static final String UNIQUE_CARD_TOKENS_BY_PAN_TEST_QUERY = """
            """;

    public static final String UNIQUE_CARD_TOKENS_BY_CURRENCY_TEST_QUERY = """
            """;

    public static final String UNIQUE_CARD_TOKENS_BY_SHOP_TEST_QUERY = """
            """;

    public static final String UNIQUE_CARD_TOKENS_BY_PARTY_TEST_QUERY = """
            """;

    public static final String UNIQUE_CARD_TOKENS_BY_MOBILE_TEST_QUERY = """
            """;

    public static final String UNIQUE_CARD_TOKENS_BY_RECURRENT_TEST_QUERY = """
            """;

    public static final String UNIQUE_CARD_TOKENS_BY_CARD_TOKEN_TEST_QUERY = """
            """;
}
