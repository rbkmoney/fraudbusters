package com.rbkmoney.fraudbusters.resource;

import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.fraudbusters.constant.PaymentField;
import com.rbkmoney.fraudbusters.converter.CheckedPaymentToPaymentInfoConverter;
import com.rbkmoney.fraudbusters.service.HistoricalDataService;
import com.rbkmoney.fraudbusters.service.dto.FilterDto;
import com.rbkmoney.fraudbusters.service.dto.HistoricalPaymentsDto;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoricalDataHandler implements HistoricalDataServiceSrv.Iface {

    private final HistoricalDataService historicalDataService;
    private final CheckedPaymentToPaymentInfoConverter converter;

    @Override
    public PaymentInfoResult getPayments(Filter filter, Page page) {
        FilterDto filterDto = buildFilter(filter, page);
        HistoricalPaymentsDto historicalPaymentsDto = historicalDataService.getPayments(filterDto);
        return buildResult(historicalPaymentsDto);
    }

    private FilterDto buildFilter(Filter filter, Page page) {
        FilterDto filterDto = new FilterDto();
        Map<PaymentField, String> searchPatterns = assembleSearchPatterns(filter);
        if (filter.isSetInterval()) {
            filterDto.setTimeFrom(filter.getInterval().getLowerBound().getBoundTime());
            filterDto.setTimeTo(filter.getInterval().getUpperBound().getBoundTime());
        }
        filterDto.setLastId(page.getContinuationId());
        filterDto.setSize((int) page.getSize());
        filterDto.setSearchPatterns(searchPatterns);
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
        // TODO payment_id
        return searchPatterns;
    }

    @NotNull
    private PaymentInfoResult buildResult(HistoricalPaymentsDto historicalPaymentsDto) {
        List<PaymentInfo> paymentsInfo = historicalPaymentsDto.getPayments().stream()
                .map(converter::convert)
                .collect(Collectors.toList());
        PaymentInfoResult paymentInfoResult = new PaymentInfoResult();
        paymentInfoResult.setContinuationId(historicalPaymentsDto.getLastId());
        paymentInfoResult.setPayments(paymentsInfo);
        return paymentInfoResult;
    }
}
