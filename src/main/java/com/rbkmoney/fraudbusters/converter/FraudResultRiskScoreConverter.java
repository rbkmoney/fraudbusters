package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.damsel.domain.RiskScore;
import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import com.rbkmoney.fraudbusters.domain.FraudResult;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class FraudResultRiskScoreConverter implements Converter<FraudResult, RiskScore> {

    @Override
    public RiskScore convert(FraudResult fraudResult) {
        CheckedResultModel checkedResultModel = fraudResult.getResultModel();
        switch (checkedResultModel.getResultModel().getResultStatus()) {
            case ACCEPT:
            case NOTIFY:
                return RiskScore.low;
            case DECLINE:
                return RiskScore.fatal;
            default:
                return RiskScore.high;
        }
    }

}
