package com.rbkmoney.fraudbusters.repository.setter;

import com.rbkmoney.fraudbusters.domain.FraudResult;
import com.rbkmoney.fraudo.model.FraudModel;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.List;

@RequiredArgsConstructor
public class FraudResultBatchPreparedStatementSetter implements BatchPreparedStatementSetter {

    private final List<FraudResult> batch;

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        FraudResult fraudResult = batch.get(i);
        FraudModel fraudModel = fraudResult.getFraudModel();
        ps.setDate(1, new java.sql.Date(Calendar.getInstance().getTime().getTime()));
        ps.setString(2, fraudModel.getIp());
        ps.setString(3, fraudModel.getEmail());
        ps.setString(4, fraudModel.getBin());
        ps.setString(5, fraudModel.getFingerprint());
        ps.setString(6, fraudModel.getShopId());
        ps.setString(7, fraudModel.getPartyId());
        ps.setString(8, fraudResult.getResultStatus().name());
        ps.setLong(9, Instant.now().atZone(ZoneId.systemDefault()).withSecond(0)
                .withNano(0).toInstant().toEpochMilli());
    }

    @Override
    public int getBatchSize() {
        return batch.size();
    }
}
