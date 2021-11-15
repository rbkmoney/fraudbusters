package com.rbkmoney.fraudbusters.factory;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.fraudbusters.ClientInfo;
import com.rbkmoney.damsel.fraudbusters.CryptoWallet;
import com.rbkmoney.damsel.fraudbusters.DigitalWallet;
import com.rbkmoney.damsel.fraudbusters.Error;
import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.fraudbusters.domain.dgraph.*;
import com.rbkmoney.fraudbusters.factory.properties.OperationProperties;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
        dgraphPayment.setParty(createTestDgraphParty(partyId));
        dgraphPayment.setShop(createTestDgraphShop(shopId));

        dgraphPayment.setCreatedAt("2021-10-05T18:00:00");
        dgraphPayment.setAmount(1000L);
        dgraphPayment.setCurrency(createDefaultDgraphCurrency());
        dgraphPayment.setStatus("captured");
        dgraphPayment.setPaymentTool("tool");
        dgraphPayment.setTerminal("10001");
        dgraphPayment.setProviderId("21");
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
        dgraphPayment.setOperationIp(ipExists ? createTestDgraphIp() : null);
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
        payment.setEventTime(properties.isEventTimeDispersion()
                ? Instant.now().minus(idx, ChronoUnit.MINUTES).toString() : Instant.now().toString());
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
                        .setLastDigits(properties.getMaskedPan())
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
        dgraphRefund.setParty(createTestDgraphParty("Party"));
        dgraphRefund.setShop(createTestDgraphShop("Shop"));
        dgraphRefund.setCreatedAt("2021-10-05T18:00:00");
        dgraphRefund.setAmount(1000L);
        dgraphRefund.setCurrency(createDefaultDgraphCurrency());
        dgraphRefund.setStatus("successful");
        dgraphRefund.setPayerType("paid");
        dgraphRefund.setErrorCode(null);
        dgraphRefund.setErrorReason(null);
        dgraphRefund.setSourcePayment(createTestDgraphPaymentLink("TestPayId"));
        dgraphRefund.setCardToken(createTestDgraphToken("token", "maskedPan"));
        dgraphRefund.setBin(createTestDgraphBin());
        dgraphRefund.setFingerprint(fingerprintExists ? createTestDgraphFingerprint() : null);
        dgraphRefund.setOperationIp(ipExists ? createTestDgraphIp() : null);
        dgraphRefund.setContactEmail(emailExists ? createTestDgraphEmail() : null);
        return dgraphRefund;
    }

    public static DgraphChargeback createSmallTestDgraphChargeback() {
        return createTestDgraphChargeback(false, false, false);
    }

    public static DgraphChargeback createFullTestDgraphChargeback() {
        return createTestDgraphChargeback(true, true, true);
    }

    public static DgraphChargeback createTestDgraphChargeback(boolean ipExists,
                                                              boolean fingerprintExists,
                                                              boolean emailExists) {
        DgraphChargeback dgraphChargeback = new DgraphChargeback();
        dgraphChargeback.setChargebackId("TestChargebackIdId");
        dgraphChargeback.setPaymentId("TestPayId");
        dgraphChargeback.setParty(createTestDgraphParty("Party"));
        dgraphChargeback.setShop(createTestDgraphShop("Shop"));
        dgraphChargeback.setCreatedAt("2021-10-05T18:00:00");
        dgraphChargeback.setAmount(1000L);
        dgraphChargeback.setCurrency(createDefaultDgraphCurrency());
        dgraphChargeback.setStatus("successful");
        dgraphChargeback.setPayerType("paid");
        dgraphChargeback.setCategory("category");
        dgraphChargeback.setCode("code404");
        dgraphChargeback.setPayment(createTestDgraphPaymentLink("TestPayId"));
        dgraphChargeback.setCardToken(createTestDgraphToken("token", "maskedPan"));
        dgraphChargeback.setBin(createTestDgraphBin());
        dgraphChargeback.setFingerprint(fingerprintExists ? createTestDgraphFingerprint() : null);
        dgraphChargeback.setOperationIp(ipExists ? createTestDgraphIp() : null);
        dgraphChargeback.setEmail(emailExists ? createTestDgraphEmail() : null);
        return dgraphChargeback;
    }

    private static DgraphParty createTestDgraphParty(String partyId) {
        DgraphParty dgraphParty = new DgraphParty();
        dgraphParty.setPartyId(partyId);
        return dgraphParty;
    }

    private static DgraphShop createTestDgraphShop(String shopId) {
        DgraphShop dgraphShop = new DgraphShop();
        dgraphShop.setShopId(shopId);
        return dgraphShop;
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

    public static Chargeback generateChargeback(OperationProperties properties, int idx) {
        Chargeback chargeback = new Chargeback();
        chargeback.setId("Chargeback-" + idx);
        chargeback.setPaymentId(properties.getPaymentId());
        ReferenceInfo referenceInfo = new ReferenceInfo();
        referenceInfo.setMerchantInfo(
                new MerchantInfo()
                        .setPartyId(properties.getPartyId())
                        .setShopId(properties.getShopId())
        );
        chargeback.setReferenceInfo(referenceInfo);
        chargeback.setEventTime(Instant.now().toString());
        chargeback.setCost(
                new Cash()
                        .setAmount(1000L)
                        .setCurrency(new CurrencyRef().setSymbolicCode("RUB"))
        );
        chargeback.setStatus(ChargebackStatus.accepted);
        chargeback.setPayerType(PayerType.customer);
        chargeback.setPaymentTool(PaymentTool.bank_card(
                new BankCard()
                        .setToken(properties.getTokenId())
                        .setBin(properties.getBin())
                        .setLastDigits("0000")
                        .setPaymentToken(new BankCardTokenServiceRef().setId("PT-111"))
                        .setPaymentSystem(new PaymentSystemRef().setId("PS-111"))
        ));
        chargeback.setProviderInfo(
                new ProviderInfo()
                        .setProviderId("Provider-1")
                        .setTerminalId("Terminal-001")
                        .setCountry(properties.getCountry())
        );
        chargeback.setCategory(ChargebackCategory.fraud);
        chargeback.setChargebackCode("code404");
        chargeback.setClientInfo(
                new ClientInfo()
                        .setEmail(properties.getEmail())
                        .setFingerprint(properties.getFingerprint())
                        .setIp(properties.getIp())
        );
        return chargeback;
    }

    public static DgraphWithdrawal createTestSmallDgraphWithdrawal() {
        return createTestDgraphWithdrawal(
                false,
                false,
                Resource.crypto_wallet(new CryptoWallet().setId("CID").setCurrency("ETH"))
        );
    }

    public static DgraphWithdrawal createTestFullDgraphWithdrawal() {
        return createTestDgraphWithdrawal(
                true,
                true,
                Resource.bank_card(generateTestBankCard("token1"))
        );
    }

    private static DgraphCurrency createDefaultDgraphCurrency() {
        return createDefaultDgraphCurrency("RUB");
    }

    private static DgraphCurrency createDefaultDgraphCurrency(String currencyCode) {
        DgraphCurrency currency = new DgraphCurrency();
        currency.setCurrencyCode(currencyCode);
        return currency;
    }

    private static DgraphWithdrawal createTestDgraphWithdrawal(boolean accountExists,
                                                               boolean errorExists,
                                                               Resource resource) {
        DgraphWithdrawal dgraphWithdrawal = new DgraphWithdrawal();
        dgraphWithdrawal.setWithdrawalId("Wid-1");
        dgraphWithdrawal.setCreatedAt("2021-10-05T18:00:00");
        dgraphWithdrawal.setAmount(1000L);
        dgraphWithdrawal.setCurrency(createDefaultDgraphCurrency());
        dgraphWithdrawal.setStatus("status-1");
        dgraphWithdrawal.setProviderId("123");
        dgraphWithdrawal.setTerminalId("345");
        dgraphWithdrawal.setCountry(createTestDgraphCountry());
        if (accountExists) {
            dgraphWithdrawal.setAccountId("AccId");
            dgraphWithdrawal.setAccountIdentity("Iddy");
            dgraphWithdrawal.setAccountCurrency(createDefaultDgraphCurrency("BSD"));
        }
        if (errorExists) {
            dgraphWithdrawal.setErrorReason("reason");
            dgraphWithdrawal.setErrorCode("code");
        }
        dgraphWithdrawal.setDestinationResource(resource.getSetField().getFieldName());
        if (resource.isSetCryptoWallet()) {
            dgraphWithdrawal.setCryptoWalletId("CID-1");
            dgraphWithdrawal.setCryptoWalletCurrency(createDefaultDgraphCurrency("ETH"));
        } else if (resource.isSetDigitalWallet()) {
            dgraphWithdrawal.setDigitalWalletId("DGT-1");
            dgraphWithdrawal.setDigitalWalletDataProvider("P-1");
        } else if (resource.isSetBankCard()) {
            dgraphWithdrawal.setBin(createTestDgraphBin());
            dgraphWithdrawal.setCardToken(createTestDgraphToken("tokenID", "MaskedPAN"));
        }
        return dgraphWithdrawal;
    }

    public static Withdrawal generateWithdrawal(int idx,
                                                Resource destinationResource) {
        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setId("WID-" + idx + "-" + Instant.now().toEpochMilli());
        withdrawal.setEventTime(LocalDateTime.now().toString());
        withdrawal.setDestinationResource(destinationResource);
        withdrawal.setCost(new Cash().setAmount(1000L).setCurrency(new CurrencyRef().setSymbolicCode("RUB")));
        withdrawal.setStatus(WithdrawalStatus.succeeded);
        withdrawal.setAccount(
                new Account()
                        .setId("AID-1")
                        .setCurrency(new CurrencyRef().setSymbolicCode("RUB"))
                        .setIdentity("IDX_P_1"));
        withdrawal.setError(
                new Error()
                        .setErrorCode("CODE-1")
                        .setErrorReason("Reason-2")
        );
        withdrawal.setProviderInfo(
                new ProviderInfo()
                        .setTerminalId("term001")
                        .setProviderId("prov001")
                        .setCountry("Russia")
        );
        return withdrawal;
    }

    public static DigitalWallet generateTestDigitalWallet(String id, String provider) {
        return new DigitalWallet()
                .setId(id)
                .setDigitalDataProvider(provider);
    }

    public static CryptoWallet generateTestCryptoWallet(String id, String currency) {
        return new CryptoWallet()
                .setId(id)
                .setCurrency(currency);
    }

    public static BankCard generateTestBankCard(String tokenId) {
        return new BankCard()
                .setToken(tokenId)
                .setPaymentSystem(new PaymentSystemRef().setId("123"))
                .setBin("000111")
                .setLastDigits("0000")
                .setPaymentToken(new BankCardTokenServiceRef().setId("321"))
                .setTokenizationMethod(TokenizationMethod.dpan)
                .setIssuerCountry(CountryCode.RUS)
                .setBankName("BaBank")
                .setCardholderName("IVAN IV")
                .setCategory("CTGR");
    }

}
