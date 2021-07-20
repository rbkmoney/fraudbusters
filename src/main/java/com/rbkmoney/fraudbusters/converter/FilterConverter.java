package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.damsel.fraudbusters.Filter;
import com.rbkmoney.damsel.fraudbusters.Page;
import com.rbkmoney.damsel.fraudbusters.Sort;
import com.rbkmoney.fraudbusters.constant.PaymentField;
import com.rbkmoney.fraudbusters.service.dto.FilterDto;
import com.rbkmoney.fraudbusters.service.dto.SortDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FilterConverter {

    public FilterDto convert(Filter filter, Page page, Sort sort) {
        FilterDto filterDto = new FilterDto();
        Map<PaymentField, String> searchPatterns = assembleSearchPatterns(filter);
        filterDto.setSearchPatterns(searchPatterns);
        filterDto.setLastId(page.getContinuationId());
        filterDto.setSize(page.getSize());
        if (filter.isSetInterval()) {
            filterDto.setTimeFrom(filter.getInterval().getLowerBound().getBoundTime());
            filterDto.setTimeTo(filter.getInterval().getUpperBound().getBoundTime());
        }
        SortDto sortDto = new SortDto();
        sortDto.setField(sort.getField());
        sortDto.setOrder(com.rbkmoney.fraudbusters.constant.SortOrder.valueOf(sort.getOrder().name()));
        filterDto.setSort(sortDto);
        return filterDto;
    }

    @NotNull
    private Map<PaymentField, String> assembleSearchPatterns(Filter filter) {
        Map<PaymentField, String> searchPatterns = new HashMap<>();
        if (filter.isSetCardToken()) {
            searchPatterns.put(PaymentField.CARD_TOKEN, filter.getCardToken());
        }
        if (filter.isSetEmail()) {
            searchPatterns.put(PaymentField.EMAIL, filter.getEmail());
        }
        if (filter.isSetStatus()) {
            searchPatterns.put(PaymentField.STATUS, filter.getStatus());
        }
        if (filter.isSetShopId()) {
            searchPatterns.put(PaymentField.SHOP_ID, filter.getShopId());
        }
        if (filter.isSetPartyId()) {
            searchPatterns.put(PaymentField.PARTY_ID, filter.getPartyId());
        }
        if (filter.isSetProviderCountry()) {
            searchPatterns.put(PaymentField.BANK_COUNTRY, filter.getProviderCountry());
        }
        if (filter.isSetFingerprint()) {
            searchPatterns.put(PaymentField.FINGERPRINT, filter.getFingerprint());
        }
        if (filter.isSetTerminal()) {
            searchPatterns.put(PaymentField.TERMINAL, filter.getTerminal());
        }
        if (filter.isSetPaymentId()) {
            searchPatterns.put(PaymentField.ID, filter.getPaymentId());
        }
        return searchPatterns;
    }

}
