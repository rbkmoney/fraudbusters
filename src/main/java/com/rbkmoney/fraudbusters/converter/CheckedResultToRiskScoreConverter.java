package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.damsel.domain.RiskScore;
import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CheckedResultToRiskScoreConverter implements Converter<CheckedResultModel, RiskScore> {

    @Override
    public RiskScore convert(CheckedResultModel checkedResultModel) {
        return switch (checkedResultModel.getResultModel().getResultStatus()) {
            case ACCEPT, ACCEPT_AND_NOTIFY, NOTIFY -> RiskScore.low;
            case DECLINE, DECLINE_AND_NOTIFY -> RiskScore.fatal;
            default -> RiskScore.high;
        };
    }

}
