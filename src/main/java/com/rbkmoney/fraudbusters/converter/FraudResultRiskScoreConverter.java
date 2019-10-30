package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.damsel.domain.RiskScore;
import com.rbkmoney.fraudbusters.domain.FraudResult;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FraudResultRiskScoreConverter implements Converter<FraudResult, RiskScore> {

    private final CheckedResultToRiskScoreConverter checkedResultToRiskScoreConverter;

    @Override
    public RiskScore convert(FraudResult fraudResult) {
        return checkedResultToRiskScoreConverter.convert(fraudResult.getResultModel());
    }

}
