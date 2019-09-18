package com.rbkmoney.fraudbusters.repository.setter;

import com.rbkmoney.fraudbusters.domain.MgEventSinkRow;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
public class MgEventSinkBatchPreparedStatementSetter implements BatchPreparedStatementSetter {

    public static final String INSERT = "INSERT INTO fraud.events_sink_mg " +
            "(timestamp, eventTime, ip, email, bin, fingerprint, shopId, partyId, resultStatus, errorCode, errorMessage,  amount, " +
            "country, bankCountry, currency, invoiceId, maskedPan, bankName, cardToken, paymentId)" +
            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private final List<MgEventSinkRow> batch;

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        MgEventSinkRow mgEventSinkRow = batch.get(i);
        int l = 1;
        ps.setDate(l++, mgEventSinkRow.getTimestamp());
        ps.setLong(l++, mgEventSinkRow.getEventTime());
        ps.setString(l++, mgEventSinkRow.getIp());
        ps.setString(l++, mgEventSinkRow.getEmail());
        ps.setString(l++, mgEventSinkRow.getBin());
        ps.setString(l++, mgEventSinkRow.getFingerprint());
        ps.setString(l++, mgEventSinkRow.getShopId());
        ps.setString(l++, mgEventSinkRow.getPartyId());
        ps.setString(l++, mgEventSinkRow.getResultStatus());
        ps.setString(l++, mgEventSinkRow.getErrorCode());
        ps.setString(l++, mgEventSinkRow.getErrorMessage());
        ps.setLong(l++, mgEventSinkRow.getAmount());
        ps.setString(l++, mgEventSinkRow.getCountry());
        ps.setString(l++, mgEventSinkRow.getBankCountry());
        ps.setString(l++, mgEventSinkRow.getCurrency());
        ps.setString(l++, mgEventSinkRow.getInvoiceId());
        ps.setString(l++, mgEventSinkRow.getMaskedPan());
        ps.setString(l++, mgEventSinkRow.getBankName());
        ps.setString(l++, mgEventSinkRow.getCardToken());
        ps.setString(l, mgEventSinkRow.getPaymentId());
    }

    @Override
    public int getBatchSize() {
        return batch.size();
    }
}
