package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.fraudbusters.domain.EventP2P;
import com.rbkmoney.fraudbusters.fraud.model.P2PModel;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ScoresResultToEventP2PConverter implements Converter<P2PModel, EventP2P> {

    @Override
    public EventP2P convert(P2PModel source) {
        return null;
    }

}
