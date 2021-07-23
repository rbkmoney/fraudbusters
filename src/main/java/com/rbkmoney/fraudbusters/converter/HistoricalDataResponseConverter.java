package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.damsel.fraudbusters.HistoricalData;
import com.rbkmoney.damsel.fraudbusters.HistoricalDataResponse;
import com.rbkmoney.damsel.fraudbusters.Payment;
import com.rbkmoney.fraudbusters.service.dto.HistoricalPaymentsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HistoricalDataResponseConverter implements Converter<HistoricalPaymentsDto, HistoricalDataResponse> {

    private final CheckedPaymentToPaymentConverter converter;

    @Override
    public HistoricalDataResponse convert(HistoricalPaymentsDto historicalPaymentsDto) {
        List<Payment> payments = historicalPaymentsDto.getPayments().stream()
                .map(converter::convert)
                .collect(Collectors.toList());
        HistoricalData historicalData = new HistoricalData();
        historicalData.setPayments(payments);
        return new HistoricalDataResponse()
                .setContinuationId(historicalPaymentsDto.getLastId())
                .setData(historicalData);
    }
}
