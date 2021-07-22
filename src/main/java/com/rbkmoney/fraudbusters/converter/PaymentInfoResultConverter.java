package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.damsel.fraudbusters.PaymentInfo;
import com.rbkmoney.damsel.fraudbusters.PaymentInfoResult;
import com.rbkmoney.fraudbusters.service.dto.HistoricalPaymentsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PaymentInfoResultConverter implements Converter<HistoricalPaymentsDto, PaymentInfoResult> {

    private final CheckedPaymentToPaymentInfoConverter converter;

    @Override
    public PaymentInfoResult convert(HistoricalPaymentsDto historicalPaymentsDto) {
        List<PaymentInfo> paymentsInfo = historicalPaymentsDto.getPayments().stream()
                .map(converter::convert)
                .collect(Collectors.toList());
        return new PaymentInfoResult()
                .setContinuationId(historicalPaymentsDto.getLastId())
                .setPayments(paymentsInfo);
    }
}
