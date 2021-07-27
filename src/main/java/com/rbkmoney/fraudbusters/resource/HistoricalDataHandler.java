package com.rbkmoney.fraudbusters.resource;

import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.fraudbusters.converter.FilterConverter;
import com.rbkmoney.fraudbusters.converter.PaymentInfoResultConverter;
import com.rbkmoney.fraudbusters.exception.InvalidTemplateException;
import com.rbkmoney.fraudbusters.service.HistoricalDataService;
import com.rbkmoney.fraudbusters.service.dto.FilterDto;
import com.rbkmoney.fraudbusters.service.dto.HistoricalPaymentsDto;
import lombok.RequiredArgsConstructor;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class HistoricalDataHandler implements HistoricalDataServiceSrv.Iface {

    private final HistoricalDataService historicalDataService;
    private final PaymentInfoResultConverter resultConverter;
    private final FilterConverter filterConverter;

    @Override
    public PaymentInfoResult getPayments(Filter filter, Page page, Sort sort) {
        FilterDto filterDto = filterConverter.convert(filter, page, sort);
        HistoricalPaymentsDto historicalPaymentsDto = historicalDataService.getPayments(filterDto);
        return resultConverter.convert(historicalPaymentsDto);
    }

    @Override
    public HistoricalDataSetCheckResult applyRuleOnHistoricalDataSet(
            EmulationRuleApplyRequest emulationRuleApplyRequest) throws TException {
        Set<HistoricalTransactionCheck> historicalTransactionChecks = null;
        try {
            if (emulationRuleApplyRequest.getEmulationRule().isSetTemplateEmulation()) {
                historicalTransactionChecks = historicalDataService.applySingleRule(
                        emulationRuleApplyRequest.getEmulationRule().getTemplateEmulation().getTemplate(),
                        emulationRuleApplyRequest.getTransactions()
                );
            }
        } catch (InvalidTemplateException ex) {
            throw new HistoricalDataServiceException().setReason(ex.getMessage());
        }

        return new HistoricalDataSetCheckResult()
                .setHistoricalTransactionCheck(historicalTransactionChecks);
    }

}
