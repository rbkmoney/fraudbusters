package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.fraudbusters.domain.Event;
import com.rbkmoney.fraudbusters.domain.FraudResult;
import com.rbkmoney.fraudo.model.FraudModel;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class FraudResultToEventConverter implements Converter<FraudResult, Event> {

    @Override
    public Event convert(FraudResult fraudResult) {
        Event event = new Event();
        FraudModel fraudModel = fraudResult.getFraudModel();
        event.setAmount(fraudModel.getAmount());
        event.setBin(fraudModel.getBin());
        event.setEmail(fraudModel.getEmail());
        event.setEventTime(Instant.now().atZone(ZoneId.systemDefault()).withSecond(0)
                .withNano(0).toInstant().toEpochMilli());
        event.setTimestamp(new java.sql.Date(Calendar.getInstance().getTime().getTime()));
        event.setFingerprint(fraudModel.getFingerprint());
        event.setIp(fraudModel.getIp());
        event.setPartyId(fraudModel.getPartyId());
        event.setResultStatus(fraudResult.getResultModel().getResultStatus().name());
        event.setShopId(fraudModel.getShopId());
        return event;
    }

    public List<Event> convertBatch(List<FraudResult> fraudResults) {
        return fraudResults.stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }
}
