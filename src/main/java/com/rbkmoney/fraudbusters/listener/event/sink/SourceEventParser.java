package com.rbkmoney.fraudbusters.listener.event.sink;

import com.rbkmoney.damsel.payment_processing.EventPayload;
import com.rbkmoney.fraudbusters.converter.BinaryConverter;
import com.rbkmoney.fraudbusters.exception.ParseException;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SourceEventParser {

    private final BinaryConverter<EventPayload> converter;

    public EventPayload parseEvent(SinkEvent message) {
        try {
            byte[] bin = message.getEvent().getData().getBin();
            return converter.convert(bin, EventPayload.class);
        } catch (Exception e) {
            log.error("Exception when parse message e: ", e);
            throw new ParseException();
        }
    }
}
