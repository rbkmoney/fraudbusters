package com.rbkmoney.fraudbusters.resource;

import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.fraudbusters.converter.CheckedPaymentToPaymentInfoConverter;
import com.rbkmoney.fraudbusters.converter.FilterConverter;
import com.rbkmoney.fraudbusters.service.HistoricalDataService;
import com.rbkmoney.fraudbusters.service.dto.FilterDto;
import com.rbkmoney.fraudbusters.service.dto.HistoricalPaymentsDto;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoricalDataHandler implements HistoricalDataServiceSrv.Iface {

    private final HistoricalDataService historicalDataService;
    private final CheckedPaymentToPaymentInfoConverter converter;
    private final FilterConverter filterConverter;

    @Override
    public PaymentInfoResult getPayments(Filter filter, Page page) {
        FilterDto filterDto = filterConverter.convert(filter, page);
        HistoricalPaymentsDto historicalPaymentsDto = historicalDataService.getPayments(filterDto);
        return buildResult(historicalPaymentsDto);
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
