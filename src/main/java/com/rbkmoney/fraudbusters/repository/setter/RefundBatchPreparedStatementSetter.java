package com.rbkmoney.fraudbusters.repository.setter;

import com.rbkmoney.damsel.fraudbusters.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
public class RefundBatchPreparedStatementSetter implements BatchPreparedStatementSetter {

    private final List<Payment> batch;

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        Payment event = batch.get(i);
        int l = 1;
        ps.setObject(l++, event.getTimestamp());
        ps.setLong(l++, event.getEventTimeHour());
        ps.setLong(l++, event.getEventTime());

        ps.setString(l++, event.getPartyId());
        ps.setString(l++, event.getShopId());

        ps.setString(l++, event.getEmail());
        ps.setString(l++, event.getProviderId());

        ps.setLong(l++, event.getAmount());
        ps.setString(l++, event.getCurrency());

        ps.setString(l++, event.getStatus());
        ps.setString(l++, event.getErrorCode());
        ps.setString(l++, event.getErrorReason());

        ps.setString(l++, event.getInvoiceId());
        ps.setString(l++, event.getPaymentId());

        ps.setString(l++, event.getIp());
        ps.setString(l++, event.getBin());
        ps.setString(l++, event.getMaskedPan());
        ps.setString(l++, event.getPaymentTool());
        ps.setString(l++, event.getFingerprint());
        ps.setString(l++, event.getCardToken());
        ps.setString(l++, event.getPaymentSystem());
        ps.setString(l++, event.getPaymentCountry());
        ps.setString(l++, event.getBankCountry());
        ps.setString(l, event.getTerminal());
    }

    @Override
    public int getBatchSize() {
        return batch.size();
    }
}