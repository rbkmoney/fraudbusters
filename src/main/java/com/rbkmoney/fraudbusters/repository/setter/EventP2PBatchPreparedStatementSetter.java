package com.rbkmoney.fraudbusters.repository.setter;

import com.rbkmoney.fraudbusters.domain.EventP2P;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
public class EventP2PBatchPreparedStatementSetter implements BatchPreparedStatementSetter {

    private final List<EventP2P> batch;

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        EventP2P event = batch.get(i);
        int l = 1;
        ps.setDate(l++, event.getTimestamp());
        ps.setLong(l++, event.getEventTime());

        ps.setString(l++, event.getIdentityId());
        ps.setString(l++, event.getTransferId());

        ps.setString(l++, event.getIp());
        ps.setString(l++, event.getEmail());
        ps.setString(l++, event.getBin());
        ps.setString(l++, event.getFingerprint());

        ps.setLong(l++, event.getAmount());
        ps.setString(l++, event.getCurrency());

        ps.setString(l++, event.getCountry());
        ps.setString(l++, event.getBankCountry());
        ps.setString(l++, event.getMaskedPan());
        ps.setString(l++, event.getBankName());
        ps.setString(l++, event.getCardTokenFrom());
        ps.setString(l++, event.getCardTokenTo());

        ps.setString(l++, event.getResultStatus());
        ps.setString(l++, event.getCheckedRule());
        ps.setString(l, event.getCheckedTemplate());
    }

    @Override
    public int getBatchSize() {
        return batch.size();
    }
}
