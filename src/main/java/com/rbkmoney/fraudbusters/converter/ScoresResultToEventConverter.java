package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.fraudbusters.constant.ScoresType;
import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import com.rbkmoney.fraudbusters.domain.EventP2P;
import com.rbkmoney.fraudbusters.domain.ScoresResult;
import com.rbkmoney.fraudbusters.fraud.model.P2PModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScoresResultToEventConverter implements BatchConverter<ScoresResult<P2PModel>, EventP2P> {

    private final Converter<P2PModel, EventP2P> eventP2PConverter;

    @Override
    public EventP2P convert(ScoresResult<P2PModel> scoresResult) {
        Map<String, CheckedResultModel> scores = scoresResult.getScores();
        CheckedResultModel resultModel = scores.get(ScoresType.FRAUD);

        EventP2P event = eventP2PConverter.convert(scoresResult.getRequest());

        event.setCheckedTemplate(resultModel.getCheckedTemplate());
        Optional.ofNullable(resultModel.getResultModel())
                .ifPresent(result -> {
                    event.setCheckedRule(result.getRuleChecked());
                    event.setResultStatus(result.getResultStatus().name());
                });
        return event;
    }

}
