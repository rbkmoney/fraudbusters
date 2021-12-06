package com.rbkmoney.fraudbusters.dgraph.insert.data;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class VelocityTestData {

    public static final String TEST_SHORT_PAYMENT_UPSERT_QUERY = """
            query all() {
                getTokenUid(func: type(Token)) @filter(eq(tokenId, "token-1")) {
                    sourceTokenUid as uid
                }
                        
                getCurrencyUid(func: type(Currency)) @filter(eq(currencyCode, "RUB")) {
                    sourceCurrencyUid as uid
                }
                        
                getPartyUid(func: type(Party)) @filter(eq(partyId, "partyId-1")) {
                    sourcePartyUid as uid
                }
                        
                getShopUid(func: type(Shop)) @filter(eq(shopId, "shopId-1")) {
                    sourceShopUid as uid
                }
                        
                getBinUid(func: type(Bin)) @filter(eq(cardBin, "000000")) {
                    sourceBinUid as uid
                }
                        
                getPaymentUid(func: type(Payment)) @filter(eq(paymentId, "TestPayment")) {
                    sourcePaymentUid as uid
                }
            }
            """;

    public static final String TEST_FULL_PAYMENT_UPSERT_QUERY = """
            query all() {
                getTokenUid(func: type(Token)) @filter(eq(tokenId, "token-1")) {
                    sourceTokenUid as uid
                }
                        
                getCurrencyUid(func: type(Currency)) @filter(eq(currencyCode, "RUB")) {
                    sourceCurrencyUid as uid
                }
                        
                getPartyUid(func: type(Party)) @filter(eq(partyId, "partyId-1")) {
                    sourcePartyUid as uid
                }
                        
                getShopUid(func: type(Shop)) @filter(eq(shopId, "shopId-1")) {
                    sourceShopUid as uid
                }
                        
                getBinUid(func: type(Bin)) @filter(eq(cardBin, "000000")) {
                    sourceBinUid as uid
                }
                        
                getEmailUid(func: type(Email)) @filter(eq(userEmail, "1@1.ru")) {
                    sourceEmailUid as uid
                }
                        
                getFingerUid(func: type(Fingerprint)) @filter(eq(fingerprintData, "fData")) {
                    sourceFingerUid as uid
                }
                        
                getCountryUid(func: type(Country)) @filter(eq(countryName, "Russia")) {
                    sourceCountryUid as uid
                }
                        
                getIpUid(func: type(Ip)) @filter(eq(ipAddress, "127.0.0.1")) {
                    sourceIpUid as uid
                }
                        
                getPaymentUid(func: type(Payment)) @filter(eq(paymentId, "TestPayment")) {
                    sourcePaymentUid as uid
                }
            }
            """;

    public static final String TEST_INSERT_PAYMENT_SHORT_BLOCK = """
            uid(sourceTokenUid) <dgraph.type> "Token" .
            uid(sourceTokenUid) <tokenId> "token-1" .
            uid(sourceTokenUid) <bin> uid(sourceBinUid) .
            uid(sourceTokenUid) <maskedPan> "pan-1" .
            uid(sourceTokenUid) <lastActTime> "2021-10-05T18:00:00" .
            uid(sourceTokenUid) <payments> uid(sourcePaymentUid) (createdAt = 2021-10-05T18:00:00, status = "captured") .
                        
            uid(sourcePartyUid) <dgraph.type> "Party" .
            uid(sourcePartyUid) <partyId> "partyId-1" .
            uid(sourcePartyUid) <lastActTime> "2021-10-05T18:00:00" .
            uid(sourcePartyUid) <payments> uid(sourcePaymentUid) (createdAt = 2021-10-05T18:00:00, status = "captured") .
            uid(sourcePartyUid) <tokens> uid(sourceTokenUid) .
                        
            uid(sourceShopUid) <dgraph.type> "Shop" .
            uid(sourceShopUid) <shopId> "shopId-1" .
            uid(sourceShopUid) <lastActTime> "2021-10-05T18:00:00" .
            uid(sourceShopUid) <payments> uid(sourcePaymentUid) (createdAt = 2021-10-05T18:00:00, status = "captured") .
            uid(sourceShopUid) <tokens> uid(sourceTokenUid) .
            uid(sourceShopUid) <party> uid(sourcePartyUid) .
            uid(sourcePartyUid) <shops> uid(sourceShopUid) .
                        
            uid(sourceCurrencyUid) <dgraph.type> "Currency" .
            uid(sourceCurrencyUid) <currencyCode> "RUB" .
            uid(sourceCurrencyUid) <payments> uid(sourcePaymentUid) (createdAt = 2021-10-05T18:00:00, status = "captured") .
                        
            uid(sourceBinUid) <dgraph.type> "Bin" .
            uid(sourceBinUid) <cardBin> "000000" .
            uid(sourceBinUid) <payments> uid(sourcePaymentUid) (createdAt = 2021-10-05T18:00:00, status = "captured") .
            uid(sourceBinUid) <tokens> uid(sourceTokenUid) .
                        
            uid(sourcePaymentUid) <dgraph.type> "Payment" .
            uid(sourcePaymentUid) <paymentId> "TestPayment" .
            uid(sourcePaymentUid) <createdAt> "2021-10-05T18:00:00" .
            uid(sourcePaymentUid) <amount> "1000" .
            uid(sourcePaymentUid) <currency> uid(sourceCurrencyUid) .
            uid(sourcePaymentUid) <status> "captured" .
                        
            uid(sourcePaymentUid) <paymentTool> "tool" .
            uid(sourcePaymentUid) <terminal> "10001" .
            uid(sourcePaymentUid) <providerId> "21" .
            uid(sourcePaymentUid) <payerType> "type-1" .
            uid(sourcePaymentUid) <tokenProvider> "provider-1" .
            uid(sourcePaymentUid) <mobile> "false" .
            uid(sourcePaymentUid) <recurrent> "false" .
            uid(sourcePaymentUid) <cardToken> uid(sourceTokenUid) .
            uid(sourcePaymentUid) <party> uid(sourcePartyUid) .
            uid(sourcePaymentUid) <shop> uid(sourceShopUid) .
            uid(sourcePaymentUid) <bin> uid(sourceBinUid) .
            """;

    public static final String TEST_INSERT_FULL_PAYMENT_BLOCK = """ 
            uid(sourceTokenUid) <dgraph.type> "Token" .
            uid(sourceTokenUid) <tokenId> "token-1" .
            uid(sourceTokenUid) <bin> uid(sourceBinUid) .
            uid(sourceTokenUid) <maskedPan> "pan-1" .
            uid(sourceTokenUid) <lastActTime> "2021-10-05T18:00:00" .
            uid(sourceTokenUid) <payments> uid(sourcePaymentUid) (createdAt = 2021-10-05T18:00:00, status = "captured") .
                        
            uid(sourcePartyUid) <dgraph.type> "Party" .
            uid(sourcePartyUid) <partyId> "partyId-1" .
            uid(sourcePartyUid) <lastActTime> "2021-10-05T18:00:00" .
            uid(sourcePartyUid) <payments> uid(sourcePaymentUid) (createdAt = 2021-10-05T18:00:00, status = "captured") .
            uid(sourcePartyUid) <tokens> uid(sourceTokenUid) .
                        
            uid(sourceShopUid) <dgraph.type> "Shop" .
            uid(sourceShopUid) <shopId> "shopId-1" .
            uid(sourceShopUid) <lastActTime> "2021-10-05T18:00:00" .
            uid(sourceShopUid) <payments> uid(sourcePaymentUid) (createdAt = 2021-10-05T18:00:00, status = "captured") .
            uid(sourceShopUid) <tokens> uid(sourceTokenUid) .
            uid(sourceShopUid) <party> uid(sourcePartyUid) .
            uid(sourcePartyUid) <shops> uid(sourceShopUid) .
                        
            uid(sourceCurrencyUid) <dgraph.type> "Currency" .
            uid(sourceCurrencyUid) <currencyCode> "RUB" .
            uid(sourceCurrencyUid) <payments> uid(sourcePaymentUid) (createdAt = 2021-10-05T18:00:00, status = "captured") .
                        
            uid(sourceBinUid) <dgraph.type> "Bin" .
            uid(sourceBinUid) <cardBin> "000000" .
            uid(sourceBinUid) <payments> uid(sourcePaymentUid) (createdAt = 2021-10-05T18:00:00, status = "captured") .
            uid(sourceBinUid) <tokens> uid(sourceTokenUid) .
                        
            uid(sourceFingerUid) <dgraph.type> "Fingerprint" .
            uid(sourceFingerUid) <fingerprintData> "fData" .
            uid(sourceFingerUid) <lastActTime> "2021-10-05T18:00:00" .
            uid(sourceFingerUid) <payments> uid(sourcePaymentUid) (createdAt = 2021-10-05T18:00:00, status = "captured") .
            uid(sourceFingerUid) <tokens> uid(sourceTokenUid) .
            uid(sourceTokenUid) <fingerprints> uid(sourceFingerUid) .
            uid(sourcePaymentUid) <fingerprint> uid(sourceFingerUid) .
            uid(sourceFingerUid) <emails> uid(sourceEmailUid) .
                        
            uid(sourceEmailUid) <dgraph.type> "Email" .
            uid(sourceEmailUid) <userEmail> "1@1.ru" .
            uid(sourceEmailUid) <lastActTime> "2021-10-05T18:00:00" .
            uid(sourceEmailUid) <payments> uid(sourcePaymentUid) (createdAt = 2021-10-05T18:00:00, status = "captured") .
            uid(sourceEmailUid) <tokens> uid(sourceTokenUid) .
            uid(sourceBinUid) <emails> uid(sourceEmailUid) .
            uid(sourcePartyUid) <emails> uid(sourceEmailUid) .
            uid(sourceShopUid) <emails> uid(sourceEmailUid) .
            uid(sourceTokenUid) <emails> uid(sourceEmailUid) .
            uid(sourcePaymentUid) <contactEmail> uid(sourceEmailUid) .
            uid(sourceEmailUid) <fingerprints> uid(sourceFingerUid) .
                        
            uid(sourceCountryUid) <dgraph.type> "Country" .
            uid(sourceCountryUid) <countryName> "Russia" .
            uid(sourceCountryUid) <payments> uid(sourcePaymentUid) (createdAt = 2021-10-05T18:00:00, status = "captured")  .
            uid(sourceCountryUid) <tokens> uid(sourceTokenUid) .
            uid(sourcePaymentUid) <country> uid(sourceCountryUid) .
            uid(sourceCountryUid) <emails> uid(sourceEmailUid) .
            uid(sourceCountryUid) <ips> uid(sourceIpUid) .
                        
            uid(sourceIpUid) <dgraph.type> "Ip" .
            uid(sourceIpUid) <ipAddress> "127.0.0.1" .
            uid(sourceIpUid) <payments> uid(sourcePaymentUid) (createdAt = 2021-10-05T18:00:00, status = "captured") .
            uid(sourceIpUid) <tokens> uid(sourceTokenUid) .
            uid(sourcePaymentUid) <operationIp> uid(sourceIpUid) .
            uid(sourceIpUid) <emails> uid(sourceEmailUid) .
            uid(sourceIpUid) <countries> uid(sourceCountryUid) .
                        
            uid(sourcePaymentUid) <dgraph.type> "Payment" .
            uid(sourcePaymentUid) <paymentId> "TestPayment" .
            uid(sourcePaymentUid) <createdAt> "2021-10-05T18:00:00" .
            uid(sourcePaymentUid) <amount> "1000" .
            uid(sourcePaymentUid) <currency> uid(sourceCurrencyUid) .
            uid(sourcePaymentUid) <status> "captured" .
                        
            uid(sourcePaymentUid) <paymentTool> "tool" .
            uid(sourcePaymentUid) <terminal> "10001" .
            uid(sourcePaymentUid) <providerId> "21" .
            uid(sourcePaymentUid) <payerType> "type-1" .
            uid(sourcePaymentUid) <tokenProvider> "provider-1" .
            uid(sourcePaymentUid) <mobile> "false" .
            uid(sourcePaymentUid) <recurrent> "false" .
            uid(sourcePaymentUid) <cardToken> uid(sourceTokenUid) .
            uid(sourcePaymentUid) <party> uid(sourcePartyUid) .
            uid(sourcePaymentUid) <shop> uid(sourceShopUid) .
            uid(sourcePaymentUid) <bin> uid(sourceBinUid) .
            """;

    public static final String TEST_FRAUD_PAYMENT_UPSERT_QUERY = """
            query all() {
                getFraudPaymentUid(func: type(FraudPayment)) @filter(eq(paymentId, "pay-1")) {
                    sourceFraudPaymentUid as uid
                }
                        
                getPaymentUid(func: type(Payment)) @filter(eq(paymentId, "pay-1")) {
                    sourcePaymentUid as uid
                }
            }
            """;

    public static final String TEST_INSERT_FRAUD_PAYMENT_BLOCK = """
            uid(sourceFraudPaymentUid) <dgraph.type> "FraudPayment" .
            uid(sourceFraudPaymentUid) <paymentId> "pay-1" .
            uid(sourceFraudPaymentUid) <createdAt> "2021-10-05T18:00:00" .
            uid(sourceFraudPaymentUid) <fraudType> "simple fraud" .
            uid(sourceFraudPaymentUid) <comment> "some comment" .
            uid(sourceFraudPaymentUid) <sourcePayment> uid(sourcePaymentUid) .
                        
            uid(sourcePaymentUid) <dgraph.type> "Payment" .
            uid(sourcePaymentUid) <paymentId> "pay-1" .
            uid(sourcePaymentUid) <fraudPayment> uid(sourceFraudPaymentUid) .
            """;

    public static final String TEST_SMALL_REFUND_UPSERT_QUERY = """
            query all() {
                getTokenUid(func: type(Token)) @filter(eq(tokenId, "token")) {
                    sourceTokenUid as uid
                }
                        
                getCurrencyUid(func: type(Currency)) @filter(eq(currencyCode, "RUB")) {
                    sourceCurrencyUid as uid
                }
                        
                getPartyUid(func: type(Party)) @filter(eq(partyId, "Party")) {
                    sourcePartyUid as uid
                }
                        
                getShopUid(func: type(Shop)) @filter(eq(shopId, "Shop")) {
                    sourceShopUid as uid
                }
                        
                getBinUid(func: type(Bin)) @filter(eq(cardBin, "000000")) {
                    sourceBinUid as uid
                }
                        
                getPaymentUid(func: type(Payment)) @filter(eq(paymentId, "TestPayId")) {
                    sourcePaymentUid as uid
                }
                        
                getRefundUid(func: type(Refund)) @filter(eq(paymentId, "TestPayId") and eq(refundId, "TestRefId")) {
                    sourceRefundUid as uid
                }
            }
            """;

    public static final String TEST_FULL_REFUND_UPSERT_QUERY = """
            query all() {
                getTokenUid(func: type(Token)) @filter(eq(tokenId, "token")) {
                    sourceTokenUid as uid
                }
                        
                getCurrencyUid(func: type(Currency)) @filter(eq(currencyCode, "RUB")) {
                    sourceCurrencyUid as uid
                }
                        
                getPartyUid(func: type(Party)) @filter(eq(partyId, "Party")) {
                    sourcePartyUid as uid
                }
                        
                getShopUid(func: type(Shop)) @filter(eq(shopId, "Shop")) {
                    sourceShopUid as uid
                }
                        
                getBinUid(func: type(Bin)) @filter(eq(cardBin, "000000")) {
                    sourceBinUid as uid
                }
                        
                getEmailUid(func: type(Email)) @filter(eq(userEmail, "1@1.ru")) {
                    sourceEmailUid as uid
                }
                        
                getFingerUid(func: type(Fingerprint)) @filter(eq(fingerprintData, "fData")) {
                    sourceFingerUid as uid
                }
                        
                getIpUid(func: type(Ip)) @filter(eq(ipAddress, "127.0.0.1")) {
                    sourceIpUid as uid
                }
                        
                getPaymentUid(func: type(Payment)) @filter(eq(paymentId, "TestPayId")) {
                    sourcePaymentUid as uid
                }
                        
                getRefundUid(func: type(Refund)) @filter(eq(paymentId, "TestPayId") and eq(refundId, "TestRefId")) {
                    sourceRefundUid as uid
                }
            }
            """;

    public static final String TEST_INSERT_SMALL_REFUND_BLOCK = """
            uid(sourceTokenUid) <dgraph.type> "Token" .
            uid(sourceTokenUid) <tokenId> "token" .
            uid(sourceTokenUid) <bin> uid(sourceBinUid) .
            uid(sourceTokenUid) <maskedPan> "maskedPan" .
            uid(sourceTokenUid) <lastActTime> "2021-10-05T18:00:00" .
            uid(sourceTokenUid) <refunds> uid(sourceRefundUid) (createdAt = 2021-10-05T18:00:00, status = "successful") .
                        
            uid(sourcePartyUid) <dgraph.type> "Party" .
            uid(sourcePartyUid) <partyId> "Party" .
            uid(sourcePartyUid) <lastActTime> "2021-10-05T18:00:00" .
            uid(sourcePartyUid) <refunds> uid(sourceRefundUid) (createdAt = 2021-10-05T18:00:00, status = "successful") .
            uid(sourcePartyUid) <tokens> uid(sourceTokenUid) .
                        
            uid(sourceShopUid) <dgraph.type> "Shop" .
            uid(sourceShopUid) <shopId> "Shop" .
            uid(sourceShopUid) <lastActTime> "2021-10-05T18:00:00" .
            uid(sourceShopUid) <refunds> uid(sourceRefundUid) (createdAt = 2021-10-05T18:00:00, status = "successful") .
            uid(sourceShopUid) <tokens> uid(sourceTokenUid) .
            uid(sourceShopUid) <party> uid(sourcePartyUid) .
            uid(sourcePartyUid) <shops> uid(sourceShopUid) .
                        
            uid(sourceCurrencyUid) <dgraph.type> "Currency" .
            uid(sourceCurrencyUid) <currencyCode> "RUB" .
            uid(sourceCurrencyUid) <refunds> uid(sourceRefundUid) (createdAt = 2021-10-05T18:00:00, status = "successful") .
                        
            uid(sourceBinUid) <dgraph.type> "Bin" .
            uid(sourceBinUid) <cardBin> "000000" .
            uid(sourceBinUid) <refunds> uid(sourceRefundUid) (createdAt = 2021-10-05T18:00:00, status = "successful") .
            uid(sourceBinUid) <tokens> uid(sourceTokenUid) .
                        
            uid(sourceRefundUid) <dgraph.type> "Refund" .
            uid(sourceRefundUid) <paymentId> "TestPayId" .
            uid(sourceRefundUid) <refundId> "TestRefId" .
            uid(sourceRefundUid) <createdAt> "2021-10-05T18:00:00" .
            uid(sourceRefundUid) <amount> "1000" .
            uid(sourceRefundUid) <currency> uid(sourceCurrencyUid) .
            uid(sourceRefundUid) <status> "successful" .
            uid(sourceRefundUid) <payerType> "paid" .
                        
            uid(sourceRefundUid) <cardToken> uid(sourceTokenUid) .
            uid(sourceRefundUid) <party> uid(sourcePartyUid) .
            uid(sourceRefundUid) <shop> uid(sourceShopUid) .
            uid(sourceRefundUid) <bin> uid(sourceBinUid) .
                        
            uid(sourcePaymentUid) <dgraph.type> "Payment" .
            uid(sourcePaymentUid) <paymentId> "TestPayId" .
            uid(sourcePaymentUid) <refunds> uid(sourceRefundUid) (createdAt = 2021-10-05T18:00:00, status = "successful") .
            """;

    public static final String TEST_INSERT_FULL_REFUND_BLOCK = """
            uid(sourceTokenUid) <dgraph.type> "Token" .
            uid(sourceTokenUid) <tokenId> "token" .
            uid(sourceTokenUid) <bin> uid(sourceBinUid) .
            uid(sourceTokenUid) <maskedPan> "maskedPan" .
            uid(sourceTokenUid) <lastActTime> "2021-10-05T18:00:00" .
            uid(sourceTokenUid) <refunds> uid(sourceRefundUid) (createdAt = 2021-10-05T18:00:00, status = "successful") .
                        
            uid(sourcePartyUid) <dgraph.type> "Party" .
            uid(sourcePartyUid) <partyId> "Party" .
            uid(sourcePartyUid) <lastActTime> "2021-10-05T18:00:00" .
            uid(sourcePartyUid) <refunds> uid(sourceRefundUid) (createdAt = 2021-10-05T18:00:00, status = "successful") .
            uid(sourcePartyUid) <tokens> uid(sourceTokenUid) .
                        
            uid(sourceShopUid) <dgraph.type> "Shop" .
            uid(sourceShopUid) <shopId> "Shop" .
            uid(sourceShopUid) <lastActTime> "2021-10-05T18:00:00" .
            uid(sourceShopUid) <refunds> uid(sourceRefundUid) (createdAt = 2021-10-05T18:00:00, status = "successful") .
            uid(sourceShopUid) <tokens> uid(sourceTokenUid) .
            uid(sourceShopUid) <party> uid(sourcePartyUid) .
            uid(sourcePartyUid) <shops> uid(sourceShopUid) .
                        
            uid(sourceCurrencyUid) <dgraph.type> "Currency" .
            uid(sourceCurrencyUid) <currencyCode> "RUB" .
            uid(sourceCurrencyUid) <refunds> uid(sourceRefundUid) (createdAt = 2021-10-05T18:00:00, status = "successful") .
                        
            uid(sourceBinUid) <dgraph.type> "Bin" .
            uid(sourceBinUid) <cardBin> "000000" .
            uid(sourceBinUid) <refunds> uid(sourceRefundUid) (createdAt = 2021-10-05T18:00:00, status = "successful") .
            uid(sourceBinUid) <tokens> uid(sourceTokenUid) .
                        
            uid(sourceFingerUid) <dgraph.type> "Fingerprint" .
            uid(sourceFingerUid) <fingerprintData> "fData" .
            uid(sourceFingerUid) <lastActTime> "2021-10-05T18:00:00" .
            uid(sourceFingerUid) <refunds> uid(sourceRefundUid) (createdAt = 2021-10-05T18:00:00, status = "successful") .
            uid(sourceFingerUid) <tokens> uid(sourceTokenUid) .
            uid(sourceTokenUid) <fingerprints> uid(sourceFingerUid) .
            uid(sourceRefundUid) <fingerprint> uid(sourceFingerUid) .
            uid(sourceFingerUid) <emails> uid(sourceEmailUid) .
                        
            uid(sourceEmailUid) <dgraph.type> "Email" .
            uid(sourceEmailUid) <userEmail> "1@1.ru" .
            uid(sourceEmailUid) <lastActTime> "2021-10-05T18:00:00" .
            uid(sourceEmailUid) <refunds> uid(sourceRefundUid) (createdAt = 2021-10-05T18:00:00, status = "successful") .
            uid(sourceEmailUid) <tokens> uid(sourceTokenUid) .
            uid(sourceBinUid) <emails> uid(sourceEmailUid) .
            uid(sourcePartyUid) <emails> uid(sourceEmailUid) .
            uid(sourceShopUid) <emails> uid(sourceEmailUid) .
            uid(sourceTokenUid) <emails> uid(sourceEmailUid) .
            uid(sourceRefundUid) <contactEmail> uid(sourceEmailUid) .
            uid(sourceEmailUid) <fingerprints> uid(sourceFingerUid) .
                        
            uid(sourceIpUid) <dgraph.type> "Ip" .
            uid(sourceIpUid) <ipAddress> "127.0.0.1" .
            uid(sourceIpUid) <refunds> uid(sourceRefundUid) (createdAt = 2021-10-05T18:00:00, status = "successful") .
            uid(sourceIpUid) <tokens> uid(sourceTokenUid) .
            uid(sourceRefundUid) <operationIp> uid(sourceIpUid) .
            uid(sourceIpUid) <emails> uid(sourceEmailUid) .
                        
            uid(sourceRefundUid) <dgraph.type> "Refund" .
            uid(sourceRefundUid) <paymentId> "TestPayId" .
            uid(sourceRefundUid) <refundId> "TestRefId" .
            uid(sourceRefundUid) <createdAt> "2021-10-05T18:00:00" .
            uid(sourceRefundUid) <amount> "1000" .
            uid(sourceRefundUid) <currency> uid(sourceCurrencyUid) .
            uid(sourceRefundUid) <status> "successful" .
            uid(sourceRefundUid) <payerType> "paid" .
                        
            uid(sourceRefundUid) <cardToken> uid(sourceTokenUid) .
            uid(sourceRefundUid) <party> uid(sourcePartyUid) .
            uid(sourceRefundUid) <shop> uid(sourceShopUid) .
            uid(sourceRefundUid) <bin> uid(sourceBinUid) .
                        
            uid(sourcePaymentUid) <dgraph.type> "Payment" .
            uid(sourcePaymentUid) <paymentId> "TestPayId" .
            uid(sourcePaymentUid) <refunds> uid(sourceRefundUid) (createdAt = 2021-10-05T18:00:00, status = "successful") .
            """;

    public static final String TEST_SMALL_CHARGEBACK_UPSERT_QUERY = """
            query all() {
                getTokenUid(func: type(Token)) @filter(eq(tokenId, "token")) {
                    sourceTokenUid as uid
                }
                        
                getCurrencyUid(func: type(Currency)) @filter(eq(currencyCode, "RUB")) {
                    sourceCurrencyUid as uid
                }
                        
                getPartyUid(func: type(Party)) @filter(eq(partyId, "Party")) {
                    sourcePartyUid as uid
                }
                        
                getShopUid(func: type(Shop)) @filter(eq(shopId, "Shop")) {
                    sourceShopUid as uid
                }
                        
                getBinUid(func: type(Bin)) @filter(eq(cardBin, "000000")) {
                    sourceBinUid as uid
                }
                        
                getPaymentUid(func: type(Payment)) @filter(eq(paymentId, "TestPayId")) {
                    sourcePaymentUid as uid
                }
                        
                getChargebackUid(func: type(Chargeback)) @filter(eq(paymentId, "TestPayId") and eq(chargebackId, "TestChargebackIdId")) {
                    sourceChargebackUid as uid
                }
            }
            """;

    public static final String TEST_FULL_CHARGEBACK_UPSERT_QUERY = """
            query all() {
                getTokenUid(func: type(Token)) @filter(eq(tokenId, "token")) {
                    sourceTokenUid as uid
                }
                        
                getCurrencyUid(func: type(Currency)) @filter(eq(currencyCode, "RUB")) {
                    sourceCurrencyUid as uid
                }
                        
                getPartyUid(func: type(Party)) @filter(eq(partyId, "Party")) {
                    sourcePartyUid as uid
                }
                        
                getShopUid(func: type(Shop)) @filter(eq(shopId, "Shop")) {
                    sourceShopUid as uid
                }
                        
                getBinUid(func: type(Bin)) @filter(eq(cardBin, "000000")) {
                    sourceBinUid as uid
                }
                        
                getEmailUid(func: type(Email)) @filter(eq(userEmail, "1@1.ru")) {
                    sourceEmailUid as uid
                }
                        
                getFingerUid(func: type(Fingerprint)) @filter(eq(fingerprintData, "fData")) {
                    sourceFingerUid as uid
                }
                        
                getIpUid(func: type(Ip)) @filter(eq(ipAddress, "127.0.0.1")) {
                    sourceIpUid as uid
                }
                        
                getPaymentUid(func: type(Payment)) @filter(eq(paymentId, "TestPayId")) {
                    sourcePaymentUid as uid
                }
                        
                getChargebackUid(func: type(Chargeback)) @filter(eq(paymentId, "TestPayId") and eq(chargebackId, "TestChargebackIdId")) {
                    sourceChargebackUid as uid
                }
            }
            """;

    public static final String TEST_INSERT_SMALL_CHARGEBACK_BLOCK = """
            uid(sourceTokenUid) <dgraph.type> "Token" .
            uid(sourceTokenUid) <tokenId> "token" .
            uid(sourceTokenUid) <bin> uid(sourceBinUid) .
            uid(sourceTokenUid) <maskedPan> "maskedPan" .
            uid(sourceTokenUid) <lastActTime> "2021-10-05T18:00:00" .
            uid(sourceTokenUid) <chargebacks> uid(sourceChargebackUid) (createdAt = 2021-10-05T18:00:00, status = "succeeded") .
                        
            uid(sourcePartyUid) <dgraph.type> "Party" .
            uid(sourcePartyUid) <partyId> "Party" .
            uid(sourcePartyUid) <lastActTime> "2021-10-05T18:00:00" .
            uid(sourcePartyUid) <chargebacks> uid(sourceChargebackUid) (createdAt = 2021-10-05T18:00:00, status = "succeeded") .
            uid(sourcePartyUid) <tokens> uid(sourceTokenUid) .
                        
            uid(sourceShopUid) <dgraph.type> "Shop" .
            uid(sourceShopUid) <lastActTime> "2021-10-05T18:00:00" .
            uid(sourceShopUid) <shopId> "Shop" .
            uid(sourceShopUid) <chargebacks> uid(sourceChargebackUid) (createdAt = 2021-10-05T18:00:00, status = "succeeded") .
            uid(sourceShopUid) <tokens> uid(sourceTokenUid) .
            uid(sourceShopUid) <party> uid(sourcePartyUid) .
            uid(sourcePartyUid) <shops> uid(sourceShopUid) .
                        
            uid(sourceCurrencyUid) <dgraph.type> "Currency" .
            uid(sourceCurrencyUid) <currencyCode> "RUB" .
            uid(sourceCurrencyUid) <chargebacks> uid(sourceChargebackUid) (createdAt = 2021-10-05T18:00:00, status = "succeeded") .
                        
            uid(sourceBinUid) <dgraph.type> "Bin" .
            uid(sourceBinUid) <cardBin> "000000" .
            uid(sourceBinUid) <chargebacks> uid(sourceChargebackUid) (createdAt = 2021-10-05T18:00:00, status = "succeeded") .
            uid(sourceBinUid) <tokens> uid(sourceTokenUid) .
                        
            uid(sourceChargebackUid) <dgraph.type> "Chargeback" .
            uid(sourceChargebackUid) <paymentId> "TestPayId" .
            uid(sourceChargebackUid) <chargebackId> "TestChargebackIdId" .
            uid(sourceChargebackUid) <createdAt> "2021-10-05T18:00:00" .
            uid(sourceChargebackUid) <amount> "1000" .
            uid(sourceChargebackUid) <currency> uid(sourceCurrencyUid) .
            uid(sourceChargebackUid) <status> "succeeded" .
            uid(sourceChargebackUid) <category> "category" .
            uid(sourceChargebackUid) <code> "code404" .
            uid(sourceChargebackUid) <payerType> "paid" .
                        
            uid(sourceChargebackUid) <cardToken> uid(sourceTokenUid) .
            uid(sourceChargebackUid) <party> uid(sourcePartyUid) .
            uid(sourceChargebackUid) <shop> uid(sourceShopUid) .
            uid(sourceChargebackUid) <bin> uid(sourceBinUid) .
                        
            uid(sourcePaymentUid) <dgraph.type> "Payment" .
            uid(sourcePaymentUid) <paymentId> "TestPayId" .
            uid(sourcePaymentUid) <chargebacks> uid(sourceChargebackUid) (createdAt = 2021-10-05T18:00:00, status = "succeeded") .
            """;

    public static final String TEST_INSERT_FULL_CHARGEBACK_BLOCK = """
            uid(sourceTokenUid) <dgraph.type> "Token" .
            uid(sourceTokenUid) <tokenId> "token" .
            uid(sourceTokenUid) <bin> uid(sourceBinUid) .
            uid(sourceTokenUid) <maskedPan> "maskedPan" .
            uid(sourceTokenUid) <lastActTime> "2021-10-05T18:00:00" .
            uid(sourceTokenUid) <chargebacks> uid(sourceChargebackUid) (createdAt = 2021-10-05T18:00:00, status = "succeeded") .
                        
            uid(sourcePartyUid) <dgraph.type> "Party" .
            uid(sourcePartyUid) <partyId> "Party" .
            uid(sourcePartyUid) <lastActTime> "2021-10-05T18:00:00" .
            uid(sourcePartyUid) <chargebacks> uid(sourceChargebackUid) (createdAt = 2021-10-05T18:00:00, status = "succeeded") .
            uid(sourcePartyUid) <tokens> uid(sourceTokenUid) .
                        
            uid(sourceShopUid) <dgraph.type> "Shop" .
            uid(sourceShopUid) <lastActTime> "2021-10-05T18:00:00" .
            uid(sourceShopUid) <shopId> "Shop" .
            uid(sourceShopUid) <chargebacks> uid(sourceChargebackUid) (createdAt = 2021-10-05T18:00:00, status = "succeeded") .
            uid(sourceShopUid) <tokens> uid(sourceTokenUid) .
            uid(sourceShopUid) <party> uid(sourcePartyUid) .
            uid(sourcePartyUid) <shops> uid(sourceShopUid) .
                        
            uid(sourceCurrencyUid) <dgraph.type> "Currency" .
            uid(sourceCurrencyUid) <currencyCode> "RUB" .
            uid(sourceCurrencyUid) <chargebacks> uid(sourceChargebackUid) (createdAt = 2021-10-05T18:00:00, status = "succeeded") .
                        
            uid(sourceBinUid) <dgraph.type> "Bin" .
            uid(sourceBinUid) <cardBin> "000000" .
            uid(sourceBinUid) <chargebacks> uid(sourceChargebackUid) (createdAt = 2021-10-05T18:00:00, status = "succeeded") .
            uid(sourceBinUid) <tokens> uid(sourceTokenUid) .
                        
            uid(sourceFingerUid) <dgraph.type> "Fingerprint" .
            uid(sourceFingerUid) <fingerprintData> "fData" .
            uid(sourceFingerUid) <lastActTime> "2021-10-05T18:00:00" .
            uid(sourceFingerUid) <chargebacks> uid(sourceChargebackUid) (createdAt = 2021-10-05T18:00:00, status = "succeeded") .
            uid(sourceFingerUid) <tokens> uid(sourceTokenUid) .
            uid(sourceTokenUid) <fingerprints> uid(sourceFingerUid) .
            uid(sourceChargebackUid) <fingerprint> uid(sourceFingerUid) .
            uid(sourceFingerUid) <emails> uid(sourceEmailUid) .
                        
            uid(sourceEmailUid) <dgraph.type> "Email" .
            uid(sourceEmailUid) <userEmail> "1@1.ru" .
            uid(sourceEmailUid) <lastActTime> "2021-10-05T18:00:00" .
            uid(sourceEmailUid) <chargebacks> uid(sourceChargebackUid) (createdAt = 2021-10-05T18:00:00, status = "succeeded") .
            uid(sourceEmailUid) <tokens> uid(sourceTokenUid) .
            uid(sourceBinUid) <emails> uid(sourceEmailUid) .
            uid(sourcePartyUid) <emails> uid(sourceEmailUid) .
            uid(sourceShopUid) <emails> uid(sourceEmailUid) .
            uid(sourceTokenUid) <emails> uid(sourceEmailUid) .
            uid(sourceChargebackUid) <contactEmail> uid(sourceEmailUid) .
            uid(sourceEmailUid) <fingerprints> uid(sourceFingerUid) .
                        
            uid(sourceIpUid) <dgraph.type> "Ip" .
            uid(sourceIpUid) <ipAddress> "127.0.0.1" .
            uid(sourceIpUid) <chargebacks> uid(sourceChargebackUid) (createdAt = 2021-10-05T18:00:00, status = "succeeded") .
            uid(sourceIpUid) <tokens> uid(sourceTokenUid) .
            uid(sourceChargebackUid) <operationIp> uid(sourceIpUid) .
            uid(sourceIpUid) <emails> uid(sourceEmailUid) .
                        
            uid(sourceChargebackUid) <dgraph.type> "Chargeback" .
            uid(sourceChargebackUid) <paymentId> "TestPayId" .
            uid(sourceChargebackUid) <chargebackId> "TestChargebackIdId" .
            uid(sourceChargebackUid) <createdAt> "2021-10-05T18:00:00" .
            uid(sourceChargebackUid) <amount> "1000" .
            uid(sourceChargebackUid) <currency> uid(sourceCurrencyUid) .
            uid(sourceChargebackUid) <status> "succeeded" .
            uid(sourceChargebackUid) <category> "category" .
            uid(sourceChargebackUid) <code> "code404" .
            uid(sourceChargebackUid) <payerType> "paid" .
                        
            uid(sourceChargebackUid) <cardToken> uid(sourceTokenUid) .
            uid(sourceChargebackUid) <party> uid(sourcePartyUid) .
            uid(sourceChargebackUid) <shop> uid(sourceShopUid) .
            uid(sourceChargebackUid) <bin> uid(sourceBinUid) .
                        
            uid(sourcePaymentUid) <dgraph.type> "Payment" .
            uid(sourcePaymentUid) <paymentId> "TestPayId" .
            uid(sourcePaymentUid) <chargebacks> uid(sourceChargebackUid) (createdAt = 2021-10-05T18:00:00, status = "succeeded") .
            """;

    public static final String TEST_SMALL_WITHDRAWAL_UPSERT_QUERY = """
            query all() {
                        
                getCurrencyUid(func: type(Currency)) @filter(eq(currencyCode, "RUB")) {
                    sourceCurrencyUid as uid
                }
                        
                getCryptoCurrencyUid(func: type(Currency)) @filter(eq(currencyCode, "ETH")) {
                    sourceCryptoCurrencyUid as uid
                }
                        
                getCountryUid(func: type(Country)) @filter(eq(countryName, "Russia")) {
                    sourceCountryUid as uid
                }
                        
                getWithdrawalUid(func: type(Withdrawal)) @filter(eq(withdrawalId, "Wid-1")) {
                    sourceWithdrawalUid as uid
                }
            }
            """;

    public static final String TEST_FULL_WITHDRAWAL_UPSERT_QUERY = """
            query all() {
                        
                getCurrencyUid(func: type(Currency)) @filter(eq(currencyCode, "RUB")) {
                    sourceCurrencyUid as uid
                }
                        
                getAccountCurrencyUid(func: type(Currency)) @filter(eq(currencyCode, "BSD")) {
                    sourceAccountCurrencyUid as uid
                }
                        
                getBinUid(func: type(Bin)) @filter(eq(cardBin, "000000")) {
                    sourceBinUid as uid
                }
                        
                getTokenUid(func: type(Token)) @filter(eq(tokenId, "tokenID")) {
                    sourceTokenUid as uid
                }
                        
                getCountryUid(func: type(Country)) @filter(eq(countryName, "Russia")) {
                    sourceCountryUid as uid
                }
                        
                getWithdrawalUid(func: type(Withdrawal)) @filter(eq(withdrawalId, "Wid-1")) {
                    sourceWithdrawalUid as uid
                }
            }
            """;

    public static final String TEST_INSERT_SMALL_WITHDRAWAL_BLOCK = """
            uid(sourceWithdrawalUid) <dgraph.type> "Withdrawal" .
            uid(sourceWithdrawalUid) <withdrawalId> "Wid-1" .
            uid(sourceWithdrawalUid) <createdAt> "2021-10-05T18:00:00" .
            uid(sourceWithdrawalUid) <amount> "1000" .
            uid(sourceWithdrawalUid) <status> "status-1" .
            uid(sourceWithdrawalUid) <providerId> "123" .
            uid(sourceWithdrawalUid) <terminalId> "345" .
            uid(sourceWithdrawalUid) <destinationResource> "crypto_wallet" .
            uid(sourceWithdrawalUid) <cryptoWalletId> "CID-1" .
                        
            uid(sourceCurrencyUid) <dgraph.type> "Currency" .
            uid(sourceCurrencyUid) <currencyCode> "RUB" .
            uid(sourceCurrencyUid) <withdrawals> uid(sourceWithdrawalUid) .
                        
            uid(sourceCryptoCurrencyUid) <dgraph.type> "Currency" .
            uid(sourceCryptoCurrencyUid) <currencyCode> "ETH" .
            uid(sourceCryptoCurrencyUid) <withdrawals> uid(sourceWithdrawalUid) .
            uid(sourceWithdrawalUid) <cryptoWalletCurrency> uid(sourceCryptoCurrencyUid) .
                        
            uid(sourceCountryUid) <dgraph.type> "Country" .
            uid(sourceCountryUid) <countryName> "Russia" .
            uid(sourceWithdrawalUid) <country> uid(sourceCountryUid) .
                        
            """;

    public static final String TEST_INSERT_FULL_WITHDRAWAL_BLOCK = """
            uid(sourceWithdrawalUid) <dgraph.type> "Withdrawal" .
            uid(sourceWithdrawalUid) <withdrawalId> "Wid-1" .
            uid(sourceWithdrawalUid) <createdAt> "2021-10-05T18:00:00" .
            uid(sourceWithdrawalUid) <amount> "1000" .
            uid(sourceWithdrawalUid) <status> "status-1" .
            uid(sourceWithdrawalUid) <providerId> "123" .
            uid(sourceWithdrawalUid) <terminalId> "345" .
            uid(sourceWithdrawalUid) <accountId> "AccId" .
            uid(sourceWithdrawalUid) <accountIdentity> "Iddy" .
            uid(sourceWithdrawalUid) <errorCode> "code" .
            uid(sourceWithdrawalUid) <errorReason> "reason" .
            uid(sourceWithdrawalUid) <destinationResource> "bank_card" .
                        
            uid(sourceCurrencyUid) <dgraph.type> "Currency" .
            uid(sourceCurrencyUid) <currencyCode> "RUB" .
            uid(sourceCurrencyUid) <withdrawals> uid(sourceWithdrawalUid) .
                        
            uid(sourceAccountCurrencyUid) <dgraph.type> "Currency" .
            uid(sourceAccountCurrencyUid) <currencyCode> "BSD" .
            uid(sourceAccountCurrencyUid) <withdrawals> uid(sourceWithdrawalUid) .
            uid(sourceWithdrawalUid) <accountCurrency> uid(sourceAccountCurrencyUid) .
                        
            uid(sourceTokenUid) <dgraph.type> "Token" .
            uid(sourceTokenUid) <tokenId> "tokenID" .
            uid(sourceTokenUid) <bin> uid(sourceBinUid) .
            uid(sourceTokenUid) <maskedPan> "MaskedPAN" .
            uid(sourceTokenUid) <lastActTime> "2021-10-05T18:00:00" .
            uid(sourceWithdrawalUid) <cardToken> uid(sourceTokenUid) .
                        
            uid(sourceBinUid) <dgraph.type> "Bin" .
            uid(sourceBinUid) <cardBin> "000000" .
            uid(sourceBinUid) <tokens> uid(sourceTokenUid) .
            uid(sourceWithdrawalUid) <bin> uid(sourceBinUid) .
                        
            uid(sourceCountryUid) <dgraph.type> "Country" .
            uid(sourceCountryUid) <countryName> "Russia" .
            uid(sourceWithdrawalUid) <country> uid(sourceCountryUid) .
                        
            """;
}
