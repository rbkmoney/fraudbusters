package com.rbkmoney.fraudbusters.repository.setter;

import com.rbkmoney.fraudbusters.domain.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
public class EventBatchPreparedStatementSetter implements BatchPreparedStatementSetter {

    private final List<Event> batch;

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        Event event = batch.get(i);
        int l = 1;
        ps.setDate(l++, event.getTimestamp());
        ps.setString(l++, event.getIp());
        ps.setString(l++, event.getEmail());
        ps.setString(l++, event.getBin());
        ps.setString(l++, event.getFingerprint());
        ps.setString(l++, event.getShopId());
        ps.setString(l++, event.getPartyId());
        ps.setString(l++, event.getResultStatus());
        ps.setLong(l++, event.getAmount());
        ps.setLong(l++, event.getEventTime());
        ps.setString(l++, event.getCountry());
    }

    @Override
    public int getBatchSize() {
        return batch.size();
    }
}
