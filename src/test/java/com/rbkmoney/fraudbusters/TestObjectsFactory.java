package com.rbkmoney.fraudbusters;

import com.rbkmoney.damsel.base.TimestampInterval;
import com.rbkmoney.damsel.base.TimestampIntervalBound;
import com.rbkmoney.damsel.fraudbusters.Filter;
import com.rbkmoney.damsel.fraudbusters.Page;
import com.rbkmoney.damsel.fraudbusters.Sort;
import com.rbkmoney.damsel.fraudbusters.SortOrder;
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

    public static Filter testFilter() {
        Filter filter = new Filter();
        String email = randomString();
        String cardToken = randomString();
        String status = randomString();
        String shopId = randomString();
        String partyId = randomString();
        String providerCountry = randomString();
        String fingerPrint = randomString();
        String terminal = randomString();
        String id = randomString();
        filter.setPartyId(partyId);
        filter.setEmail(email);
        filter.setCardToken(cardToken);
        filter.setFingerprint(fingerPrint);
        filter.setShopId(shopId);
        filter.setStatus(status);
        filter.setTerminal(terminal);
        filter.setProviderCountry(providerCountry);
        filter.setPaymentId(id);
        TimestampInterval timestampInterval = new TimestampInterval();
        TimestampIntervalBound lowerBound = new TimestampIntervalBound();
        String lowerBoundTime = LocalDateTime.now().toString();
        lowerBound.setBoundTime(lowerBoundTime);
        TimestampIntervalBound upperBound = new TimestampIntervalBound();
        String upperBoundTime = LocalDateTime.now().toString();
        upperBound.setBoundTime(upperBoundTime);
        timestampInterval.setLowerBound(lowerBound);
        timestampInterval.setUpperBound(upperBound);
        filter.setInterval(timestampInterval);
        return filter;
    }

    public static Sort testSort() {
        Sort sort = new Sort();
        sort.setField(randomString());
        sort.setOrder(SortOrder.DESC);
        return sort;
    }

    public static Page testPage() {
        Page page = new Page();
        String continuationId = randomString();
        Long size = randomLong();
        page.setSize(size);
        page.setContinuationId(continuationId);
        return page;
    }
}
