package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.fraudbusters.domain.Event;
import com.rbkmoney.fraudbusters.domain.FraudResult;
import com.rbkmoney.fraudo.model.FraudModel;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class FraudResultToEventConverter implements Converter<FraudResult, Event> {

    @Override
    public Event convert(FraudResult fraudResult) {
        Event event = new Event();
        FraudModel fraudModel = fraudResult.getFraudRequest().getFraudModel();
        event.setAmount(fraudModel.getAmount());
        event.setBin(fraudModel.getBin());
        event.setEmail(fraudModel.getEmail());
        event.setEventTime(getEventTime(fraudResult));
        event.setTimestamp(java.sql.Date.valueOf(LocalDateTime.now().toLocalDate()));
        event.setFingerprint(fraudModel.getFingerprint());
        event.setIp(fraudModel.getIp());
        event.setPartyId(fraudModel.getPartyId());
        event.setResultStatus(fraudResult.getResultModel().getResultStatus().name());
        event.setShopId(fraudModel.getShopId());
        return event;
    }

    @NotNull
    private Long getEventTime(FraudResult fraudResult) {
        if (fraudResult.getFraudRequest().getMetadata() != null) {
            Long timestamp = fraudResult.getFraudRequest().getMetadata().getTimestamp();
            return timestamp != null ? timestamp : generateNow();
        }
        return generateNow();
    }

    private long generateNow() {
        return LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    }

    public List<Event> convertBatch(List<FraudResult> fraudResults) {
        return fraudResults.stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }
}
