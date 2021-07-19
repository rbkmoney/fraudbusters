package com.rbkmoney.fraudbusters;

import com.rbkmoney.fraudbusters.constant.PaymentStatus;
import com.rbkmoney.fraudbusters.domain.CheckedPayment;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class TestObjectsFactory {

    public static CheckedPayment testCheckedPayment() {
        CheckedPayment checkedPayment = new CheckedPayment();
        checkedPayment.setAmount(randomLong());
        checkedPayment.setEmail(randomString());
        checkedPayment.setPaymentSystem(randomString());
        checkedPayment.setCurrency(randomString());
        checkedPayment.setPartyId(randomString());
        checkedPayment.setFingerprint(randomString());
        checkedPayment.setBankCountry(randomString());
        checkedPayment.setCardToken(randomString());
        checkedPayment.setIp(randomString());
        checkedPayment.setId(randomString());
        checkedPayment.setShopId(randomString());
        checkedPayment.setPaymentTool(randomString());
        checkedPayment.setPaymentCountry(randomString());
        checkedPayment.setTerminal(randomString());
        checkedPayment.setEventTime(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        checkedPayment.setPaymentStatus(PaymentStatus.processed.toString());
        checkedPayment.setProviderId(randomString());
        return checkedPayment;
    }

    public static List<CheckedPayment> testCheckedPayments(int n) {
        return IntStream.rangeClosed(1, n)
                .mapToObj(value -> testCheckedPayment())
                .collect(Collectors.toList());
    }

    public static Long randomLong() {
        return ThreadLocalRandom.current().nextLong(1000);
    }

    public static String randomString() {
        return UUID.randomUUID().toString();
    }
}
