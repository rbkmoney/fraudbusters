package com.rbkmoney.fraudbusters.stream.aggregate;

import com.rbkmoney.fraudbusters.stream.aggregate.handler.MgEventSinkHandler;
import com.rbkmoney.fraudbusters.domain.MgEventSinkRow;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MgEventSinkRowMapper implements KeyValueMapper<String, SinkEvent, KeyValue<String, MgEventSinkRow>> {

    private final MgEventSinkHandler mgEventSinkHandler;

    @Override
    public KeyValue<String, MgEventSinkRow> apply(String key, SinkEvent value) {
        return new KeyValue<>(key, mgEventSinkHandler.handle(value));
    }

}
