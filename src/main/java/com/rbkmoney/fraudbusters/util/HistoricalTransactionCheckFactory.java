package com.rbkmoney.fraudbusters.util;

import com.rbkmoney.damsel.fraudbusters.HistoricalTransactionCheck;
import com.rbkmoney.damsel.fraudbusters.Payment;
import com.rbkmoney.fraudo.model.ResultModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class HistoricalTransactionCheckFactory {

    private final CheckResultFactory checkResultFactory;

    public HistoricalTransactionCheck createHistoricalTransactionCheck(
            Payment payment,
            String templateString,
            ResultModel resultModel
    ) {
        return new HistoricalTransactionCheck()
                .setTransaction(payment)
                .setCheckResult(checkResultFactory.createCheckResult(templateString, resultModel));
    }

}
