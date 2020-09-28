package com.rbkmoney.fraudbusters.repository.setter;

import com.rbkmoney.damsel.fraudbusters.FraudPayment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.util.StringUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class FraudPaymentBatchPreparedStatementSetter implements BatchPreparedStatementSetter {

    public static final String FORMAT = "yyyy-MM-dd[ HH:mm:ss]";
    private final List<FraudPayment> payments;

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        FraudPayment payment = payments.get(i);
        LocalDateTime date = LocalDateTime.now();
        if (!StringUtils.isEmpty(payment.getEventTime())) {
            date = LocalDateTime.parse(payment.getEventTime(), DateTimeFormatter.ofPattern(FORMAT));
        } else {
            log.warn("eventTIme not found for fraud operation: {}", payment);
        }
        int l = 1;
        ps.setObject(l++, date.toLocalDate());
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
