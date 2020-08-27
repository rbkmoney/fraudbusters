package com.rbkmoney.fraudbusters.repository.setter;

import com.rbkmoney.damsel.fraudbusters.FraudPayment;
import com.rbkmoney.fraudbusters.domain.TimeProperties;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
public class FraudPaymentBatchPreparedStatementSetter implements BatchPreparedStatementSetter {

    private final List<FraudPayment> payments;

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        FraudPayment payment = payments.get(i);
        TimeProperties timeProperties = TimestampUtil.generateTimeProperties();
        int l = 1;
        ps.setObject(l++, timeProperties.getTimestamp());
        ps.setString(l++, payment.getId());
        ps.setString(l++, payment.getEventTime());
        ps.setString(l++, payment.getType());
        ps.setString(l, payment.getComment());
    }

    @Override
    public int getBatchSize() {
        return payments.size();
    }

}
