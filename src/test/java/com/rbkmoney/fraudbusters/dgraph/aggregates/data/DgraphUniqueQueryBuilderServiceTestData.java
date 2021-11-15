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
                predicates as var(func: type(Email))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade @normalize {
                        operationIp @filter(eq(ipAddress, "localhost"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_EMAILS_BY_FINGERPRINT_TEST_QUERY = """
            query all() {
                predicates as var(func: type(Email))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade @normalize {
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_EMAILS_BY_COUNTRY_BANK_TEST_QUERY = """
            query all() {
                predicates as var(func: type(Email))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade @normalize {
                        country @filter(eq(countryName, "Russia"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_EMAILS_BY_COUNTRY_IP_TEST_QUERY = """
            query all() {
                predicates as var(func: type(Email))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade @normalize {
                        operationIp @filter(eq(ipAddress, "localhost"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_EMAILS_BY_BIN_TEST_QUERY = """
            query all() {
                predicates as var(func: type(Email))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade @normalize {
                        bin @filter(eq(cardBin, "000000"))
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
                predicates as var(func: type(Email))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade @normalize {
                        cardToken @filter(eq(maskedPan, "2424"))
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
                predicates as var(func: type(Email))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade @normalize {
                        currency @filter(eq(currencyCode, "RUB"))
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_EMAILS_BY_SHOP_TEST_QUERY = """
            query all() {
                predicates as var(func: type(Email))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade @normalize {
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                        shop @filter(eq(shopId, "shop1"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_EMAILS_BY_PARTY_TEST_QUERY = """
            query all() {
                predicates as var(func: type(Email))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade @normalize {
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                        party @filter(eq(partyId, "party1"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_EMAILS_BY_MOBILE_TEST_QUERY = """
            query all() {
                predicates as var(func: type(Email))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured")) @filter(eq(mobile, false)) @cascade @normalize {
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_EMAILS_BY_RECURRENT_TEST_QUERY = """
            query all() {
                predicates as var(func: type(Email))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured")) @filter(eq(recurrent, true)) @cascade @normalize {
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_EMAILS_BY_CARD_TOKEN_TEST_QUERY = """
            query all() {
                predicates as var(func: type(Email))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade @normalize {
                        cardToken @filter(eq(tokenId, "token001"))
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_IPS_BY_EMAIL_TEST_QUERY = """
            query all() {
                predicates as var(func: type(IP))  @cascade {
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

    public static final String UNIQUE_IPS_BY_IP_TEST_QUERY = """
            query all() {
                        
              aggregates(func: type(IP)) @filter(eq(ipAddress, "localhost")) {
                  count: count(uid)
              }
                        
            }
            """;

    public static final String UNIQUE_IPS_BY_FINGERPRINT_TEST_QUERY = """
            query all() {
                predicates as var(func: type(IP))  @cascade {
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

    public static final String UNIQUE_IPS_BY_COUNTRY_BANK_TEST_QUERY = """
            query all() {
                predicates as var(func: type(IP))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade @normalize {
                        country @filter(eq(countryName, "Russia"))
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_IPS_BY_COUNTRY_IP_TEST_QUERY = """
            query all() {
                        
              aggregates(func: type(IP)) @filter(eq(ipAddress, "localhost")) {
                  count: count(uid)
              }
                        
            }
            """;

    public static final String UNIQUE_IPS_BY_BIN_TEST_QUERY = """
            query all() {
                predicates as var(func: type(IP))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade @normalize {
                        bin @filter(eq(cardBin, "000000"))
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_IPS_BY_PAN_TEST_QUERY = """
            query all() {
                predicates as var(func: type(IP))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade @normalize {
                        cardToken @filter(eq(maskedPan, "2424"))
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_IPS_BY_CURRENCY_TEST_QUERY = """
            query all() {
                predicates as var(func: type(IP))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade @normalize {
                        currency @filter(eq(currencyCode, "RUB"))
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_IPS_BY_SHOP_TEST_QUERY = """
            query all() {
                predicates as var(func: type(IP))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade @normalize {
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                        shop @filter(eq(shopId, "shop1"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_IPS_BY_PARTY_TEST_QUERY = """
            query all() {
                predicates as var(func: type(IP))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade @normalize {
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                        party @filter(eq(partyId, "party1"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_IPS_BY_MOBILE_TEST_QUERY = """
            query all() {
                predicates as var(func: type(IP))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured")) @filter(eq(mobile, false)) @cascade @normalize {
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_IPS_BY_RECURRENT_TEST_QUERY = """
            query all() {
                predicates as var(func: type(IP))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured")) @filter(eq(recurrent, true)) @cascade @normalize {
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_IPS_BY_CARD_TOKEN_TEST_QUERY = """
            query all() {
                predicates as var(func: type(IP))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade @normalize {
                        cardToken @filter(eq(tokenId, "token001"))
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_FINGERPRINTS_BY_EMAIL_TEST_QUERY = """
            query all() {
                predicates as var(func: type(Fingerprint))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade @normalize {
                        cardToken @filter(eq(tokenId, "token001"))
                        contactEmail @filter(eq(userEmail, "test@test.ru"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_FINGERPRINTS_BY_IP_TEST_QUERY = """
            query all() {
                predicates as var(func: type(Fingerprint))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade @normalize {
                        cardToken @filter(eq(tokenId, "token001"))
                        operationIp @filter(eq(ipAddress, "localhost"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_FINGERPRINTS_BY_FINGERPRINT_TEST_QUERY = """
            query all() {
                        
              aggregates(func: type(Fingerprint)) @filter(eq(fingerprintData, "finger001")) {
                  count: count(uid)
              }
                        
            }
            """;

    public static final String UNIQUE_FINGERPRINTS_BY_COUNTRY_BANK_TEST_QUERY = """
            query all() {
                predicates as var(func: type(Fingerprint))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade @normalize {
                        cardToken @filter(eq(tokenId, "token001"))
                        country @filter(eq(countryName, "Russia"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_FINGERPRINTS_BY_COUNTRY_IP_TEST_QUERY = """
            query all() {
                predicates as var(func: type(Fingerprint))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade @normalize {
                        cardToken @filter(eq(tokenId, "token001"))
                        operationIp @filter(eq(ipAddress, "localhost"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_FINGERPRINTS_BY_BIN_TEST_QUERY = """
            query all() {
                predicates as var(func: type(Fingerprint))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade @normalize {
                        bin @filter(eq(cardBin, "000000"))
                        cardToken @filter(eq(tokenId, "token001"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_FINGERPRINTS_BY_PAN_TEST_QUERY = """
            query all() {
                predicates as var(func: type(Fingerprint))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade @normalize {
                        cardToken @filter(eq(maskedPan, "2424") and eq(tokenId, "token001"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_FINGERPRINTS_BY_CURRENCY_TEST_QUERY = """
            query all() {
                predicates as var(func: type(Fingerprint))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade @normalize {
                        cardToken @filter(eq(tokenId, "token001"))
                        currency @filter(eq(currencyCode, "RUB"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_FINGERPRINTS_BY_SHOP_TEST_QUERY = """
            query all() {
                predicates as var(func: type(Fingerprint))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade @normalize {
                        cardToken @filter(eq(tokenId, "token001"))
                        shop @filter(eq(shopId, "shop1"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_FINGERPRINTS_BY_PARTY_TEST_QUERY = """
            query all() {
                predicates as var(func: type(Fingerprint))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade @normalize {
                        cardToken @filter(eq(tokenId, "token001"))
                        party @filter(eq(partyId, "party1"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_FINGERPRINTS_BY_MOBILE_TEST_QUERY = """
            query all() {
                predicates as var(func: type(Fingerprint))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured")) @filter(eq(mobile, false)) @cascade @normalize {
                        cardToken @filter(eq(tokenId, "token001"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_FINGERPRINTS_BY_RECURRENT_TEST_QUERY = """
            query all() {
                predicates as var(func: type(Fingerprint))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured")) @filter(eq(recurrent, true)) @cascade @normalize {
                        cardToken @filter(eq(tokenId, "token001"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_FINGERPRINTS_BY_CARD_TOKEN_TEST_QUERY = """
            query all() {
                predicates as var(func: type(Fingerprint))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade @normalize {
                        cardToken @filter(eq(tokenId, "token001"))
                        contactEmail @filter(eq(userEmail, "test@test.ru"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_COUNTRY_BANKS_BY_EMAIL_TEST_QUERY = """
            query all() {
                predicates as var(func: type(Country))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade @normalize {
                        cardToken @filter(eq(tokenId, "token001"))
                        contactEmail @filter(eq(userEmail, "test@test.ru"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_COUNTRY_BANKS_BY_IP_TEST_QUERY = """
            query all() {
                predicates as var(func: type(Country))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade @normalize {
                        cardToken @filter(eq(tokenId, "token001"))
                        operationIp @filter(eq(ipAddress, "localhost"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_COUNTRY_BANKS_BY_FINGERPRINT_TEST_QUERY = """
            query all() {
                predicates as var(func: type(Country))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade @normalize {
                        cardToken @filter(eq(tokenId, "token001"))
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_COUNTRY_BANKS_BY_COUNTRY_BANK_TEST_QUERY = """
            query all() {
                        
              aggregates(func: type(Country))  {
                  count: count(uid)
              }
                        
            }
            """;

    public static final String UNIQUE_COUNTRY_BANKS_BY_COUNTRY_IP_TEST_QUERY = """
            query all() {
                predicates as var(func: type(Country))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade @normalize {
                        cardToken @filter(eq(tokenId, "token001"))
                        operationIp @filter(eq(ipAddress, "localhost"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_COUNTRY_BANKS_BY_BIN_TEST_QUERY = """
            query all() {
                predicates as var(func: type(Country))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade @normalize {
                        bin @filter(eq(cardBin, "000000"))
                        cardToken @filter(eq(tokenId, "token001"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_COUNTRY_BANKS_BY_PAN_TEST_QUERY = """
            query all() {
                predicates as var(func: type(Country))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade @normalize {
                        bin @filter(eq(cardBin, "000000"))
                        cardToken @filter(eq(maskedPan, "2424") and eq(tokenId, "token001"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_COUNTRY_BANKS_BY_CURRENCY_TEST_QUERY = """
            query all() {
                predicates as var(func: type(Country))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade @normalize {
                        cardToken @filter(eq(maskedPan, "2424") and eq(tokenId, "token001"))
                        currency @filter(eq(currencyCode, "RUB"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_COUNTRY_BANKS_BY_SHOP_TEST_QUERY = """
            query all() {
                predicates as var(func: type(Country))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade @normalize {
                        cardToken @filter(eq(tokenId, "token001"))
                        shop @filter(eq(shopId, "shop1"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_COUNTRY_BANKS_BY_PARTY_TEST_QUERY = """
            query all() {
                predicates as var(func: type(Country))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade @normalize {
                        cardToken @filter(eq(tokenId, "token001"))
                        party @filter(eq(partyId, "party1"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_COUNTRY_BANKS_BY_MOBILE_TEST_QUERY = """
            query all() {
                predicates as var(func: type(Country))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured")) @filter(eq(mobile, false)) @cascade @normalize {
                        cardToken @filter(eq(tokenId, "token001"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_COUNTRY_BANKS_BY_RECURRENT_TEST_QUERY = """
            query all() {
                predicates as var(func: type(Country))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured")) @filter(eq(recurrent, true)) @cascade @normalize {
                        cardToken @filter(eq(tokenId, "token001"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
            """;

    public static final String UNIQUE_COUNTRY_BANKS_BY_CARD_TOKEN_TEST_QUERY = """
            query all() {
                predicates as var(func: type(Country))  @cascade {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured")) @filter(eq(recurrent, true)) @cascade @normalize {
                        cardToken @filter(eq(tokenId, "token001"))
                    }
                }
                        
                aggregates(func: uid(predicates)) {
                    count: count(uid)
                }
                        
            }
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
