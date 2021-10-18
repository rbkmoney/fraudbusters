package com.rbkmoney.fraudbusters.factory;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.damsel.fraudbusters.ClientInfo;
import com.rbkmoney.fraudbusters.domain.dgraph.*;
import com.rbkmoney.fraudbusters.factory.properties.OperationProperties;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestDgraphObjectFactory {

    public static DgraphPayment createSmallTestDgraphPayment() {
        return createTestDgraphPayment(false, false, false, false);
    }

    public static DgraphPayment createFullTestDgraphPayment() {
        return createTestDgraphPayment(true, true, true, true);
    }

    public static DgraphPayment createTestDgraphPayment(boolean ipExists,
                                                        boolean countryExists,
                                                        boolean fingerprintExists,
                                                        boolean emailExists) {
        DgraphPayment dgraphPayment = new DgraphPayment();
        dgraphPayment.setPaymentId("TestPayment");
        String partyId = "partyId-1";
        String shopId = "shopId-1";
        dgraphPayment.setPartyId(partyId);
        dgraphPayment.setShopId(shopId);
        dgraphPayment.setPartyShop(createTestDgraphPartyShop(partyId, shopId));

        dgraphPayment.setCreatedAt("2021-10-05T18:00:00");
        dgraphPayment.setAmount(1000L);
        dgraphPayment.setCurrency("RUB");
        dgraphPayment.setStatus("captured");
        dgraphPayment.setPaymentTool("tool");
        dgraphPayment.setTerminal("10001");
        dgraphPayment.setProviderId("21");
        dgraphPayment.setBankCountry("Russia");
        dgraphPayment.setPayerType("type-1");
        dgraphPayment.setTokenProvider("provider-1");
        dgraphPayment.setMobile(false);
        dgraphPayment.setRecurrent(false);
        dgraphPayment.setErrorReason(null);
        dgraphPayment.setErrorCode(null);
        dgraphPayment.setCheckedTemplate(null);
        dgraphPayment.setCheckedRule(null);
        dgraphPayment.setResultStatus(null);
        dgraphPayment.setCheckedResultsJson(null);
        dgraphPayment.setBin(createTestDgraphBin());
        dgraphPayment.setCardToken(createTestDgraphToken("token-1", "pan-1"));

        dgraphPayment.setCountry(countryExists ? createTestDgraphCountry() : null);
        dgraphPayment.setPaymentIp(ipExists ? createTestDgraphIp() : null);
        dgraphPayment.setFingerprint(fingerprintExists ? createTestDgraphFingerprint() : null);
        dgraphPayment.setContactEmail(emailExists ? createTestDgraphEmail() : null);
        return dgraphPayment;
    }

    public static Payment generatePayment(OperationProperties properties, int idx) {
        Payment payment = new Payment();
        payment.setId("Pay-" + Instant.now().toEpochMilli() + "-" + idx);
        ReferenceInfo referenceInfo = new ReferenceInfo();
        referenceInfo.setMerchantInfo(
                new MerchantInfo()
                        .setPartyId(properties.getPartyId())
                        .setShopId(properties.getShopId())
        );
        payment.setReferenceInfo(referenceInfo);
        payment.setEventTime(Instant.now().toString());
        payment.setCost(
                new Cash()
                        .setAmount(1000L)
                        .setCurrency(new CurrencyRef().setSymbolicCode("RUB"))
        );
        payment.setStatus(PaymentStatus.captured);
        payment.setPaymentTool(PaymentTool.bank_card(
                new BankCard()
                        .setToken(properties.getTokenId())
                        .setBin(properties.getBin())
                        .setLastDigits("0000")
                        .setPaymentToken(new BankCardTokenServiceRef().setId("PT-111"))
                        .setPaymentSystem(new PaymentSystemRef().setId("PS-111"))
        ));
        payment.setProviderInfo(
                new ProviderInfo()
                        .setProviderId("Provider-1")
                        .setTerminalId("Terminal-001")
                        .setCountry(properties.getCountry())
        );
        payment.setMobile(false);
        payment.setRecurrent(false);
        payment.setError(null);
        payment.setClientInfo(
                new ClientInfo()
                        .setEmail(properties.getEmail())
                        .setFingerprint(properties.getFingerprint())
                        .setIp(properties.getIp())
        );
        return payment;
    }

    public static DgraphFraudPayment createTestFraudDgraphPayment() {
        return createTestFraudDgraphPayment("pay-1", "2021-10-05T18:00:00");
    }

    public static DgraphFraudPayment createTestFraudDgraphPayment(String paymentId, String createdAt) {
        DgraphFraudPayment payment = new DgraphFraudPayment();
        payment.setPaymentId(paymentId);
        payment.setCreatedAt(createdAt);
        payment.setFraudType("simple fraud");
        payment.setComment("some comment");
        return payment;
    }

    public static FraudPayment createTestFraudPayment(String paymentId, String createdAt) {
        FraudPayment payment = new FraudPayment();
        payment.setId(paymentId);
        payment.setEventTime(createdAt);
        payment.setType("simple fraud");
        payment.setComment("some comment");
        return payment;
    }

    public static DgraphRefund createSmallTestDgraphRefund() {
        return createTestDgraphRefund(false, false, false);
    }

    public static DgraphRefund createFullTestDgraphRefund() {
        return createTestDgraphRefund(true, true, true);
    }

    public static DgraphRefund createTestDgraphRefund(boolean ipExists,
                                                      boolean fingerprintExists,
                                                      boolean emailExists) {
        DgraphRefund dgraphRefund = new DgraphRefund();
        dgraphRefund.setRefundId("TestRefId");
        dgraphRefund.setPaymentId("TestPayId");
        dgraphRefund.setPartyId("Party");
        dgraphRefund.setShopId("Shop");
        dgraphRefund.setPartyShop(createTestDgraphPartyShop("Party", "Shop"));
        dgraphRefund.setCreatedAt("2021-10-05T18:00:00");
        dgraphRefund.setAmount(1000L);
        dgraphRefund.setCurrency("RUB");
        dgraphRefund.setStatus("successful");
        dgraphRefund.setPayerType("paid");
        dgraphRefund.setErrorCode(null);
        dgraphRefund.setErrorReason(null);
        dgraphRefund.setPayment(createTestDgraphPaymentLink("TestPayId"));
        dgraphRefund.setCardToken(createTestDgraphToken("token", "maskedPan"));
        dgraphRefund.setBin(createTestDgraphBin());
        dgraphRefund.setFingerprint(fingerprintExists ? createTestDgraphFingerprint() : null);
        dgraphRefund.setRefundIp(ipExists ? createTestDgraphIp() : null);
        dgraphRefund.setEmail(emailExists ? createTestDgraphEmail() : null);
        return dgraphRefund;
    }

    private static DgraphPartyShop createTestDgraphPartyShop(String partyId, String shopId) {
        DgraphPartyShop partyShop = new DgraphPartyShop();
        partyShop.setPartyId(partyId);
        partyShop.setShopId(shopId);
        return partyShop;
    }

    private static DgraphBin createTestDgraphBin() {
        DgraphBin dgraphBin = new DgraphBin();
        dgraphBin.setBin("000000");
        return dgraphBin;
    }

    private static DgraphToken createTestDgraphToken(String tokenId, String maskedPan) {
        DgraphToken dgraphToken = new DgraphToken();
        dgraphToken.setTokenId(tokenId);
        dgraphToken.setMaskedPan(maskedPan);
        return dgraphToken;
    }

    private static DgraphCountry createTestDgraphCountry() {
        DgraphCountry country = new DgraphCountry();
        country.setCountryName("Russia");
        return country;
    }

    private static DgraphIp createTestDgraphIp() {
        DgraphIp dgraphIp = new DgraphIp();
        dgraphIp.setIp("127.0.0.1");
        return dgraphIp;
    }

    private static DgraphFingerprint createTestDgraphFingerprint() {
        DgraphFingerprint fingerprint = new DgraphFingerprint();
        fingerprint.setFingerprintData("fData");
        return fingerprint;
    }

    private static DgraphEmail createTestDgraphEmail() {
        DgraphEmail dgraphEmail = new DgraphEmail();
        dgraphEmail.setUserEmail("1@1.ru");
        return dgraphEmail;
    }

    private static DgraphPayment createTestDgraphPaymentLink(String paymentId) {
        DgraphPayment dgraphPayment = new DgraphPayment();
        dgraphPayment.setPaymentId(paymentId);
        return dgraphPayment;
    }

    public static Refund generateRefund(OperationProperties properties, int idx) {
        Refund refund = new Refund();
        refund.setId("Refund-" + idx);
        refund.setPaymentId(properties.getPaymentId());
        ReferenceInfo referenceInfo = new ReferenceInfo();
        referenceInfo.setMerchantInfo(
                new MerchantInfo()
                        .setPartyId(properties.getPartyId())
                        .setShopId(properties.getShopId())
        );
        refund.setReferenceInfo(referenceInfo);
        refund.setEventTime(Instant.now().toString());
        refund.setCost(
                new Cash()
                        .setAmount(1000L)
                        .setCurrency(new CurrencyRef().setSymbolicCode("RUB"))
        );
        refund.setStatus(RefundStatus.succeeded);
        refund.setPayerType(PayerType.customer);
        refund.setPaymentTool(PaymentTool.bank_card(
                new BankCard()
                        .setToken(properties.getTokenId())
                        .setBin(properties.getBin())
                        .setLastDigits("0000")
                        .setPaymentToken(new BankCardTokenServiceRef().setId("PT-111"))
                        .setPaymentSystem(new PaymentSystemRef().setId("PS-111"))
        ));
        refund.setProviderInfo(
                new ProviderInfo()
                        .setProviderId("Provider-1")
                        .setTerminalId("Terminal-001")
                        .setCountry(properties.getCountry())
        );
        refund.setError(null);
        refund.setClientInfo(
                new ClientInfo()
                        .setEmail(properties.getEmail())
                        .setFingerprint(properties.getFingerprint())
                        .setIp(properties.getIp())
        );
        return refund;
    }

}
