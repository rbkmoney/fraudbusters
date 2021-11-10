package com.rbkmoney.fraudbusters.dgraph.aggregates.data;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DgraphPaymentCountQueryBuilderServiceTestData {

    public static final String PAYMENTS_COUNT_QUERY_BY_TOKEN_ROOT_WITH_MINIMAL_DATA = """
            query all() {
                aggregates(func: type(Token)) @filter(eq(tokenId, "token001")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade {
                        count : count(uid)
                    }
                }
            }
            """;

    public static final String PAYMENTS_COUNT_QUERY_BY_TOKEN_ROOT_WITH_USUAL_DATASET = """
            query all() {
                aggregates(func: type(Token)) @filter(eq(tokenId, "token001")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade {
                        count : count(uid)
                        partyShop @filter(eq(shopId, "shop1") and eq(partyId, "party1"))
                    }
                }
            }
            """;

    public static final String PAYMENTS_COUNT_QUERY_BY_TOKEN_ROOT_WITH_FULL_DATASET = """
            query all() {
                aggregates(func: type(Token)) @filter(eq(maskedPan, "2424") and eq(tokenId, "token001")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured")) @filter(eq(bankCountry, "Russia") and eq(mobile, false) and eq(recurrent, true)) @cascade {
                        count : count(uid)
                        bin @filter(eq(cardBin, "000000"))
                        contactEmail @filter(eq(userEmail, "test@test.ru"))
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                        operationIp @filter(eq(ipAddress, "localhost"))
                        partyShop @filter(eq(shopId, "shop1") and eq(partyId, "party1"))
                    }
                }
            }
            """;

    public static final String PAYMENTS_COUNT_QUERY_BY_EMAIL_ROOT_WITH_MINIMAL_DATA = """
            query all() {
                aggregates(func: type(Email)) @filter(eq(userEmail, "test@test.ru")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade {
                        count : count(uid)
                    }
                }
            }
            """;

    public static final String PAYMENTS_COUNT_QUERY_BY_EMAIL_ROOT_WITH_USUAL_DATASET = """
            query all() {
                aggregates(func: type(Email)) @filter(eq(userEmail, "test@test.ru")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade {
                        count : count(uid)
                        partyShop @filter(eq(shopId, "shop1") and eq(partyId, "party1"))
                    }
                }
            }
            """;

    public static final String PAYMENTS_COUNT_QUERY_BY_EMAIL_ROOT_WITH_FULL_DATASET = """
            query all() {
                aggregates(func: type(Email)) @filter(eq(userEmail, "test@test.ru")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured")) @filter(eq(bankCountry, "Russia") and eq(mobile, false) and eq(recurrent, true)) @cascade {
                        count : count(uid)
                        bin @filter(eq(cardBin, "000000"))
                        cardToken @filter(eq(maskedPan, "2424") and eq(tokenId, "token001"))
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                        operationIp @filter(eq(ipAddress, "localhost"))
                        partyShop @filter(eq(shopId, "shop1") and eq(partyId, "party1"))
                    }
                }
            }
            """;

    public static final String PAYMENTS_COUNT_QUERY_BY_FINGERPRINT_ROOT_WITH_MINIMAL_DATA = """
            query all() {
                aggregates(func: type(Fingerprint)) @filter(eq(fingerprintData, "finger001")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade {
                        count : count(uid)
                    }
                }
            }
            """;

    public static final String PAYMENTS_COUNT_QUERY_BY_FINGERPRINT_ROOT_WITH_USUAL_DATASET = """
            query all() {
                aggregates(func: type(Fingerprint)) @filter(eq(fingerprintData, "finger001")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade {
                        count : count(uid)
                        partyShop @filter(eq(shopId, "shop1") and eq(partyId, "party1"))
                    }
                }
            }
            """;

    public static final String PAYMENTS_COUNT_QUERY_BY_FINGERPRINT_ROOT_WITH_FULL_DATASET = """
            query all() {
                aggregates(func: type(Fingerprint)) @filter(eq(fingerprintData, "finger001")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured")) @filter(eq(bankCountry, "Russia") and eq(mobile, false) and eq(recurrent, true)) @cascade {
                        count : count(uid)
                        bin @filter(eq(cardBin, "000000"))
                        cardToken @filter(eq(maskedPan, "2424") and eq(tokenId, "token001"))
                        contactEmail @filter(eq(userEmail, "test@test.ru"))
                        operationIp @filter(eq(ipAddress, "localhost"))
                        partyShop @filter(eq(shopId, "shop1") and eq(partyId, "party1"))
                    }
                }
            }
            """;

    public static final String PAYMENTS_COUNT_QUERY_BY_IP_ROOT_WITH_MINIMAL_DATASET = """
            query all() {
                aggregates(func: type(IP)) @filter(eq(ipAddress, "localhost")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade {
                        count : count(uid)
                    }
                }
            }
            """;

    public static final String PAYMENTS_COUNT_QUERY_BY_IP_ROOT_WITH_USUAL_DATASET = """
            query all() {
                aggregates(func: type(IP)) @filter(eq(ipAddress, "localhost")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade {
                        count : count(uid)
                        partyShop @filter(eq(shopId, "shop1") and eq(partyId, "party1"))
                    }
                }
            }
            """;

    public static final String PAYMENTS_COUNT_QUERY_BY_IP_ROOT_WITH_FULL_DATASET = """
            query all() {
                aggregates(func: type(IP)) @filter(eq(ipAddress, "localhost")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured")) @filter(eq(bankCountry, "Russia") and eq(mobile, false) and eq(recurrent, true)) @cascade {
                        count : count(uid)
                        bin @filter(eq(cardBin, "000000"))
                        cardToken @filter(eq(maskedPan, "2424") and eq(tokenId, "token001"))
                        contactEmail @filter(eq(userEmail, "test@test.ru"))
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                        partyShop @filter(eq(shopId, "shop1") and eq(partyId, "party1"))
                    }
                }
            }
            """;

    public static final String PAYMENTS_COUNT_QUERY_BY_PAN_ROOT_WITH_MINIMAL_DATASET = """
            query all() {
                aggregates(func: type(Bin)) @filter(eq(maskedPan, "2424")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade {
                        count : count(uid)
                    }
                }
            }
            """;

    public static final String PAYMENTS_COUNT_QUERY_BY_PAN_ROOT_WITH_USUAL_DATASET = """
            query all() {
                aggregates(func: type(Bin)) @filter(eq(maskedPan, "2424")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade {
                        count : count(uid)
                        partyShop @filter(eq(shopId, "shop1") and eq(partyId, "party1"))
                    }
                }
            }
            """;

    public static final String PAYMENTS_COUNT_QUERY_BY_PAN_ROOT_WITH_FULL_DATASET = """
            query all() {
                aggregates(func: type(Token)) @filter(eq(maskedPan, "2424")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured")) @filter(eq(bankCountry, "Russia") and eq(mobile, false) and eq(recurrent, true)) @cascade {
                        count : count(uid)
                        bin @filter(eq(cardBin, "000000"))
                        contactEmail @filter(eq(userEmail, "test@test.ru"))
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                        operationIp @filter(eq(ipAddress, "localhost"))
                        partyShop @filter(eq(shopId, "shop1") and eq(partyId, "party1"))
                    }
                }
            }
            """;

    public static final String PAYMENTS_COUNT_QUERY_BY_CURRENCY_ROOT_WITH_MINIMAL_DATASET = """
            query all() {
                aggregates(func: type(Payment)) @filter(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured") and eq(currency, "RUB")) @normalize {
                    count : count(uid)
                }
            }
            """;

    public static final String PAYMENTS_COUNT_QUERY_BY_CURRENCY_ROOT_WITH_USUAL_DATASET = """
            query all() {
                aggregates(func: type(Payment)) @filter(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured") and eq(currency, "RUB")) @normalize {
                    count : count(uid)
                    partyShop @filter(eq(shopId, "shop1") and eq(partyId, "party1"))
                }
            }
            """;

    public static final String PAYMENTS_COUNT_QUERY_BY_CURRENCY_ROOT_WITH_FULL_DATASET = """
            query all() {
                aggregates(func: type(Payment)) @filter(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured") and eq(currency, "RUB")) @normalize {
                    count : count(uid)
                    bin @filter(eq(cardBin, "000000"))
                    cardToken @filter(eq(maskedPan, "2424") and eq(tokenId, "token001"))
                    contactEmail @filter(eq(userEmail, "test@test.ru"))
                    fingerprint @filter(eq(fingerprintData, "finger001"))
                    operationIp @filter(eq(ipAddress, "localhost"))
                    partyShop @filter(eq(shopId, "shop1") and eq(partyId, "party1"))
                }
            }
            """;

    public static final String PAYMENTS_COUNT_QUERY_BY_SHOP_ID_ROOT_WITH_MINIMAL_DATASET = """
            query all() {
                aggregates(func: type(PartyShop)) @filter(eq(shopId, "shop1")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade {
                        count : count(uid)
                    }
                }
            }
            """;

    public static final String PAYMENTS_COUNT_QUERY_BY_SHOP_ID_ROOT_WITH_USUAL_DATASET = """
            query all() {
                aggregates(func: type(PartyShop))  @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade {
                        count : count(uid)
                        bin @filter(eq(cardBin, "000000"))
                    }
                }
            }
            """;

    public static final String PAYMENTS_COUNT_QUERY_BY_SHOP_ID_ROOT_WITH_FULL_DATASET = """
            query all() {
                aggregates(func: type(PartyShop)) @filter(eq(shopId, "shop1")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured")) @filter(eq(bankCountry, "Russia") and eq(mobile, false) and eq(recurrent, true)) @cascade {
                        count : count(uid)
                        bin @filter(eq(cardBin, "000000"))
                        cardToken @filter(eq(maskedPan, "2424") and eq(tokenId, "token001"))
                        contactEmail @filter(eq(userEmail, "test@test.ru"))
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                        operationIp @filter(eq(ipAddress, "localhost"))
                    }
                }
            }
            """;

    public static final String PAYMENTS_COUNT_QUERY_BY_PARTY_ID_ROOT_WITH_MINIMAL_DATASET = """
            query all() {
                aggregates(func: type(PartyShop)) @filter(eq(partyId, "party1")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade {
                        count : count(uid)
                    }
                }
            }
            """;

    public static final String PAYMENTS_COUNT_QUERY_BY_PARTY_ID_ROOT_WITH_USUAL_DATASET = """
            query all() {
                aggregates(func: type(PartyShop)) @filter(eq(partyId, "party1")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade {
                        count : count(uid)
                        bin @filter(eq(cardBin, "000000"))
                    }
                }
            }
            """;

    public static final String PAYMENTS_COUNT_QUERY_BY_PARTY_ID_ROOT_WITH_FULL_DATASET = """
            query all() {
                aggregates(func: type(PartyShop)) @filter(eq(shopId, "shop1") and eq(partyId, "party1")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured")) @filter(eq(bankCountry, "Russia") and eq(mobile, false) and eq(recurrent, true)) @cascade {
                        count : count(uid)
                        bin @filter(eq(cardBin, "000000"))
                        cardToken @filter(eq(maskedPan, "2424") and eq(tokenId, "token001"))
                        contactEmail @filter(eq(userEmail, "test@test.ru"))
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                        operationIp @filter(eq(ipAddress, "localhost"))
                    }
                }
            }
            """;

    public static final String PAYMENTS_COUNT_QUERY_BY_COUNTRY_BANK_ROOT_WITH_MINIMAL_DATASET = """
            query all() {
                aggregates(func: type(Payment)) @filter(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured") and eq(bankCountry, "Russia")) @normalize {
                    count : count(uid)
                }
            }
            """;

    public static final String PAYMENTS_COUNT_QUERY_BY_COUNTRY_BANK_ROOT_WITH_USUAL_DATASET = """
            query all() {
                aggregates(func: type(Payment)) @filter(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured") and eq(bankCountry, "Russia")) @normalize {
                    count : count(uid)
                    partyShop @filter(eq(shopId, "shop1") and eq(partyId, "party1"))
                }
            }
            """;

    public static final String PAYMENTS_COUNT_QUERY_BY_COUNTRY_BANK_ROOT_WITH_FULL_DATASET = """
            query all() {
                aggregates(func: type(Payment)) @filter(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured") and eq(bankCountry, "Russia")) @normalize {
                    count : count(uid)
                    bin @filter(eq(cardBin, "000000"))
                    cardToken @filter(eq(maskedPan, "2424") and eq(tokenId, "token001"))
                    contactEmail @filter(eq(userEmail, "test@test.ru"))
                    fingerprint @filter(eq(fingerprintData, "finger001"))
                    operationIp @filter(eq(ipAddress, "localhost"))
                    partyShop @filter(eq(shopId, "shop1") and eq(partyId, "party1"))
                }
            }
            """;

    public static final String PAYMENTS_COUNT_QUERY_BY_COUNTRY_IP_ROOT_WITH_MINIMAL_DATASET = """
            query all() {
                aggregates(func: type(IP)) @filter(eq(ipAddress, "localhost")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade {
                        count : count(uid)
                    }
                }
            }
            """;

    public static final String PAYMENTS_COUNT_QUERY_BY_COUNTRY_IP_ROOT_WITH_USUAL_DATASET = """
            query all() {
                aggregates(func: type(IP)) @filter(eq(ipAddress, "localhost")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade {
                        count : count(uid)
                        partyShop @filter(eq(shopId, "shop1") and eq(partyId, "party1"))
                    }
                }
            }
            """;

    public static final String PAYMENTS_COUNT_QUERY_BY_COUNTRY_IP_ROOT_WITH_FULL_DATASET = """
            query all() {
                aggregates(func: type(IP)) @filter(eq(ipAddress, "localhost")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured")) @filter(eq(bankCountry, "Russia") and eq(mobile, false) and eq(recurrent, true)) @cascade {
                        count : count(uid)
                        bin @filter(eq(cardBin, "000000"))
                        cardToken @filter(eq(maskedPan, "2424") and eq(tokenId, "token001"))
                        contactEmail @filter(eq(userEmail, "test@test.ru"))
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                        partyShop @filter(eq(shopId, "shop1") and eq(partyId, "party1"))
                    }
                }
            }
            """;

    public static final String PAYMENTS_COUNT_QUERY_BY_MOBILE_ROOT_WITH_MINIMAL_DATASET = """
            query all() {
                aggregates(func: type(Payment)) @filter(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured") and eq(mobile, false)) @normalize {
                    count : count(uid)
                }
            }
            """;

    public static final String PAYMENTS_COUNT_QUERY_BY_MOBILE_ROOT_WITH_USUAL_DATASET = """
            query all() {
                aggregates(func: type(Payment)) @filter(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured") and eq(mobile, false)) @normalize {
                    count : count(uid)
                    partyShop @filter(eq(shopId, "shop1") and eq(partyId, "party1"))
                }
            }
            """;

    public static final String PAYMENTS_COUNT_QUERY_BY_MOBILE_ROOT_WITH_FULL_DATASET = """
            query all() {
                aggregates(func: type(Payment)) @filter(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured") and eq(mobile, false) and eq(recurrent, true)) @normalize {
                    count : count(uid)
                    bin @filter(eq(cardBin, "000000"))
                    cardToken @filter(eq(maskedPan, "2424") and eq(tokenId, "token001"))
                    contactEmail @filter(eq(userEmail, "test@test.ru"))
                    fingerprint @filter(eq(fingerprintData, "finger001"))
                    operationIp @filter(eq(ipAddress, "localhost"))
                    partyShop @filter(eq(shopId, "shop1") and eq(partyId, "party1"))
                }
            }
            """;

    public static final String PAYMENTS_COUNT_QUERY_BY_RECURRENT_ROOT_WITH_MINIMAL_DATASET = """
            query all() {
                aggregates(func: type(Payment)) @filter(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured") and eq(recurrent, true)) @normalize {
                    count : count(uid)
                }
            }
            """;

    public static final String PAYMENTS_COUNT_QUERY_BY_RECURRENT_ROOT_WITH_USUAL_DATASET = """
            query all() {
                aggregates(func: type(Payment)) @filter(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured") and eq(recurrent, true)) @normalize {
                    count : count(uid)
                    partyShop @filter(eq(shopId, "shop1") and eq(partyId, "party1"))
                }
            }
            """;

    public static final String PAYMENTS_COUNT_QUERY_BY_RECURRENT_ROOT_WITH_FULL_DATASET = """
            query all() {
                aggregates(func: type(Payment)) @filter(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured") and eq(mobile, false) and eq(recurrent, true)) @normalize {
                    count : count(uid)
                    bin @filter(eq(cardBin, "000000"))
                    cardToken @filter(eq(maskedPan, "2424") and eq(tokenId, "token001"))
                    contactEmail @filter(eq(userEmail, "test@test.ru"))
                    fingerprint @filter(eq(fingerprintData, "finger001"))
                    operationIp @filter(eq(ipAddress, "localhost"))
                    partyShop @filter(eq(shopId, "shop1") and eq(partyId, "party1"))
                }
            }
            """;

}
