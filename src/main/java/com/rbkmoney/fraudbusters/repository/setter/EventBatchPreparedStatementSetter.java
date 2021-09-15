package com.rbkmoney.fraudbusters.repository.setter;

import com.rbkmoney.fraudbusters.domain.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
public class EventBatchPreparedStatementSetter implements BatchPreparedStatementSetter {

    public static final String INSERT = """
            INSERT INTO fraud.events_unique
             (timestamp, eventTimeHour, eventTime, ip, email, bin, fingerprint, shopId, partyId, resultStatus, amount,
             country, checkedRule, bankCountry, currency, invoiceId, maskedPan, bankName, cardToken,
             paymentId, checkedTemplate, payerType, tokenProvider, mobile, recurrent)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""";

    private final List<Event> batch;

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        Event event = batch.get(i);
        int l = 1;
        ps.setObject(l++, event.getTimestamp());
        ps.setLong(l++, event.getEventTimeHour());
        ps.setLong(l++, event.getEventTime());

        ps.setString(l++, event.getIp());
        ps.setString(l++, event.getEmail());
        ps.setString(l++, event.getBin());
        ps.setString(l++, event.getFingerprint());
        ps.setString(l++, event.getShopId());
        ps.setString(l++, event.getPartyId());
        ps.setString(l++, event.getResultStatus());
        ps.setLong(l++, event.getAmount());
        ps.setString(l++, event.getCountry());
        ps.setString(l++, event.getCheckedRule());
        ps.setString(l++, event.getBankCountry());
        ps.setString(l++, event.getCurrency());
        ps.setString(l++, event.getInvoiceId());
        ps.setString(l++, event.getMaskedPan());
        ps.setString(l++, event.getBankName());
        ps.setString(l++, event.getCardToken());
        ps.setString(l++, event.getPaymentId());
        ps.setString(l++, event.getCheckedTemplate());
        ps.setString(l++, event.getPayerType());
        ps.setString(l++, event.getTokenProvider());
        ps.setObject(l++, event.isMobile());
        ps.setObject(l, event.isRecurrent());
    }

    @Override
    public int getBatchSize() {
        return batch.size();
    }
}
