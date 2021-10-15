package com.rbkmoney.fraudbusters.factory;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.damsel.fraudbusters.ClientInfo;
import com.rbkmoney.fraudbusters.domain.dgraph.*;
import com.rbkmoney.fraudbusters.factory.properties.PaymentProperties;
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
        DgraphPartyShop partyShop = new DgraphPartyShop();
        partyShop.setPartyId(partyId);
        partyShop.setShopId(shopId);
        dgraphPayment.setPartyShop(partyShop);

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

        DgraphBin dgraphBin = new DgraphBin();
        dgraphBin.setBin("000000");
        dgraphPayment.setDgraphBin(dgraphBin);
        DgraphToken dgraphToken = new DgraphToken();
        dgraphToken.setTokenId("token-1");
        dgraphToken.setMaskedPan("pan-1");
        dgraphPayment.setCardToken(dgraphToken);
        if (countryExists) {
            DgraphCountry country = new DgraphCountry();
            country.setCountryName("Russia");
            dgraphPayment.setCountry(country);
        }
        if (ipExists) {
            DgraphIp dgraphIp = new DgraphIp();
            dgraphIp.setIp("127.0.0.1");
            dgraphPayment.setDgraphIp(dgraphIp);
        }
        if (fingerprintExists) {
            DgraphFingerprint fingerprint = new DgraphFingerprint();
            fingerprint.setFingerprintData("fData");
            dgraphPayment.setFingerprint(fingerprint);
        }
        if (emailExists) {
            DgraphEmail dgraphEmail = new DgraphEmail();
            dgraphEmail.setUserEmail("1@1.ru");
            dgraphPayment.setContactEmail(dgraphEmail);
        }
        return dgraphPayment;
    }

    public static Payment generatePayment(PaymentProperties properties, int idx) {
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

}
