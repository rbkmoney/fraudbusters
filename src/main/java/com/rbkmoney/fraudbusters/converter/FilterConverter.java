package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.damsel.fraudbusters.Filter;
import com.rbkmoney.damsel.fraudbusters.Page;
import com.rbkmoney.damsel.fraudbusters.Sort;
import com.rbkmoney.fraudbusters.constant.PaymentField;
import com.rbkmoney.fraudbusters.service.dto.FilterDto;
import com.rbkmoney.fraudbusters.service.dto.SortDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class FilterConverter {

    public FilterDto convert(Filter filter, Page page, Sort sort) {
        FilterDto filterDto = new FilterDto();
        Map<PaymentField, String> searchPatterns = assembleSearchPatterns(filter);
        filterDto.setSearchPatterns(searchPatterns);
        filterDto.setLastId(page.getContinuationId());
        if (page.getSize() > 0) {
            filterDto.setSize(page.getSize());
        }
        if (filter.isSetInterval()) {
            filterDto.setTimeFrom(filter.getInterval().getLowerBound().getBoundTime());
            filterDto.setTimeTo(filter.getInterval().getUpperBound().getBoundTime());
        }
        SortDto sortDto = new SortDto();
        sortDto.setField(sort.getField());
        sortDto.setOrder(Objects.nonNull(sort.getOrder())
                ? com.rbkmoney.fraudbusters.constant.SortOrder.valueOf(sort.getOrder().name())
                : null);
        filterDto.setSort(sortDto);
        return filterDto;
    }

    @NotNull
    private Map<PaymentField, String> assembleSearchPatterns(Filter filter) {
        Map<PaymentField, String> searchPatterns = new HashMap<>();
        if (filter.isSetCardToken() && StringUtils.hasLength(filter.getCardToken())) {
            searchPatterns.put(PaymentField.CARD_TOKEN, filter.getCardToken());
        }
        if (filter.isSetEmail() && StringUtils.hasLength(filter.getEmail())) {
            searchPatterns.put(PaymentField.EMAIL, filter.getEmail());
        }
        if (filter.isSetStatus() && StringUtils.hasLength(filter.getStatus())) {
            searchPatterns.put(PaymentField.STATUS, filter.getStatus());
        }
        if (filter.isSetShopId() && StringUtils.hasLength(filter.getShopId())) {
            searchPatterns.put(PaymentField.SHOP_ID, filter.getShopId());
        }
        if (filter.isSetPartyId() && StringUtils.hasLength(filter.getPartyId())) {
            searchPatterns.put(PaymentField.PARTY_ID, filter.getPartyId());
        }
        if (filter.isSetProviderCountry() && StringUtils.hasLength(filter.getProviderCountry())) {
            searchPatterns.put(PaymentField.BANK_COUNTRY, filter.getProviderCountry());
        }
        if (filter.isSetFingerprint() && StringUtils.hasLength(filter.getFingerprint())) {
            searchPatterns.put(PaymentField.FINGERPRINT, filter.getFingerprint());
        }
        if (filter.isSetTerminal() && StringUtils.hasLength(filter.getTerminal())) {
            searchPatterns.put(PaymentField.TERMINAL, filter.getTerminal());
        }
        if (filter.isSetPaymentId() && StringUtils.hasLength(filter.getPaymentId())) {
            searchPatterns.put(PaymentField.ID, filter.getPaymentId());
        }
        if (filter.isSetMaskedPan() && StringUtils.hasLength(filter.getMaskedPan())) {
            searchPatterns.put(PaymentField.MASKED_PAN, filter.getMaskedPan());
        }
        if (filter.isSetInvoiceId() && StringUtils.hasLength(filter.getInvoiceId())) {
            searchPatterns.put(PaymentField.INVOICE_ID, filter.getInvoiceId());
        }
        return searchPatterns;
    }

}
