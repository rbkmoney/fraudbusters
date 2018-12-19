package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.damsel.domain.RiskScore;
import com.rbkmoney.fraudbusters.domain.FraudResult;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class FraudResultRiskScoreConverter implements Converter<FraudResult, RiskScore> {

    @Override
    public RiskScore convert(FraudResult fraudResult) {
        switch (fraudResult.getResultStatus()) {
            case ACCEPT:
                return RiskScore.low;
            case DECLINE:
                return RiskScore.fatal;
            case THREE_DS:
                return RiskScore.high;
            case NOTIFY:
                return RiskScore.low;
            default:
                return RiskScore.high;
        }
    }
}
