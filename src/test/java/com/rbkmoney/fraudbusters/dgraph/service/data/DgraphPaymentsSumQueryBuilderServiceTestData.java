package com.rbkmoney.fraudbusters.dgraph.service.data;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DgraphPaymentsSumQueryBuilderServiceTestData {

    public static final String PAYMENTS_SUM_QUERY_BY_TOKEN_ROOT_WITH_MINIMAL_DATA = """
            query all() {
                aggregates(func: type(Token)) @filter(eq(tokenId, "token001")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade {
                        amount as amount
                    }
                    sum : sum(val(amount))
                }
            }
            """;

    public static final String PAYMENTS_SUM_QUERY_BY_TOKEN_ROOT_WITH_USUAL_DATASET = """
            query all() {
                aggregates(func: type(Token)) @filter(eq(tokenId, "token001")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade {
                        amount as amount
                        party @filter(eq(partyId, "party1"))
                        shop @filter(eq(shopId, "shop1"))
                    }
                    sum : sum(val(amount))
                }
            }
            """;

    public static final String PAYMENTS_SUM_QUERY_BY_TOKEN_ROOT_WITH_FULL_DATASET = """
            query all() {
                aggregates(func: type(Token)) @filter(eq(maskedPan, "2424") and eq(tokenId, "token001")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured")) @filter(eq(mobile, false) and eq(recurrent, true)) @cascade {
                        amount as amount
                        bin @filter(eq(cardBin, "000000"))
                        contactEmail @filter(eq(userEmail, "test@test.ru"))
                        country @filter(eq(countryName, "Russia"))
                        currency @filter(eq(currencyCode, "RUB"))
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                        operationIp @filter(eq(ipAddress, "localhost"))
                        party @filter(eq(partyId, "party1"))
                        shop @filter(eq(shopId, "shop1"))
                    }
                    sum : sum(val(amount))
                }
            }
            """;

    public static final String PAYMENTS_SUM_QUERY_BY_EMAIL_ROOT_WITH_MINIMAL_DATA = """
            query all() {
                aggregates(func: type(Email)) @filter(eq(userEmail, "test@test.ru")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade {
                        amount as amount
                    }
                    sum : sum(val(amount))
                }
            }
            """;

    public static final String PAYMENTS_SUM_QUERY_BY_EMAIL_ROOT_WITH_USUAL_DATASET = """
            query all() {
                aggregates(func: type(Email)) @filter(eq(userEmail, "test@test.ru")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade {
                        amount as amount
                        party @filter(eq(partyId, "party1"))
                        shop @filter(eq(shopId, "shop1"))
                    }
                    sum : sum(val(amount))
                }
            }
            """;

    public static final String PAYMENTS_SUM_QUERY_BY_EMAIL_ROOT_WITH_FULL_DATASET = """
            query all() {
                aggregates(func: type(Email)) @filter(eq(userEmail, "test@test.ru")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured")) @filter(eq(mobile, false) and eq(recurrent, true)) @cascade {
                        amount as amount
                        bin @filter(eq(cardBin, "000000"))
                        cardToken @filter(eq(maskedPan, "2424") and eq(tokenId, "token001"))
                        country @filter(eq(countryName, "Russia"))
                        currency @filter(eq(currencyCode, "RUB"))
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                        operationIp @filter(eq(ipAddress, "localhost"))
                        party @filter(eq(partyId, "party1"))
                        shop @filter(eq(shopId, "shop1"))
                    }
                    sum : sum(val(amount))
                }
            }
            """;

    public static final String PAYMENTS_SUM_QUERY_BY_FINGERPRINT_ROOT_WITH_MINIMAL_DATA = """
            query all() {
                aggregates(func: type(Fingerprint)) @filter(eq(fingerprintData, "finger001")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade {
                        amount as amount
                    }
                    sum : sum(val(amount))
                }
            }
            """;

    public static final String PAYMENTS_SUM_QUERY_BY_FINGERPRINT_ROOT_WITH_USUAL_DATASET = """
            query all() {
                aggregates(func: type(Fingerprint)) @filter(eq(fingerprintData, "finger001")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade {
                        amount as amount
                        party @filter(eq(partyId, "party1"))
                        shop @filter(eq(shopId, "shop1"))
                    }
                    sum : sum(val(amount))
                }
            }
            """;

    public static final String PAYMENTS_SUM_QUERY_BY_FINGERPRINT_ROOT_WITH_FULL_DATASET = """
            query all() {
                aggregates(func: type(Fingerprint)) @filter(eq(fingerprintData, "finger001")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured")) @filter(eq(mobile, false) and eq(recurrent, true)) @cascade {
                        amount as amount
                        bin @filter(eq(cardBin, "000000"))
                        cardToken @filter(eq(maskedPan, "2424") and eq(tokenId, "token001"))
                        contactEmail @filter(eq(userEmail, "test@test.ru"))
                        country @filter(eq(countryName, "Russia"))
                        currency @filter(eq(currencyCode, "RUB"))
                        operationIp @filter(eq(ipAddress, "localhost"))
                        party @filter(eq(partyId, "party1"))
                        shop @filter(eq(shopId, "shop1"))
                    }
                    sum : sum(val(amount))
                }
            }
            """;

    public static final String PAYMENTS_SUM_QUERY_BY_IP_ROOT_WITH_MINIMAL_DATASET = """
            query all() {
                aggregates(func: type(Ip)) @filter(eq(ipAddress, "localhost")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade {
                        amount as amount
                    }
                    sum : sum(val(amount))
                }
            }
            """;

    public static final String PAYMENTS_SUM_QUERY_BY_IP_ROOT_WITH_USUAL_DATASET = """
            query all() {
                aggregates(func: type(Ip)) @filter(eq(ipAddress, "localhost")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade {
                        amount as amount
                        party @filter(eq(partyId, "party1"))
                        shop @filter(eq(shopId, "shop1"))
                    }
                    sum : sum(val(amount))
                }
            }
            """;

    public static final String PAYMENTS_SUM_QUERY_BY_IP_ROOT_WITH_FULL_DATASET = """
            query all() {
                aggregates(func: type(Ip)) @filter(eq(ipAddress, "localhost")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured")) @filter(eq(mobile, false) and eq(recurrent, true)) @cascade {
                        amount as amount
                        bin @filter(eq(cardBin, "000000"))
                        cardToken @filter(eq(maskedPan, "2424") and eq(tokenId, "token001"))
                        contactEmail @filter(eq(userEmail, "test@test.ru"))
                        country @filter(eq(countryName, "Russia"))
                        currency @filter(eq(currencyCode, "RUB"))
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                        party @filter(eq(partyId, "party1"))
                        shop @filter(eq(shopId, "shop1"))
                    }
                    sum : sum(val(amount))
                }
            }
            """;

    public static final String PAYMENTS_SUM_QUERY_BY_PAN_ROOT_WITH_MINIMAL_DATASET = """
            query all() {
                aggregates(func: type(Bin)) @filter(eq(maskedPan, "2424")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade {
                        amount as amount
                    }
                    sum : sum(val(amount))
                }
            }
            """;

    public static final String PAYMENTS_SUM_QUERY_BY_PAN_ROOT_WITH_USUAL_DATASET = """
            query all() {
                aggregates(func: type(Bin)) @filter(eq(maskedPan, "2424")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade {
                        amount as amount
                        party @filter(eq(partyId, "party1"))
                        shop @filter(eq(shopId, "shop1"))
                    }
                    sum : sum(val(amount))
                }
            }
            """;

    public static final String PAYMENTS_SUM_QUERY_BY_PAN_ROOT_WITH_FULL_DATASET = """
            query all() {
                aggregates(func: type(Token)) @filter(eq(maskedPan, "2424")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured")) @filter(eq(mobile, false) and eq(recurrent, true)) @cascade {
                        amount as amount
                        bin @filter(eq(cardBin, "000000"))
                        contactEmail @filter(eq(userEmail, "test@test.ru"))
                        country @filter(eq(countryName, "Russia"))
                        currency @filter(eq(currencyCode, "RUB"))
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                        operationIp @filter(eq(ipAddress, "localhost"))
                        party @filter(eq(partyId, "party1"))
                        shop @filter(eq(shopId, "shop1"))
                    }
                    sum : sum(val(amount))
                }
            }
            """;

    public static final String PAYMENTS_SUM_QUERY_BY_CURRENCY_ROOT_WITH_MINIMAL_DATASET = """
            query all() {
                aggregates(func: type(Currency)) @filter(eq(currencyCode, "RUB")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade {
                        amount as amount
                    }
                    sum : sum(val(amount))
                }
            }
            """;

    public static final String PAYMENTS_SUM_QUERY_BY_CURRENCY_ROOT_WITH_USUAL_DATASET = """
            query all() {
                aggregates(func: type(Currency)) @filter(eq(currencyCode, "RUB")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade {
                        amount as amount
                        party @filter(eq(partyId, "party1"))
                        shop @filter(eq(shopId, "shop1"))
                    }
                    sum : sum(val(amount))
                }
            }
            """;

    public static final String PAYMENTS_SUM_QUERY_BY_CURRENCY_ROOT_WITH_FULL_DATASET = """
            query all() {
                aggregates(func: type(Currency)) @filter(eq(currencyCode, "RUB")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured")) @filter(eq(mobile, false) and eq(recurrent, true)) @cascade {
                        amount as amount
                        bin @filter(eq(cardBin, "000000"))
                        cardToken @filter(eq(maskedPan, "2424") and eq(tokenId, "token001"))
                        contactEmail @filter(eq(userEmail, "test@test.ru"))
                        country @filter(eq(countryName, "Russia"))
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                        operationIp @filter(eq(ipAddress, "localhost"))
                        party @filter(eq(partyId, "party1"))
                        shop @filter(eq(shopId, "shop1"))
                    }
                    sum : sum(val(amount))
                }
            }
            """;

    public static final String PAYMENTS_SUM_QUERY_BY_SHOP_ID_ROOT_WITH_MINIMAL_DATASET = """
            query all() {
                aggregates(func: type(Shop)) @filter(eq(shopId, "shop1")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade {
                        amount as amount
                    }
                    sum : sum(val(amount))
                }
            }
            """;

    public static final String PAYMENTS_SUM_QUERY_BY_SHOP_ID_ROOT_WITH_USUAL_DATASET = """
            query all() {
                aggregates(func: type(Shop))  @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade {
                        amount as amount
                        bin @filter(eq(cardBin, "000000"))
                    }
                    sum : sum(val(amount))
                }
            }
            """;

    public static final String PAYMENTS_SUM_QUERY_BY_SHOP_ID_ROOT_WITH_FULL_DATASET = """
            query all() {
                aggregates(func: type(Shop)) @filter(eq(shopId, "shop1")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured")) @filter(eq(mobile, false) and eq(recurrent, true)) @cascade {
                        amount as amount
                        bin @filter(eq(cardBin, "000000"))
                        cardToken @filter(eq(maskedPan, "2424") and eq(tokenId, "token001"))
                        contactEmail @filter(eq(userEmail, "test@test.ru"))
                        country @filter(eq(countryName, "Russia"))
                        currency @filter(eq(currencyCode, "RUB"))
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                        operationIp @filter(eq(ipAddress, "localhost"))
                        party @filter(eq(partyId, "party1"))
                    }
                    sum : sum(val(amount))
                }
            }
            """;

    public static final String PAYMENTS_SUM_QUERY_BY_PARTY_ID_ROOT_WITH_MINIMAL_DATASET = """
            query all() {
                aggregates(func: type(Party)) @filter(eq(partyId, "party1")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade {
                        amount as amount
                    }
                    sum : sum(val(amount))
                }
            }
            """;

    public static final String PAYMENTS_SUM_QUERY_BY_PARTY_ID_ROOT_WITH_USUAL_DATASET = """
            query all() {
                aggregates(func: type(Party)) @filter(eq(partyId, "party1")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade {
                        amount as amount
                        bin @filter(eq(cardBin, "000000"))
                    }
                    sum : sum(val(amount))
                }
            }
            """;

    public static final String PAYMENTS_SUM_QUERY_BY_PARTY_ID_ROOT_WITH_FULL_DATASET = """
            query all() {
                aggregates(func: type(Party)) @filter(eq(partyId, "party1")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured")) @filter(eq(mobile, false) and eq(recurrent, true)) @cascade {
                        amount as amount
                        bin @filter(eq(cardBin, "000000"))
                        cardToken @filter(eq(maskedPan, "2424") and eq(tokenId, "token001"))
                        contactEmail @filter(eq(userEmail, "test@test.ru"))
                        country @filter(eq(countryName, "Russia"))
                        currency @filter(eq(currencyCode, "RUB"))
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                        operationIp @filter(eq(ipAddress, "localhost"))
                        shop @filter(eq(shopId, "shop1"))
                    }
                    sum : sum(val(amount))
                }
            }
            """;

    public static final String PAYMENTS_SUM_QUERY_BY_COUNTRY_BANK_ROOT_WITH_MINIMAL_DATASET = """
            query all() {
                aggregates(func: type(Country)) @filter(eq(countryName, "Russia")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade {
                        amount as amount
                    }
                    sum : sum(val(amount))
                }
            }
            """;

    public static final String PAYMENTS_SUM_QUERY_BY_COUNTRY_BANK_ROOT_WITH_USUAL_DATASET = """
            query all() {
                aggregates(func: type(Country)) @filter(eq(countryName, "Russia")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade {
                        amount as amount
                        party @filter(eq(partyId, "party1"))
                        shop @filter(eq(shopId, "shop1"))
                    }
                    sum : sum(val(amount))
                }
            }
            """;

    public static final String PAYMENTS_SUM_QUERY_BY_COUNTRY_BANK_ROOT_WITH_FULL_DATASET = """
            query all() {
                aggregates(func: type(Country)) @filter(eq(countryName, "Russia")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured")) @filter(eq(mobile, false) and eq(recurrent, true)) @cascade {
                        amount as amount
                        bin @filter(eq(cardBin, "000000"))
                        cardToken @filter(eq(maskedPan, "2424") and eq(tokenId, "token001"))
                        contactEmail @filter(eq(userEmail, "test@test.ru"))
                        currency @filter(eq(currencyCode, "RUB"))
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                        operationIp @filter(eq(ipAddress, "localhost"))
                        party @filter(eq(partyId, "party1"))
                        shop @filter(eq(shopId, "shop1"))
                    }
                    sum : sum(val(amount))
                }
            }
            """;

    public static final String PAYMENTS_SUM_QUERY_BY_COUNTRY_IP_ROOT_WITH_MINIMAL_DATASET = """
            query all() {
                aggregates(func: type(Ip)) @filter(eq(ipAddress, "localhost")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade {
                        amount as amount
                    }
                    sum : sum(val(amount))
                }
            }
            """;

    public static final String PAYMENTS_SUM_QUERY_BY_COUNTRY_IP_ROOT_WITH_USUAL_DATASET = """
            query all() {
                aggregates(func: type(Ip)) @filter(eq(ipAddress, "localhost")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured"))  @cascade {
                        amount as amount
                        party @filter(eq(partyId, "party1"))
                        shop @filter(eq(shopId, "shop1"))
                    }
                    sum : sum(val(amount))
                }
            }
            """;

    public static final String PAYMENTS_SUM_QUERY_BY_COUNTRY_IP_ROOT_WITH_FULL_DATASET = """
            query all() {
                aggregates(func: type(Ip)) @filter(eq(ipAddress, "localhost")) @normalize {
                    payments @facets(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured")) @filter(eq(mobile, false) and eq(recurrent, true)) @cascade {
                        amount as amount
                        bin @filter(eq(cardBin, "000000"))
                        cardToken @filter(eq(maskedPan, "2424") and eq(tokenId, "token001"))
                        contactEmail @filter(eq(userEmail, "test@test.ru"))
                        country @filter(eq(countryName, "Russia"))
                        currency @filter(eq(currencyCode, "RUB"))
                        fingerprint @filter(eq(fingerprintData, "finger001"))
                        party @filter(eq(partyId, "party1"))
                        shop @filter(eq(shopId, "shop1"))
                    }
                    sum : sum(val(amount))
                }
            }
            """;

    public static final String PAYMENTS_SUM_QUERY_BY_MOBILE_ROOT_WITH_MINIMAL_DATASET = """
            query all() {
                src as var(func: type(Payment)) @filter(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured") and eq(mobile, false)) @cascade {
                }
                        
                var(func: type(Payment)) @filter(uid(src)) {
                    amount as amount
                }
                        
                aggregates() {
                    sum : sum(val(amount))
                }
            }
            """;

    public static final String PAYMENTS_SUM_QUERY_BY_MOBILE_ROOT_WITH_USUAL_DATASET = """
            query all() {
                src as var(func: type(Payment)) @filter(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured") and eq(mobile, false)) @cascade {
                    party @filter(eq(partyId, "party1"))
                    shop @filter(eq(shopId, "shop1"))
                }
                        
                var(func: type(Payment)) @filter(uid(src)) {
                    amount as amount
                }
                        
                aggregates() {
                    sum : sum(val(amount))
                }
            }
            """;

    public static final String PAYMENTS_SUM_QUERY_BY_MOBILE_ROOT_WITH_FULL_DATASET = """
            query all() {
                src as var(func: type(Payment)) @filter(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured") and eq(mobile, false) and eq(recurrent, true)) @cascade {
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
                        
                var(func: type(Payment)) @filter(uid(src)) {
                    amount as amount
                }
                        
                aggregates() {
                    sum : sum(val(amount))
                }
            }
            """;

    public static final String PAYMENTS_SUM_QUERY_BY_RECURRENT_ROOT_WITH_MINIMAL_DATASET = """
            query all() {
                src as var(func: type(Payment)) @filter(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured") and eq(recurrent, true)) @cascade {
                }
                        
                var(func: type(Payment)) @filter(uid(src)) {
                    amount as amount
                }
                        
                aggregates() {
                    sum : sum(val(amount))
                }
            }
            """;

    public static final String PAYMENTS_SUM_QUERY_BY_RECURRENT_ROOT_WITH_USUAL_DATASET = """
            query all() {
                src as var(func: type(Payment)) @filter(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured") and eq(recurrent, true)) @cascade {
                    party @filter(eq(partyId, "party1"))
                    shop @filter(eq(shopId, "shop1"))
                }
                        
                var(func: type(Payment)) @filter(uid(src)) {
                    amount as amount
                }
                        
                aggregates() {
                    sum : sum(val(amount))
                }
            }
            """;

    public static final String PAYMENTS_SUM_QUERY_BY_RECURRENT_ROOT_WITH_FULL_DATASET = """
            query all() {
                src as var(func: type(Payment)) @filter(ge(createdAt, "2021-10-28T19:40:54Z") and le(createdAt, "2021-10-28T19:47:54Z") and eq(status, "captured") and eq(mobile, false) and eq(recurrent, true)) @cascade {
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
                        
                var(func: type(Payment)) @filter(uid(src)) {
                    amount as amount
                }
                        
                aggregates() {
                    sum : sum(val(amount))
                }
            }
            """;

}
