package com.rbkmoney.fraudbusters.resource.payment.handler;

import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.fraudbusters.converter.FilterConverter;
import com.rbkmoney.fraudbusters.converter.HistoricalDataResponseConverter;
import com.rbkmoney.fraudbusters.converter.PaymentToPaymentModelConverter;
import com.rbkmoney.fraudbusters.exception.InvalidTemplateException;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.service.HistoricalDataService;
import com.rbkmoney.fraudbusters.service.RuleCheckingService;
import com.rbkmoney.fraudbusters.service.dto.*;
import com.rbkmoney.fraudbusters.util.HistoricalTransactionCheckFactory;
import com.rbkmoney.fraudo.model.ResultModel;
import lombok.RequiredArgsConstructor;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoricalDataHandler implements HistoricalDataServiceSrv.Iface {

    private final HistoricalDataService historicalDataService;
    private final RuleCheckingService ruleCheckingService;
    private final HistoricalDataResponseConverter resultConverter;
    private final FilterConverter filterConverter;
    private final PaymentToPaymentModelConverter paymentModelConverter;
    private final HistoricalTransactionCheckFactory historicalTransactionCheckFactory;

    private static final int INVALID_TEMPLATE_ERROR_CODE = 470;

    @Override
    public HistoricalDataResponse getPayments(Filter filter, Page page, Sort sort) {
        FilterDto filterDto = filterConverter.convert(filter, page, sort);
        HistoricalPaymentsDto historicalPaymentsDto = historicalDataService.getPayments(filterDto);
        return resultConverter.convertPayment(historicalPaymentsDto);
    }

    @Override
    public HistoricalDataResponse getFraudResults(Filter filter, Page page, Sort sort) {
        FilterDto filterDto = filterConverter.convert(filter, page, sort);
        HistoricalFraudResultsDto historicalFraudResultsDto = historicalDataService.getFraudResults(filterDto);
        return resultConverter.convertFraudResult(historicalFraudResultsDto);
    }

    @Override
    public HistoricalDataResponse getRefunds(Filter filter, Page page, Sort sort) {
        FilterDto filterDto = filterConverter.convert(filter, page, sort);
        HistoricalRefundsDto historicalRefundsDto = historicalDataService.getRefunds(filterDto);
        return resultConverter.convertRefund(historicalRefundsDto);
    }

    @Override
    public HistoricalDataResponse getChargebacks(Filter filter, Page page, Sort sort) {
        FilterDto filterDto = filterConverter.convert(filter, page, sort);
        HistoricalChargebacksDto historicalChargebacksDto = historicalDataService.getChargebacks(filterDto);
        return resultConverter.convertChargeback(historicalChargebacksDto);
    }

    @Override
    public HistoricalDataResponse getFraudPayments(Filter filter, Page page, Sort sort) {
        FilterDto filterDto = filterConverter.convert(filter, page, sort);
        HistoricalPaymentsDto historicalPaymentsDto = historicalDataService.getFraudPayments(filterDto);
        return resultConverter.convertFraudPayment(historicalPaymentsDto);
    }

    @Override
    public HistoricalDataSetCheckResult applyRuleOnHistoricalDataSet(
            EmulationRuleApplyRequest emulationRuleApplyRequest) throws HistoricalDataServiceException, TException {
        Set<HistoricalTransactionCheck> historicalTransactionChecks = null;
        try {
            if (emulationRuleApplyRequest.getEmulationRule().isSetTemplateEmulation()) {
                final String templateString = new String(
                        emulationRuleApplyRequest.getEmulationRule().getTemplateEmulation().getTemplate().getTemplate(),
                        StandardCharsets.UTF_8
                );
                final Map<String, PaymentModel> paymentModelMap = emulationRuleApplyRequest.getTransactions().stream()
                        .collect(Collectors.toMap(Payment::getId, paymentModelConverter::convert));
                final Map<String, ResultModel> resultMap =
                        ruleCheckingService.checkSingleRule(paymentModelMap, templateString);
                historicalTransactionChecks = emulationRuleApplyRequest.getTransactions().stream()
                        .map(transaction -> historicalTransactionCheckFactory.createHistoricalTransactionCheck(
                                transaction,
                                templateString,
                                resultMap.get(transaction.getId())
                        ))
                        .collect(Collectors.toSet());
            }
        } catch (InvalidTemplateException ex) {
            throw new HistoricalDataServiceException()
                    .setCode(INVALID_TEMPLATE_ERROR_CODE)
                    .setReason(ex.getMessage());
        }

        return new HistoricalDataSetCheckResult()
                .setHistoricalTransactionCheck(historicalTransactionChecks);
    }
}
