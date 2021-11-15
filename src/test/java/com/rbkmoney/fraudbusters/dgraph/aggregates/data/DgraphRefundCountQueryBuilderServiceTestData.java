package com.rbkmoney.fraudbusters.dgraph.aggregates.data;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DgraphRefundCountQueryBuilderServiceTestData {

    public static final String REFUNDS_COUNT_QUERY_BY_TOKEN_ROOT_WITH_MINIMAL_DATA = """
            query all() {
                aggregates(func: type(Token)) @filter(eq(tokenId, "token001")) @normalize {
                    refunds @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "successful"))  @cascade {
                        count : count(uid)
                    }
                }
            }
            """;

    public static final String REFUNDS_COUNT_QUERY_BY_TOKEN_ROOT_WITH_USUAL_DATASET = """
            query all() {
                aggregates(func: type(Token)) @filter(eq(tokenId, "token001")) @normalize {
                    refunds @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "successful"))  @cascade {
                        count : count(uid)
                        party @filter(eq(partyId, "party1"))
                        shop @filter(eq(shopId, "shop1"))
                    }
                }
            }
            """;

    public static final String REFUNDS_COUNT_QUERY_BY_TOKEN_ROOT_WITH_FULL_DATASET = """
            query all() {
                aggregates(func: type(Token)) @filter(eq(maskedPan, "2424") and eq(tokenId, "token001")) @normalize {
                    refunds @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "successful"))  @cascade {
                        count : count(uid)
                        bin @filter(eq(cardBin, "000000"))
                        contactEmail @filter(eq(userEmail, "test@test.ru"))
                        country @filter(eq(countryName, "Russia"))
                        currency @filter(eq(currencyCode, "RUB"))
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                        operationIp @filter(eq(ipAddress, "localhost"))
                        party @filter(eq(partyId, "party1"))
                        shop @filter(eq(shopId, "shop1"))
                        sourcePayment @filter(eq(mobile, false) and eq(recurrent, true))
                    }
                }
            }
            """;

    public static final String REFUNDS_COUNT_QUERY_BY_EMAIL_ROOT_WITH_MINIMAL_DATA = """
            query all() {
                aggregates(func: type(Email)) @filter(eq(userEmail, "test@test.ru")) @normalize {
                    refunds @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "successful"))  @cascade {
                        count : count(uid)
                    }
                }
            }
            """;

    public static final String REFUNDS_COUNT_QUERY_BY_EMAIL_ROOT_WITH_USUAL_DATASET = """
            query all() {
                aggregates(func: type(Email)) @filter(eq(userEmail, "test@test.ru")) @normalize {
                    refunds @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "successful"))  @cascade {
                        count : count(uid)
                        party @filter(eq(partyId, "party1"))
                        shop @filter(eq(shopId, "shop1"))
                    }
                }
            }
            """;

    public static final String REFUNDS_COUNT_QUERY_BY_EMAIL_ROOT_WITH_FULL_DATASET = """
            query all() {
                aggregates(func: type(Email)) @filter(eq(userEmail, "test@test.ru")) @normalize {
                    refunds @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "successful"))  @cascade {
                        count : count(uid)
                        bin @filter(eq(cardBin, "000000"))
                        cardToken @filter(eq(maskedPan, "2424") and eq(tokenId, "token001"))
                        country @filter(eq(countryName, "Russia"))
                        currency @filter(eq(currencyCode, "RUB"))
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                        operationIp @filter(eq(ipAddress, "localhost"))
                        party @filter(eq(partyId, "party1"))
                        shop @filter(eq(shopId, "shop1"))
                        sourcePayment @filter(eq(mobile, false) and eq(recurrent, true))
                    }
                }
            }
            """;

    public static final String REFUNDS_COUNT_QUERY_BY_IP_ROOT_WITH_MINIMAL_DATA = """
            query all() {
                aggregates(func: type(IP)) @filter(eq(ipAddress, "localhost")) @normalize {
                    refunds @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "successful"))  @cascade {
                        count : count(uid)
                    }
                }
            }
            """;

    public static final String REFUNDS_COUNT_QUERY_BY_IP_ROOT_WITH_USUAL_DATASET = """
            query all() {
                aggregates(func: type(IP)) @filter(eq(ipAddress, "localhost")) @normalize {
                    refunds @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "successful"))  @cascade {
                        count : count(uid)
                        party @filter(eq(partyId, "party1"))
                        shop @filter(eq(shopId, "shop1"))
                    }
                }
            }
            """;

    public static final String REFUNDS_COUNT_QUERY_BY_IP_ROOT_WITH_FULL_DATASET = """
            query all() {
                aggregates(func: type(IP)) @filter(eq(ipAddress, "localhost")) @normalize {
                    refunds @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "successful"))  @cascade {
                        count : count(uid)
                        bin @filter(eq(cardBin, "000000"))
                        cardToken @filter(eq(maskedPan, "2424") and eq(tokenId, "token001"))
                        contactEmail @filter(eq(userEmail, "test@test.ru"))
                        country @filter(eq(countryName, "Russia"))
                        currency @filter(eq(currencyCode, "RUB"))
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                        party @filter(eq(partyId, "party1"))
                        shop @filter(eq(shopId, "shop1"))
                        sourcePayment @filter(eq(mobile, false) and eq(recurrent, true))
                    }
                }
            }
            """;

    public static final String REFUNDS_COUNT_QUERY_BY_FINGERPRINT_ROOT_WITH_MINIMAL_DATA = """
            query all() {
                aggregates(func: type(Fingerprint)) @filter(eq(fingerprintData, "finger001")) @normalize {
                    refunds @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "successful"))  @cascade {
                        count : count(uid)
                    }
                }
            }
            """;

    public static final String REFUNDS_COUNT_QUERY_BY_FINGERPRINT_ROOT_WITH_USUAL_DATASET = """
            query all() {
                aggregates(func: type(Fingerprint)) @filter(eq(fingerprintData, "finger001")) @normalize {
                    refunds @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "successful"))  @cascade {
                        count : count(uid)
                        party @filter(eq(partyId, "party1"))
                        shop @filter(eq(shopId, "shop1"))
                    }
                }
            }
            """;

    public static final String REFUNDS_COUNT_QUERY_BY_FINGERPRINT_ROOT_WITH_FULL_DATASET = """
            query all() {
                aggregates(func: type(Fingerprint)) @filter(eq(fingerprintData, "finger001")) @normalize {
                    refunds @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "successful"))  @cascade {
                        count : count(uid)
                        bin @filter(eq(cardBin, "000000"))
                        cardToken @filter(eq(maskedPan, "2424") and eq(tokenId, "token001"))
                        contactEmail @filter(eq(userEmail, "test@test.ru"))
                        country @filter(eq(countryName, "Russia"))
                        currency @filter(eq(currencyCode, "RUB"))
                        operationIp @filter(eq(ipAddress, "localhost"))
                        party @filter(eq(partyId, "party1"))
                        shop @filter(eq(shopId, "shop1"))
                        sourcePayment @filter(eq(mobile, false) and eq(recurrent, true))
                    }
                }
            }
            """;

    public static final String REFUNDS_COUNT_QUERY_BY_COUNTRY_BANK_ROOT_WITH_MINIMAL_DATA = """
            query all() {
                aggregates(func: type(Country)) @filter(eq(countryName, "Russia")) @normalize {
                    refunds @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "successful"))  @cascade {
                        count : count(uid)
                    }
                }
            }
            """;

    public static final String REFUNDS_COUNT_QUERY_BY_COUNTRY_BANK_ROOT_WITH_USUAL_DATASET = """
            query all() {
                aggregates(func: type(Country)) @filter(eq(countryName, "Russia")) @normalize {
                    refunds @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "successful"))  @cascade {
                        count : count(uid)
                        party @filter(eq(partyId, "party1"))
                        shop @filter(eq(shopId, "shop1"))
                    }
                }
            }
            """;

    public static final String REFUNDS_COUNT_QUERY_BY_COUNTRY_BANK_ROOT_WITH_FULL_DATASET = """
            query all() {
                aggregates(func: type(Country)) @filter(eq(countryName, "Russia")) @normalize {
                    refunds @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "successful"))  @cascade {
                        count : count(uid)
                        bin @filter(eq(cardBin, "000000"))
                        cardToken @filter(eq(maskedPan, "2424") and eq(tokenId, "token001"))
                        contactEmail @filter(eq(userEmail, "test@test.ru"))
                        currency @filter(eq(currencyCode, "RUB"))
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                        operationIp @filter(eq(ipAddress, "localhost"))
                        party @filter(eq(partyId, "party1"))
                        shop @filter(eq(shopId, "shop1"))
                        sourcePayment @filter(eq(mobile, false) and eq(recurrent, true))
                    }
                }
            }
            """;

    public static final String REFUNDS_COUNT_QUERY_BY_COUNTRY_IP_ROOT_WITH_MINIMAL_DATA = """
            query all() {
                aggregates(func: type(IP)) @filter(eq(ipAddress, "localhost")) @normalize {
                    refunds @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "successful"))  @cascade {
                        count : count(uid)
                    }
                }
            }
            """;

    public static final String REFUNDS_COUNT_QUERY_BY_COUNTRY_IP_ROOT_WITH_USUAL_DATASET = """
            query all() {
                aggregates(func: type(IP)) @filter(eq(ipAddress, "localhost")) @normalize {
                    refunds @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "successful"))  @cascade {
                        count : count(uid)
                        party @filter(eq(partyId, "party1"))
                        shop @filter(eq(shopId, "shop1"))
                    }
                }
            }
            """;

    public static final String REFUNDS_COUNT_QUERY_BY_COUNTRY_IP_ROOT_WITH_FULL_DATASET = """
            query all() {
                aggregates(func: type(IP)) @filter(eq(ipAddress, "localhost")) @normalize {
                    refunds @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "successful"))  @cascade {
                        count : count(uid)
                        bin @filter(eq(cardBin, "000000"))
                        cardToken @filter(eq(maskedPan, "2424") and eq(tokenId, "token001"))
                        contactEmail @filter(eq(userEmail, "test@test.ru"))
                        country @filter(eq(countryName, "Russia"))
                        currency @filter(eq(currencyCode, "RUB"))
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                        party @filter(eq(partyId, "party1"))
                        shop @filter(eq(shopId, "shop1"))
                        sourcePayment @filter(eq(mobile, false) and eq(recurrent, true))
                    }
                }
            }
            """;

    public static final String REFUNDS_COUNT_QUERY_BY_BIN_ROOT_WITH_MINIMAL_DATA = """
            query all() {
                aggregates(func: type(Bin)) @filter(eq(cardBin, "000000")) @normalize {
                    refunds @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "successful"))  @cascade {
                        count : count(uid)
                    }
                }
            }
            """;

    public static final String REFUNDS_COUNT_QUERY_BY_BIN_ROOT_WITH_USUAL_DATASET = """
            query all() {
                aggregates(func: type(Bin)) @filter(eq(cardBin, "000000")) @normalize {
                    refunds @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "successful"))  @cascade {
                        count : count(uid)
                        party @filter(eq(partyId, "party1"))
                        shop @filter(eq(shopId, "shop1"))
                    }
                }
            }
            """;

    public static final String REFUNDS_COUNT_QUERY_BY_BIN_ROOT_WITH_FULL_DATASET = """
            query all() {
                aggregates(func: type(Bin)) @filter(eq(cardBin, "000000")) @normalize {
                    refunds @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "successful"))  @cascade {
                        count : count(uid)
                        cardToken @filter(eq(maskedPan, "2424") and eq(tokenId, "token001"))
                        contactEmail @filter(eq(userEmail, "test@test.ru"))
                        country @filter(eq(countryName, "Russia"))
                        currency @filter(eq(currencyCode, "RUB"))
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                        operationIp @filter(eq(ipAddress, "localhost"))
                        party @filter(eq(partyId, "party1"))
                        shop @filter(eq(shopId, "shop1"))
                        sourcePayment @filter(eq(mobile, false) and eq(recurrent, true))
                    }
                }
            }
            """;

    public static final String REFUNDS_COUNT_QUERY_BY_PAN_ROOT_WITH_MINIMAL_DATA = """
            query all() {
                aggregates(func: type(Token)) @filter(eq(maskedPan, "2424")) @normalize {
                    refunds @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "successful"))  @cascade {
                        count : count(uid)
                    }
                }
            }
            """;

    public static final String REFUNDS_COUNT_QUERY_BY_PAN_ROOT_WITH_USUAL_DATASET = """
            query all() {
                aggregates(func: type(Token)) @filter(eq(maskedPan, "2424")) @normalize {
                    refunds @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "successful"))  @cascade {
                        count : count(uid)
                        party @filter(eq(partyId, "party1"))
                        shop @filter(eq(shopId, "shop1"))
                    }
                }
            }
            """;

    public static final String REFUNDS_COUNT_QUERY_BY_PAN_ROOT_WITH_FULL_DATASET = """
            query all() {
                aggregates(func: type(Token)) @filter(eq(maskedPan, "2424") and eq(tokenId, "token001")) @normalize {
                    refunds @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "successful"))  @cascade {
                        count : count(uid)
                        bin @filter(eq(cardBin, "000000"))
                        contactEmail @filter(eq(userEmail, "test@test.ru"))
                        country @filter(eq(countryName, "Russia"))
                        currency @filter(eq(currencyCode, "RUB"))
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                        operationIp @filter(eq(ipAddress, "localhost"))
                        party @filter(eq(partyId, "party1"))
                        shop @filter(eq(shopId, "shop1"))
                        sourcePayment @filter(eq(mobile, false) and eq(recurrent, true))
                    }
                }
            }
            """;

    public static final String REFUNDS_COUNT_QUERY_BY_CURRENCY_ROOT_WITH_MINIMAL_DATA = """
            query all() {
                aggregates(func: type(Currency)) @filter(eq(currencyCode, "RUB")) @normalize {
                    refunds @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "successful"))  @cascade {
                        count : count(uid)
                    }
                }
            }
            """;

    public static final String REFUNDS_COUNT_QUERY_BY_CURRENCY_ROOT_WITH_USUAL_DATASET = """
            query all() {
                aggregates(func: type(Currency)) @filter(eq(currencyCode, "RUB")) @normalize {
                    refunds @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "successful"))  @cascade {
                        count : count(uid)
                        party @filter(eq(partyId, "party1"))
                        shop @filter(eq(shopId, "shop1"))
                    }
                }
            }
            """;

    public static final String REFUNDS_COUNT_QUERY_BY_CURRENCY_ROOT_WITH_FULL_DATASET = """
            query all() {
                aggregates(func: type(Currency)) @filter(eq(currencyCode, "RUB")) @normalize {
                    refunds @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "successful"))  @cascade {
                        count : count(uid)
                        bin @filter(eq(cardBin, "000000"))
                        cardToken @filter(eq(maskedPan, "2424") and eq(tokenId, "token001"))
                        contactEmail @filter(eq(userEmail, "test@test.ru"))
                        country @filter(eq(countryName, "Russia"))
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                        operationIp @filter(eq(ipAddress, "localhost"))
                        party @filter(eq(partyId, "party1"))
                        shop @filter(eq(shopId, "shop1"))
                        sourcePayment @filter(eq(mobile, false) and eq(recurrent, true))
                    }
                }
            }
            """;

    public static final String REFUNDS_COUNT_QUERY_BY_SHOP_ID_ROOT_WITH_MINIMAL_DATA = """
            query all() {
                aggregates(func: type(Shop)) @filter(eq(shopId, "shop1")) @normalize {
                    refunds @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "successful"))  @cascade {
                        count : count(uid)
                    }
                }
            }
            """;

    public static final String REFUNDS_COUNT_QUERY_BY_SHOP_ID_ROOT_WITH_USUAL_DATASET = """
            query all() {
                aggregates(func: type(Shop)) @filter(eq(shopId, "shop1")) @normalize {
                    refunds @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "successful"))  @cascade {
                        count : count(uid)
                        bin @filter(eq(cardBin, "000000"))
                        party @filter(eq(partyId, "party1"))
                    }
                }
            }
            """;

    public static final String REFUNDS_COUNT_QUERY_BY_SHOP_ID_ROOT_WITH_FULL_DATASET = """
            query all() {
                aggregates(func: type(Shop)) @filter(eq(shopId, "shop1")) @normalize {
                    refunds @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "successful"))  @cascade {
                        count : count(uid)
                        bin @filter(eq(cardBin, "000000"))
                        cardToken @filter(eq(maskedPan, "2424") and eq(tokenId, "token001"))
                        contactEmail @filter(eq(userEmail, "test@test.ru"))
                        country @filter(eq(countryName, "Russia"))
                        currency @filter(eq(currencyCode, "RUB"))
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                        operationIp @filter(eq(ipAddress, "localhost"))
                        party @filter(eq(partyId, "party1"))
                        sourcePayment @filter(eq(mobile, false) and eq(recurrent, true))
                    }
                }
            }
            """;

    public static final String REFUNDS_COUNT_QUERY_BY_PARTY_ID_ROOT_WITH_MINIMAL_DATA = """
            query all() {
                aggregates(func: type(Party)) @filter(eq(partyId, "party1")) @normalize {
                    refunds @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "successful"))  @cascade {
                        count : count(uid)
                    }
                }
            }
            """;

    public static final String REFUNDS_COUNT_QUERY_BY_PARTY_ID_ROOT_WITH_USUAL_DATASET = """
            query all() {
                aggregates(func: type(Party)) @filter(eq(partyId, "party1")) @normalize {
                    refunds @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "successful"))  @cascade {
                        count : count(uid)
                        bin @filter(eq(cardBin, "000000"))
                    }
                }
            }
            """;

    public static final String REFUNDS_COUNT_QUERY_BY_PARTY_ID_ROOT_WITH_FULL_DATASET = """
            query all() {
                aggregates(func: type(Party)) @filter(eq(partyId, "party1")) @normalize {
                    refunds @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "successful"))  @cascade {
                        count : count(uid)
                        bin @filter(eq(cardBin, "000000"))
                        cardToken @filter(eq(maskedPan, "2424") and eq(tokenId, "token001"))
                        contactEmail @filter(eq(userEmail, "test@test.ru"))
                        country @filter(eq(countryName, "Russia"))
                        currency @filter(eq(currencyCode, "RUB"))
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                        operationIp @filter(eq(ipAddress, "localhost"))
                        shop @filter(eq(shopId, "shop1"))
                        sourcePayment @filter(eq(mobile, false) and eq(recurrent, true))
                    }
                }
            }
            """;

    public static final String REFUNDS_COUNT_QUERY_BY_MOBILE_ROOT_WITH_MINIMAL_DATA = """
            query all() {
                aggregates(func: type(Payment)) @filter(eq(mobile, false)) @normalize {
                    refunds @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "successful"))  @cascade {
                        count : count(uid)
                    }
                }
            }
            """;

    public static final String REFUNDS_COUNT_QUERY_BY_MOBILE_ROOT_WITH_USUAL_DATASET = """
            query all() {
                aggregates(func: type(Payment)) @filter(eq(mobile, false)) @normalize {
                    refunds @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "successful"))  @cascade {
                        count : count(uid)
                        party @filter(eq(partyId, "party1"))
                        shop @filter(eq(shopId, "shop1"))
                    }
                }
            }
            """;

    public static final String REFUNDS_COUNT_QUERY_BY_MOBILE_ROOT_WITH_FULL_DATASET = """
            query all() {
                aggregates(func: type(Payment)) @filter(eq(mobile, false) and eq(recurrent, true)) @normalize {
                    refunds @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "successful"))  @cascade {
                        count : count(uid)
                        bin @filter(eq(cardBin, "000000"))
                        cardToken @filter(eq(maskedPan, "2424") and eq(tokenId, "token001"))
                        contactEmail @filter(eq(userEmail, "test@test.ru"))
                        country @filter(eq(countryName, "Russia"))
                        currency @filter(eq(currencyCode, "RUB"))
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                        operationIp @filter(eq(ipAddress, "localhost"))
                        party @filter(eq(partyId, "party1"))
                        shop @filter(eq(shopId, "shop1"))
                    }
                }
            }
            """;

    public static final String REFUNDS_COUNT_QUERY_BY_RECURRENT_ROOT_WITH_MINIMAL_DATA = """
            query all() {
                aggregates(func: type(Payment)) @filter(eq(recurrent, true)) @normalize {
                    refunds @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "successful"))  @cascade {
                        count : count(uid)
                    }
                }
            }
            """;

    public static final String REFUNDS_COUNT_QUERY_BY_RECURRENT_ROOT_WITH_USUAL_DATASET = """
            query all() {
                aggregates(func: type(Payment)) @filter(eq(recurrent, true)) @normalize {
                    refunds @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "successful"))  @cascade {
                        count : count(uid)
                        party @filter(eq(partyId, "party1"))
                        shop @filter(eq(shopId, "shop1"))
                    }
                }
            }
            """;

    public static final String REFUNDS_COUNT_QUERY_BY_RECURRENT_ROOT_WITH_FULL_DATASET = """
            query all() {
                aggregates(func: type(Payment)) @filter(eq(mobile, false) and eq(recurrent, true)) @normalize {
                    refunds @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "successful"))  @cascade {
                        count : count(uid)
                        bin @filter(eq(cardBin, "000000"))
                        cardToken @filter(eq(maskedPan, "2424") and eq(tokenId, "token001"))
                        contactEmail @filter(eq(userEmail, "test@test.ru"))
                        country @filter(eq(countryName, "Russia"))
                        currency @filter(eq(currencyCode, "RUB"))
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                        operationIp @filter(eq(ipAddress, "localhost"))
                        party @filter(eq(partyId, "party1"))
                        shop @filter(eq(shopId, "shop1"))
                    }
                }
            }
            """;

}
