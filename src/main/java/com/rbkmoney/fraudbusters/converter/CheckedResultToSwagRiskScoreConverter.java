package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import com.rbkmoney.swag.fraudbusters.model.RiskScore;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CheckedResultToSwagRiskScoreConverter implements Converter<CheckedResultModel, RiskScore> {

    @Override
    public RiskScore convert(CheckedResultModel checkedResultModel) {
        switch (checkedResultModel.getResultModel().getResultStatus()) {
            case ACCEPT:
            case ACCEPT_AND_NOTIFY:
            case NOTIFY:
                return RiskScore.LOW;
            case DECLINE:
            case DECLINE_AND_NOTIFY:
                return RiskScore.FATAL;
            default:
                return RiskScore.HIGH;
        }
    }

}
