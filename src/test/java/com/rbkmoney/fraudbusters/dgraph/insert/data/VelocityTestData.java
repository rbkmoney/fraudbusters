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
                        
                getShopUid(func: type(PartyShop)) @filter(eq(partyId, "partyId-1") and eq(shopId, "shopId-1")) {
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

                getShopUid(func: type(PartyShop)) @filter(eq(partyId, "partyId-1") and eq(shopId, "shopId-1")) {
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

                getIpUid(func: type(IP)) @filter(eq(ipAddress, "127.0.0.1")) {
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
                        
            uid(sourceShopUid) <dgraph.type> "PartyShop" .
            uid(sourceShopUid) <partyId> "partyId-1" .
            uid(sourceShopUid) <shopId> "shopId-1" .
            uid(sourceShopUid) <payments> uid(sourcePaymentUid) .
            uid(sourceShopUid) <tokens> uid(sourceTokenUid) .
                        
            uid(sourceBinUid) <dgraph.type> "Bin" .
            uid(sourceBinUid) <cardBin> "000000" .
            uid(sourceBinUid) <payments> uid(sourcePaymentUid) .
            uid(sourceBinUid) <tokens> uid(sourceTokenUid) .
                        
            uid(sourcePaymentUid) <dgraph.type> "Payment" .
            uid(sourcePaymentUid) <paymentId> "TestPayment" .
            uid(sourcePaymentUid) <partyId> "partyId-1" .
            uid(sourcePaymentUid) <shopId> "shopId-1" .
            uid(sourcePaymentUid) <createdAt> "2021-10-05T18:00:00" .
            uid(sourcePaymentUid) <amount> "1000" .
            uid(sourcePaymentUid) <currency> "RUB" .
            uid(sourcePaymentUid) <status> "captured" .
                        
            uid(sourcePaymentUid) <paymentTool> "tool" .
            uid(sourcePaymentUid) <terminal> "10001" .
            uid(sourcePaymentUid) <providerId> "21" .
            uid(sourcePaymentUid) <bankCountry> "Russia" .
            uid(sourcePaymentUid) <payerType> "type-1" .
            uid(sourcePaymentUid) <tokenProvider> "provider-1" .
            uid(sourcePaymentUid) <mobile> "false" .
            uid(sourcePaymentUid) <recurrent> "false" .
            uid(sourcePaymentUid) <cardToken> uid(sourceTokenUid) .
            uid(sourcePaymentUid) <partyShop> uid(sourceShopUid) .
            uid(sourcePaymentUid) <bin> uid(sourceBinUid) .
            """;

    public static final String TEST_INSERT_FULL_PAYMENT_BLOCK = """ 
            uid(sourceTokenUid) <dgraph.type> "Token" .
            uid(sourceTokenUid) <tokenId> "token-1" .
            uid(sourceTokenUid) <bin> uid(sourceBinUid) .
            uid(sourceTokenUid) <maskedPan> "pan-1" .
            uid(sourceTokenUid) <lastActTime> "2021-10-05T18:00:00" .
            uid(sourceTokenUid) <payments> uid(sourcePaymentUid) (createdAt = 2021-10-05T18:00:00, status = "captured") .
                        
            uid(sourceShopUid) <dgraph.type> "PartyShop" .
            uid(sourceShopUid) <partyId> "partyId-1" .
            uid(sourceShopUid) <shopId> "shopId-1" .
            uid(sourceShopUid) <payments> uid(sourcePaymentUid) .
            uid(sourceShopUid) <tokens> uid(sourceTokenUid) .
                        
            uid(sourceBinUid) <dgraph.type> "Bin" .
            uid(sourceBinUid) <cardBin> "000000" .
            uid(sourceBinUid) <payments> uid(sourcePaymentUid) .
            uid(sourceBinUid) <tokens> uid(sourceTokenUid) .
                        
            uid(sourceFingerUid) <dgraph.type> "Fingerprint" .
            uid(sourceFingerUid) <fingerprintData> "fData" .
            uid(sourceFingerUid) <lastActTime> "2021-10-05T18:00:00" .
            uid(sourceFingerUid) <payments> uid(sourcePaymentUid) .
            uid(sourceFingerUid) <tokens> uid(sourceTokenUid) .
            uid(sourceTokenUid) <fingerprints> uid(sourceFingerUid) .
            uid(sourcePaymentUid) <fingerprint> uid(sourceFingerUid) .
            uid(sourceFingerUid) <emails> uid(sourceEmailUid) .
                        
            uid(sourceEmailUid) <dgraph.type> "Email" .
            uid(sourceEmailUid) <userEmail> "1@1.ru" .
            uid(sourceEmailUid) <lastActTime> "2021-10-05T18:00:00" .
            uid(sourceEmailUid) <payments> uid(sourcePaymentUid) .
            uid(sourceEmailUid) <tokens> uid(sourceTokenUid) .
            uid(sourceBinUid) <emails> uid(sourceEmailUid) .
            uid(sourceShopUid) <emails> uid(sourceEmailUid) .
            uid(sourceTokenUid) <emails> uid(sourceEmailUid) .
            uid(sourcePaymentUid) <contactEmail> uid(sourceEmailUid) .
            uid(sourceEmailUid) <fingerprints> uid(sourceFingerUid) .
                        
            uid(sourceCountryUid) <dgraph.type> "Country" .
            uid(sourceCountryUid) <countryName> "Russia" .
            uid(sourceCountryUid) <payments> uid(sourcePaymentUid) .
            uid(sourceCountryUid) <tokens> uid(sourceTokenUid) .
            uid(sourcePaymentUid) <country> uid(sourceCountryUid) .
            uid(sourceCountryUid) <emails> uid(sourceEmailUid) .
            uid(sourceCountryUid) <ips> uid(sourceIpUid) .
                        
            uid(sourceIpUid) <dgraph.type> "IP" .
            uid(sourceIpUid) <ipAddress> "127.0.0.1" .
            uid(sourceIpUid) <payments> uid(sourcePaymentUid) .
            uid(sourceIpUid) <tokens> uid(sourceTokenUid) .
            uid(sourcePaymentUid) <operationIp> uid(sourceIpUid) .
            uid(sourceIpUid) <emails> uid(sourceEmailUid) .
            uid(sourceIpUid) <countries> uid(sourceCountryUid) .
                        
            uid(sourcePaymentUid) <dgraph.type> "Payment" .
            uid(sourcePaymentUid) <paymentId> "TestPayment" .
            uid(sourcePaymentUid) <partyId> "partyId-1" .
            uid(sourcePaymentUid) <shopId> "shopId-1" .
            uid(sourcePaymentUid) <createdAt> "2021-10-05T18:00:00" .
            uid(sourcePaymentUid) <amount> "1000" .
            uid(sourcePaymentUid) <currency> "RUB" .
            uid(sourcePaymentUid) <status> "captured" .
                        
            uid(sourcePaymentUid) <paymentTool> "tool" .
            uid(sourcePaymentUid) <terminal> "10001" .
            uid(sourcePaymentUid) <providerId> "21" .
            uid(sourcePaymentUid) <bankCountry> "Russia" .
            uid(sourcePaymentUid) <payerType> "type-1" .
            uid(sourcePaymentUid) <tokenProvider> "provider-1" .
            uid(sourcePaymentUid) <mobile> "false" .
            uid(sourcePaymentUid) <recurrent> "false" .
            uid(sourcePaymentUid) <cardToken> uid(sourceTokenUid) .
            uid(sourcePaymentUid) <partyShop> uid(sourceShopUid) .
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
            """;

    public static final String TEST_SMALL_REFUND_UPSERT_QUERY = """
            query all() {
                getTokenUid(func: type(Token)) @filter(eq(tokenId, "token")) {
                    sourceTokenUid as uid
                }
                        
                getShopUid(func: type(PartyShop)) @filter(eq(partyId, "Party") and eq(shopId, "Shop")) {
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
                        
                getShopUid(func: type(PartyShop)) @filter(eq(partyId, "Party") and eq(shopId, "Shop")) {
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
                        
                getIpUid(func: type(IP)) @filter(eq(ipAddress, "127.0.0.1")) {
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
                        
            uid(sourceShopUid) <dgraph.type> "PartyShop" .
            uid(sourceShopUid) <partyId> "Party" .
            uid(sourceShopUid) <shopId> "Shop" .
            uid(sourceShopUid) <refunds> uid(sourceRefundUid) .
            uid(sourceShopUid) <tokens> uid(sourceTokenUid) .
                        
            uid(sourceBinUid) <dgraph.type> "Bin" .
            uid(sourceBinUid) <cardBin> "000000" .
            uid(sourceBinUid) <refunds> uid(sourceRefundUid) .
            uid(sourceBinUid) <tokens> uid(sourceTokenUid) .
                        
            uid(sourceRefundUid) <dgraph.type> "Refund" .
            uid(sourceRefundUid) <paymentId> "TestPayId" .
            uid(sourceRefundUid) <refundId> "TestRefId" .
            uid(sourceRefundUid) <partyId> "Party" .
            uid(sourceRefundUid) <shopId> "Shop" .
            uid(sourceRefundUid) <createdAt> "2021-10-05T18:00:00" .
            uid(sourceRefundUid) <amount> "1000" .
            uid(sourceRefundUid) <currency> "RUB" .
            uid(sourceRefundUid) <status> "successful" .
            uid(sourceRefundUid) <payerType> "paid" .
                        
            uid(sourceRefundUid) <cardToken> uid(sourceTokenUid) .
            uid(sourceRefundUid) <partyShop> uid(sourceShopUid) .
            uid(sourceRefundUid) <bin> uid(sourceBinUid) .
                        
            uid(sourcePaymentUid) <dgraph.type> "Payment" .
            uid(sourcePaymentUid) <paymentId> "TestPayId" .
            uid(sourcePaymentUid) <refunds> uid(sourceRefundUid) .
            """;

    public static final String TEST_INSERT_FULL_REFUND_BLOCK = """
            uid(sourceTokenUid) <dgraph.type> "Token" .
            uid(sourceTokenUid) <tokenId> "token" .
            uid(sourceTokenUid) <bin> uid(sourceBinUid) .
            uid(sourceTokenUid) <maskedPan> "maskedPan" .
            uid(sourceTokenUid) <lastActTime> "2021-10-05T18:00:00" .
            uid(sourceTokenUid) <refunds> uid(sourceRefundUid) (createdAt = 2021-10-05T18:00:00, status = "successful") .
                        
            uid(sourceShopUid) <dgraph.type> "PartyShop" .
            uid(sourceShopUid) <partyId> "Party" .
            uid(sourceShopUid) <shopId> "Shop" .
            uid(sourceShopUid) <refunds> uid(sourceRefundUid) .
            uid(sourceShopUid) <tokens> uid(sourceTokenUid) .
                        
            uid(sourceBinUid) <dgraph.type> "Bin" .
            uid(sourceBinUid) <cardBin> "000000" .
            uid(sourceBinUid) <refunds> uid(sourceRefundUid) .
            uid(sourceBinUid) <tokens> uid(sourceTokenUid) .
                        
            uid(sourceFingerUid) <dgraph.type> "Fingerprint" .
            uid(sourceFingerUid) <fingerprintData> "fData" .
            uid(sourceFingerUid) <lastActTime> "2021-10-05T18:00:00" .
            uid(sourceFingerUid) <refunds> uid(sourceRefundUid) .
            uid(sourceFingerUid) <tokens> uid(sourceTokenUid) .
            uid(sourceTokenUid) <fingerprints> uid(sourceFingerUid) .
            uid(sourceRefundUid) <fingerprint> uid(sourceFingerUid) .
            uid(sourceFingerUid) <emails> uid(sourceEmailUid) .
                        
            uid(sourceEmailUid) <dgraph.type> "Email" .
            uid(sourceEmailUid) <userEmail> "1@1.ru" .
            uid(sourceEmailUid) <lastActTime> "2021-10-05T18:00:00" .
            uid(sourceEmailUid) <refunds> uid(sourceRefundUid) .
            uid(sourceEmailUid) <tokens> uid(sourceTokenUid) .
            uid(sourceBinUid) <emails> uid(sourceEmailUid) .
            uid(sourceShopUid) <emails> uid(sourceEmailUid) .
            uid(sourceTokenUid) <emails> uid(sourceEmailUid) .
            uid(sourceRefundUid) <contactEmail> uid(sourceEmailUid) .
            uid(sourceEmailUid) <fingerprints> uid(sourceFingerUid) .
                        
            uid(sourceIpUid) <dgraph.type> "IP" .
            uid(sourceIpUid) <ipAddress> "127.0.0.1" .
            uid(sourceIpUid) <refunds> uid(sourceRefundUid) .
            uid(sourceIpUid) <tokens> uid(sourceTokenUid) .
            uid(sourceRefundUid) <operationIp> uid(sourceIpUid) .
            uid(sourceIpUid) <emails> uid(sourceEmailUid) .
                        
            uid(sourceRefundUid) <dgraph.type> "Refund" .
            uid(sourceRefundUid) <paymentId> "TestPayId" .
            uid(sourceRefundUid) <refundId> "TestRefId" .
            uid(sourceRefundUid) <partyId> "Party" .
            uid(sourceRefundUid) <shopId> "Shop" .
            uid(sourceRefundUid) <createdAt> "2021-10-05T18:00:00" .
            uid(sourceRefundUid) <amount> "1000" .
            uid(sourceRefundUid) <currency> "RUB" .
            uid(sourceRefundUid) <status> "successful" .
            uid(sourceRefundUid) <payerType> "paid" .
                        
            uid(sourceRefundUid) <cardToken> uid(sourceTokenUid) .
            uid(sourceRefundUid) <partyShop> uid(sourceShopUid) .
            uid(sourceRefundUid) <bin> uid(sourceBinUid) .
                        
            uid(sourcePaymentUid) <dgraph.type> "Payment" .
            uid(sourcePaymentUid) <paymentId> "TestPayId" .
            uid(sourcePaymentUid) <refunds> uid(sourceRefundUid) .
            """;

    public static final String TEST_SMALL_CHARGEBACK_UPSERT_QUERY = """
            query all() {
                getTokenUid(func: type(Token)) @filter(eq(tokenId, "token")) {
                    sourceTokenUid as uid
                }
                        
                getShopUid(func: type(PartyShop)) @filter(eq(partyId, "Party") and eq(shopId, "Shop")) {
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
                        
                getShopUid(func: type(PartyShop)) @filter(eq(partyId, "Party") and eq(shopId, "Shop")) {
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
                        
                getIpUid(func: type(IP)) @filter(eq(ipAddress, "127.0.0.1")) {
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
            uid(sourceTokenUid) <chargebacks> uid(sourceChargebackUid) (createdAt = 2021-10-05T18:00:00, status = "successful") .
                        
            uid(sourceShopUid) <dgraph.type> "PartyShop" .
            uid(sourceShopUid) <partyId> "Party" .
            uid(sourceShopUid) <shopId> "Shop" .
            uid(sourceShopUid) <chargebacks> uid(sourceChargebackUid) .
            uid(sourceShopUid) <tokens> uid(sourceTokenUid) .
                        
            uid(sourceBinUid) <dgraph.type> "Bin" .
            uid(sourceBinUid) <cardBin> "000000" .
            uid(sourceBinUid) <chargebacks> uid(sourceChargebackUid) .
            uid(sourceBinUid) <tokens> uid(sourceTokenUid) .
                        
            uid(sourceChargebackUid) <dgraph.type> "Chargeback" .
            uid(sourceChargebackUid) <paymentId> "TestPayId" .
            uid(sourceChargebackUid) <chargebackId> "TestChargebackIdId" .
            uid(sourceChargebackUid) <partyId> "Party" .
            uid(sourceChargebackUid) <shopId> "Shop" .
            uid(sourceChargebackUid) <createdAt> "2021-10-05T18:00:00" .
            uid(sourceChargebackUid) <amount> "1000" .
            uid(sourceChargebackUid) <currency> "RUB" .
            uid(sourceChargebackUid) <status> "successful" .
            uid(sourceChargebackUid) <status> "successful" .
            uid(sourceChargebackUid) <category> "category" .
            uid(sourceChargebackUid) <code> "code404" .
            uid(sourceChargebackUid) <payerType> "paid" .
                        
            uid(sourceChargebackUid) <cardToken> uid(sourceTokenUid) .
            uid(sourceChargebackUid) <partyShop> uid(sourceShopUid) .
            uid(sourceChargebackUid) <bin> uid(sourceBinUid) .
                        
            uid(sourcePaymentUid) <dgraph.type> "Payment" .
            uid(sourcePaymentUid) <paymentId> "TestPayId" .
            uid(sourcePaymentUid) <chargebacks> uid(sourceChargebackUid) .
            """;

    public static final String TEST_INSERT_FULL_CHARGEBACK_BLOCK = """
            uid(sourceTokenUid) <dgraph.type> "Token" .
            uid(sourceTokenUid) <tokenId> "token" .
            uid(sourceTokenUid) <bin> uid(sourceBinUid) .
            uid(sourceTokenUid) <maskedPan> "maskedPan" .
            uid(sourceTokenUid) <lastActTime> "2021-10-05T18:00:00" .
            uid(sourceTokenUid) <chargebacks> uid(sourceChargebackUid) (createdAt = 2021-10-05T18:00:00, status = "successful") .
                        
            uid(sourceShopUid) <dgraph.type> "PartyShop" .
            uid(sourceShopUid) <partyId> "Party" .
            uid(sourceShopUid) <shopId> "Shop" .
            uid(sourceShopUid) <chargebacks> uid(sourceChargebackUid) .
            uid(sourceShopUid) <tokens> uid(sourceTokenUid) .
                        
            uid(sourceBinUid) <dgraph.type> "Bin" .
            uid(sourceBinUid) <cardBin> "000000" .
            uid(sourceBinUid) <chargebacks> uid(sourceChargebackUid) .
            uid(sourceBinUid) <tokens> uid(sourceTokenUid) .
                        
            uid(sourceFingerUid) <dgraph.type> "Fingerprint" .
            uid(sourceFingerUid) <fingerprintData> "fData" .
            uid(sourceFingerUid) <lastActTime> "2021-10-05T18:00:00" .
            uid(sourceFingerUid) <chargebacks> uid(sourceChargebackUid) .
            uid(sourceFingerUid) <tokens> uid(sourceTokenUid) .
            uid(sourceTokenUid) <fingerprints> uid(sourceFingerUid) .
            uid(sourceChargebackUid) <fingerprint> uid(sourceFingerUid) .
            uid(sourceFingerUid) <emails> uid(sourceEmailUid) .
                        
            uid(sourceEmailUid) <dgraph.type> "Email" .
            uid(sourceEmailUid) <userEmail> "1@1.ru" .
            uid(sourceEmailUid) <lastActTime> "2021-10-05T18:00:00" .
            uid(sourceEmailUid) <chargebacks> uid(sourceChargebackUid) .
            uid(sourceEmailUid) <tokens> uid(sourceTokenUid) .
            uid(sourceBinUid) <emails> uid(sourceEmailUid) .
            uid(sourceShopUid) <emails> uid(sourceEmailUid) .
            uid(sourceTokenUid) <emails> uid(sourceEmailUid) .
            uid(sourceChargebackUid) <contactEmail> uid(sourceEmailUid) .
            uid(sourceEmailUid) <fingerprints> uid(sourceFingerUid) .
                        
            uid(sourceIpUid) <dgraph.type> "IP" .
            uid(sourceIpUid) <ipAddress> "127.0.0.1" .
            uid(sourceIpUid) <chargebacks> uid(sourceChargebackUid) .
            uid(sourceIpUid) <tokens> uid(sourceTokenUid) .
            uid(sourceChargebackUid) <operationIp> uid(sourceIpUid) .
            uid(sourceIpUid) <emails> uid(sourceEmailUid) .
                        
            uid(sourceChargebackUid) <dgraph.type> "Chargeback" .
            uid(sourceChargebackUid) <paymentId> "TestPayId" .
            uid(sourceChargebackUid) <chargebackId> "TestChargebackIdId" .
            uid(sourceChargebackUid) <partyId> "Party" .
            uid(sourceChargebackUid) <shopId> "Shop" .
            uid(sourceChargebackUid) <createdAt> "2021-10-05T18:00:00" .
            uid(sourceChargebackUid) <amount> "1000" .
            uid(sourceChargebackUid) <currency> "RUB" .
            uid(sourceChargebackUid) <status> "successful" .
            uid(sourceChargebackUid) <status> "successful" .
            uid(sourceChargebackUid) <category> "category" .
            uid(sourceChargebackUid) <code> "code404" .
            uid(sourceChargebackUid) <payerType> "paid" .
                        
            uid(sourceChargebackUid) <cardToken> uid(sourceTokenUid) .
            uid(sourceChargebackUid) <partyShop> uid(sourceShopUid) .
            uid(sourceChargebackUid) <bin> uid(sourceBinUid) .
                        
            uid(sourcePaymentUid) <dgraph.type> "Payment" .
            uid(sourcePaymentUid) <paymentId> "TestPayId" .
            uid(sourcePaymentUid) <chargebacks> uid(sourceChargebackUid) .
            """;

    public static final String TEST_SMALL_WITHDRAWAL_UPSERT_QUERY = """
            query all() {
                
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
            uid(sourceWithdrawalUid) <currency> "RUB" .
            uid(sourceWithdrawalUid) <status> "status-1" .
            uid(sourceWithdrawalUid) <providerId> "123" .
            uid(sourceWithdrawalUid) <terminalId> "345" .
            uid(sourceWithdrawalUid) <destinationResource> "crypto_wallet" .
            uid(sourceWithdrawalUid) <cryptoWalletId> "CID-1" .
            uid(sourceWithdrawalUid) <cryptoWalletCurrency> "ETH" .
                        
            uid(sourceCountryUid) <dgraph.type> "Country" .
            uid(sourceCountryUid) <countryName> "Russia" .
            uid(sourceWithdrawalUid) <country> uid(sourceCountryUid) .
            
            """;

    public static final String TEST_INSERT_FULL_WITHDRAWAL_BLOCK = """
            uid(sourceWithdrawalUid) <dgraph.type> "Withdrawal" .
            uid(sourceWithdrawalUid) <withdrawalId> "Wid-1" .
            uid(sourceWithdrawalUid) <createdAt> "2021-10-05T18:00:00" .
            uid(sourceWithdrawalUid) <amount> "1000" .
            uid(sourceWithdrawalUid) <currency> "RUB" .
            uid(sourceWithdrawalUid) <status> "status-1" .
            uid(sourceWithdrawalUid) <providerId> "123" .
            uid(sourceWithdrawalUid) <terminalId> "345" .
            uid(sourceWithdrawalUid) <accountId> "AccId" .
            uid(sourceWithdrawalUid) <accountIdentity> "Iddy" .
            uid(sourceWithdrawalUid) <accountCurrency> "BSD" .
            uid(sourceWithdrawalUid) <errorCode> "code" .
            uid(sourceWithdrawalUid) <errorReason> "reason" .
            uid(sourceWithdrawalUid) <destinationResource> "bank_card" .
                        
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
