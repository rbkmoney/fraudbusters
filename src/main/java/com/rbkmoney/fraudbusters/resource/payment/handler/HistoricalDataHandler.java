package com.rbkmoney.fraudbusters.resource.payment.handler;

import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.fraudbusters.converter.FilterConverter;
import com.rbkmoney.fraudbusters.converter.HistoricalDataResponseConverter;
import com.rbkmoney.fraudbusters.service.HistoricalDataService;
import com.rbkmoney.fraudbusters.service.dto.*;
import lombok.RequiredArgsConstructor;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HistoricalDataHandler implements HistoricalDataServiceSrv.Iface {

    private final HistoricalDataService historicalDataService;
    private final HistoricalDataResponseConverter resultConverter;
    private final FilterConverter filterConverter;

    @Override
    public HistoricalDataResponse getPayments(Filter filter, Page page, Sort sort) {
        FilterDto filterDto = filterConverter.convert(filter, page, sort);
        HistoricalPaymentsDto historicalPaymentsDto = historicalDataService.getPayments(filterDto);
        return resultConverter.convertPayment(historicalPaymentsDto);
    }

    @Override
    public HistoricalDataResponse getFraudResults(Filter filter, Page page, Sort sort) throws TException {
        FilterDto filterDto = filterConverter.convert(filter, page, sort);
        HistoricalFraudResultsDto historicalFraudResultsDto = historicalDataService.getFraudResults(filterDto);
        return resultConverter.convertFraudResult(historicalFraudResultsDto);
    }

    @Override
    public HistoricalDataResponse getRefunds(Filter filter, Page page, Sort sort) throws TException {
        FilterDto filterDto = filterConverter.convert(filter, page, sort);
        HistoricalRefundsDto historicalRefundsDto = historicalDataService.getRefunds(filterDto);
        return resultConverter.convertRefund(historicalRefundsDto);
    }

    @Override
    public HistoricalDataResponse getChargebacks(Filter filter, Page page, Sort sort) throws TException {
        FilterDto filterDto = filterConverter.convert(filter, page, sort);
        HistoricalChargebacksDto historicalChargebacksDto = historicalDataService.getChargebacks(filterDto);
        return resultConverter.convertChargeback(historicalChargebacksDto);
    }

    @Override
    public HistoricalDataResponse getFraudPayments(Filter filter, Page page, Sort sort) throws TException {
        FilterDto filterDto = filterConverter.convert(filter, page, sort);
        HistoricalPaymentsDto historicalPaymentsDto = historicalDataService.getFraudPayments(filterDto);
        return resultConverter.convertPayment(historicalPaymentsDto); // TODO fraudPaymentInfo
    }

    @Override
    public HistoricalDataSetCheckResult applyRuleOnHistoricalDataSet(
            EmulationRuleApplyRequest emulationRuleApplyRequest) throws HistoricalDataServiceException, TException {
        return null;
    }
}
