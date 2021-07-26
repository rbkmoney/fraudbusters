package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.damsel.fraudbusters.HistoricalData;
import com.rbkmoney.damsel.fraudbusters.HistoricalDataResponse;
import com.rbkmoney.damsel.fraudbusters.Payment;
import com.rbkmoney.fraudbusters.service.dto.HistoricalPaymentsDto;
import com.rbkmoney.fraudbusters.service.dto.HistoricalRefundsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HistoricalDataResponseConverter {

    private final CheckedPaymentToPaymentConverter converter;

    public HistoricalDataResponse convertPayment(HistoricalPaymentsDto historicalPaymentsDto) {
        List<Payment> payments = historicalPaymentsDto.getPayments().stream()
                .map(converter::convert)
                .collect(Collectors.toList());
        HistoricalData historicalData = new HistoricalData();
        historicalData.setPayments(payments);
        return new HistoricalDataResponse()
                .setContinuationId(historicalPaymentsDto.getLastId())
                .setData(historicalData);
    }

    public HistoricalDataResponse convertRefund(HistoricalRefundsDto historicalRefundsDto) {
        HistoricalData historicalData = new HistoricalData();
        historicalData.setRefunds(historicalRefundsDto.getRefunds());
        return new HistoricalDataResponse()
                .setContinuationId(historicalRefundsDto.getLastId())
                .setData(historicalData);
    }
}
