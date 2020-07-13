package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.damsel.domain.RiskScore;
import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CheckedResultToRiskScoreConverter implements Converter<CheckedResultModel, RiskScore> {

    @Override
    public RiskScore convert(CheckedResultModel checkedResultModel) {
        switch (checkedResultModel.getResultModel().getResultStatus()) {
            case ACCEPT:
            case ACCEPT_AND_NOTIFY:
            case NOTIFY:
                return RiskScore.low;
            case DECLINE:
            case DECLINE_AND_NOTIFY:
                return RiskScore.fatal;
            default:
                return RiskScore.high;
        }
    }

}
