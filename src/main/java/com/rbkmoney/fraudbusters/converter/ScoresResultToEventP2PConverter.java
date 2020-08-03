package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.fraudbusters.domain.EventP2P;
import com.rbkmoney.fraudbusters.domain.TimeProperties;
import com.rbkmoney.fraudbusters.fraud.model.P2PModel;
import com.rbkmoney.fraudbusters.fraud.model.Payer;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ScoresResultToEventP2PConverter implements Converter<P2PModel, EventP2P> {

    @Override
    public EventP2P convert(P2PModel source) {
        EventP2P eventP2P = new EventP2P();
        TimeProperties timeProperties = TimestampUtil.generateTimeProperties();
        eventP2P.setTimestamp(timeProperties.getTimestamp());
        eventP2P.setEventTime(timeProperties.getEventTime());
        eventP2P.setEventTimeHour(timeProperties.getEventTimeHour());

        eventP2P.setTransferId(source.getTransferId());
        eventP2P.setIdentityId(source.getIdentityId());

        eventP2P.setAmount(source.getAmount());
        eventP2P.setCurrency(source.getCurrency());

        eventP2P.setIp(source.getIp());
        eventP2P.setEmail(source.getEmail());
        Payer sender = source.getSender();
        eventP2P.setBin(sender.getBin());
        eventP2P.setFingerprint(source.getFingerprint());

        eventP2P.setBankCountry(sender.getBinCountryCode());
        eventP2P.setMaskedPan(sender.getPan());
        eventP2P.setBankName(sender.getBankName());
        eventP2P.setCardTokenFrom(sender.getCardToken());
        eventP2P.setCardTokenTo(source.getReceiver().getCardToken());
        return eventP2P;
    }

}
