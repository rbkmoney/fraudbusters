package com.rbkmoney.fraudbusters.stream.aggregate;

import com.rbkmoney.fraudbusters.domain.MgEventSinkRow;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.Aggregator;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Date;

@Slf4j
@Service
public class MgEventAggregator implements Aggregator<String, MgEventSinkRow, MgEventSinkRow> {

    @Override
    public MgEventSinkRow apply(String key, MgEventSinkRow value, MgEventSinkRow aggregate) {
        log.debug("Merge aggValue={} and value={}", aggregate, value);
        aggregate.setInvoiceId(changeIfNotNull(aggregate.getInvoiceId(), value.getInvoiceId()));
        aggregate.setResultStatus(value.getResultStatus());
        aggregate.setPaymentId(changeIfNotNull(aggregate.getPaymentId(), value.getPaymentId()));
        aggregate.setEmail(changeIfNotNull(aggregate.getEmail(), value.getEmail()));
        aggregate.setFingerprint(changeIfNotNull(aggregate.getFingerprint(), value.getFingerprint()));
        aggregate.setIp(changeIfNotNull(aggregate.getIp(), value.getIp()));
        aggregate.setBankName(changeIfNotNull(aggregate.getBankName(), value.getBankName()));
        aggregate.setCardToken(changeIfNotNull(aggregate.getCardToken(), value.getCardToken()));
        aggregate.setMaskedPan(changeIfNotNull(aggregate.getMaskedPan(), value.getMaskedPan()));
        aggregate.setBin(changeIfNotNull(aggregate.getBin(), value.getBin()));
        aggregate.setBankCountry(changeIfNotNull(aggregate.getBankCountry(), value.getBankCountry()));
        aggregate.setPartyId(changeIfNotNull(aggregate.getPartyId(), value.getPartyId()));
        aggregate.setShopId(changeIfNotNull(aggregate.getShopId(), value.getShopId()));
        aggregate.setErrorCode(changeIfNotNull(aggregate.getErrorCode(), value.getErrorCode()));
        aggregate.setErrorMessage(changeIfNotNull(aggregate.getErrorMessage(), value.getErrorMessage()));
        aggregate.setCurrency(changeIfNotNull(aggregate.getCurrency(), value.getCurrency()));
        aggregate.setAmount(changeIfNotNull(aggregate.getAmount(), value.getAmount()));
        aggregate.setCountry(changeIfNotNull(aggregate.getCountry(), value.getCountry()));
        aggregate.setTimestamp(changeIfNotNull(aggregate.getTimestamp(), value.getTimestamp()));
        aggregate.setEventTime(changeIfNotNull(aggregate.getEventTime(), value.getEventTime()));
        log.debug("Merge result={}", aggregate);
        return aggregate;
    }

    private String changeIfNotNull(String value, String newValue) {
        return StringUtils.isEmpty(value) ? newValue : value;
    }

    private Long changeIfNotNull(Long value, Long newValue) {
        return value == null ? newValue : value;
    }

    private Date changeIfNotNull(Date value, Date newValue) {
        return value == null ? newValue : value;
    }

}
