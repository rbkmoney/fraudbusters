package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.fraudbusters.service.dto.HistoricalChargebacksDto;
import com.rbkmoney.fraudbusters.service.dto.HistoricalFraudResultsDto;
import com.rbkmoney.fraudbusters.service.dto.HistoricalPaymentsDto;
import com.rbkmoney.fraudbusters.service.dto.HistoricalRefundsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HistoricalDataResponseConverter {

    private final CheckedPaymentToPaymentConverter paymentConverter;
    private final EventToHistoricalTransactionCheckConverter transactionCheckConverter;
    private final CheckedPaymentToFraudPaymentInfoConverter fraudPaymentInfoConverter;

    public HistoricalDataResponse convertPayment(HistoricalPaymentsDto historicalPaymentsDto) {
        List<Payment> payments = historicalPaymentsDto.getPayments().stream()
                .map(paymentConverter::convert)
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

    public HistoricalDataResponse convertChargeback(HistoricalChargebacksDto historicalChargebacksDto) {
        HistoricalData historicalData = new HistoricalData();
        historicalData.setChargebacks(historicalChargebacksDto.getChargebacks());
        return new HistoricalDataResponse()
                .setContinuationId(historicalChargebacksDto.getLastId())
                .setData(historicalData);
    }

    public HistoricalDataResponse convertFraudResult(HistoricalFraudResultsDto historicalFraudResultsDto) {
        List<HistoricalTransactionCheck> transactionChecks = historicalFraudResultsDto.getFraudResults().stream()
                .map(transactionCheckConverter::convert)
                .collect(Collectors.toList());
        HistoricalData historicalData = new HistoricalData();
        historicalData.setFraudResults(transactionChecks);
        return new HistoricalDataResponse()
                .setContinuationId(historicalFraudResultsDto.getLastId())
                .setData(historicalData);
    }

    public HistoricalDataResponse convertFraudPayment(HistoricalPaymentsDto historicalPaymentsDto) {
        List<FraudPaymentInfo> payments = historicalPaymentsDto.getPayments().stream()
                .map(fraudPaymentInfoConverter::convert)
                .collect(Collectors.toList());
        HistoricalData historicalData = new HistoricalData();
        historicalData.setFraudPayments(payments);
        return new HistoricalDataResponse()
                .setContinuationId(historicalPaymentsDto.getLastId())
                .setData(historicalData);
    }
}
