package com.rbkmoney.fraudbusters.util;

import com.rbkmoney.damsel.fraudbusters.HistoricalTransactionCheck;
import com.rbkmoney.damsel.fraudbusters.Payment;
import com.rbkmoney.fraudbusters.converter.CheckedResultModelToCheckResultConverter;
import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class HistoricalTransactionCheckFactory {

    private final CheckedResultModelToCheckResultConverter checkResultConverter;

    public HistoricalTransactionCheck createHistoricalTransactionCheck(
            Payment payment,
            CheckedResultModel checkedResultModel
    ) {
        return new HistoricalTransactionCheck()
                .setTransaction(payment)
                .setCheckResult(checkResultConverter.convert(checkedResultModel));
    }

}
