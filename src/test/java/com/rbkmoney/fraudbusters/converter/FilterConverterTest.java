package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.damsel.base.TimestampInterval;
import com.rbkmoney.damsel.base.TimestampIntervalBound;
import com.rbkmoney.damsel.fraudbusters.Filter;
import com.rbkmoney.damsel.fraudbusters.Page;
import com.rbkmoney.fraudbusters.TestObjectsFactory;
import com.rbkmoney.fraudbusters.constant.PaymentField;
import com.rbkmoney.fraudbusters.service.dto.FilterDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


class FilterConverterTest {

    private final FilterConverter filterConverter = new FilterConverter();

    @Test
    void convert() {
        Filter filter = new Filter();
        String email = TestObjectsFactory.randomString();
        String cardToken = TestObjectsFactory.randomString();
        String status = TestObjectsFactory.randomString();
        String shopId = TestObjectsFactory.randomString();
        String partyId = TestObjectsFactory.randomString();
        String providerCountry = TestObjectsFactory.randomString();
        String fingerPrint = TestObjectsFactory.randomString();
        String terminal = TestObjectsFactory.randomString();
        filter.setPartyId(partyId);
        filter.setEmail(email);
        filter.setCardToken(cardToken);
        filter.setFingerprint(fingerPrint);
        filter.setShopId(shopId);
        filter.setStatus(status);
        filter.setTerminal(terminal);
        filter.setProviderCountry(providerCountry);
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
        Page page = new Page();
        String continuationId = TestObjectsFactory.randomString();
        Long size = TestObjectsFactory.randomLong();
        page.setSize(size);
        page.setContinuationId(continuationId);

        FilterDto dto = filterConverter.convert(filter, page);

        assertEquals(page.getSize(), dto.getSize());
        assertEquals(page.getContinuationId(), dto.getLastId());
        assertEquals(lowerBoundTime, dto.getTimeFrom());
        assertEquals(upperBoundTime, dto.getTimeTo());
        Map<PaymentField, String> searchPatterns = dto.getSearchPatterns();
        assertEquals(filter.getCardToken(), searchPatterns.get(PaymentField.CARD_TOKEN));
        assertEquals(filter.getEmail(), searchPatterns.get(PaymentField.EMAIL));
        assertEquals(filter.getFingerprint(), searchPatterns.get(PaymentField.FINGERPRINT));
        assertEquals(filter.getPartyId(), searchPatterns.get(PaymentField.PARTY_ID));
        assertEquals(filter.getShopId(), searchPatterns.get(PaymentField.SHOP_ID));
        assertEquals(filter.getStatus(), searchPatterns.get(PaymentField.STATUS));
        assertEquals(filter.getProviderCountry(), searchPatterns.get(PaymentField.BANK_COUNTRY));
        assertEquals(filter.getTerminal(), searchPatterns.get(PaymentField.TERMINAL));


    }
}