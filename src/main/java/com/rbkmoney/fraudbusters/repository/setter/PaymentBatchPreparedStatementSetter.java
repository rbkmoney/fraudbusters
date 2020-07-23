package com.rbkmoney.fraudbusters.repository.setter;

import com.rbkmoney.fraudbusters.domain.CheckedPayment;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
public class PaymentBatchPreparedStatementSetter implements BatchPreparedStatementSetter {

    public static final String FIELDS = " timestamp, eventTimeHour, eventTime, " +
            "id, " +
            "email, ip, fingerprint, " +
            "bin, maskedPan, cardToken, paymentSystem, paymentTool, " +
            "terminal, providerId, bankCountry, " +
            "partyId, shopId, " +
            "amount, currency, " +
            "status, errorCode, errorReason, " +
            "payerType, tokenProvider";

    public static final String FIELDS_MARK = "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?";

    private final List<CheckedPayment> batch;

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        CheckedPayment checkedPayment = batch.get(i);
        int l = 1;
        ps.setObject(l++, checkedPayment.getTimestamp());
        ps.setLong(l++, checkedPayment.getEventTimeHour());
        ps.setLong(l++, checkedPayment.getEventTime());
        ps.setString(l++, checkedPayment.getId());
        ps.setString(l++, checkedPayment.getEmail());
        ps.setString(l++, checkedPayment.getIp());
        ps.setString(l++, checkedPayment.getFingerprint());

        ps.setString(l++, checkedPayment.getBin());
        ps.setString(l++, checkedPayment.getMaskedPan());
        ps.setString(l++, checkedPayment.getCardToken());
        ps.setString(l++, checkedPayment.getPaymentSystem());
        ps.setString(l++, checkedPayment.getPaymentTool());

        ps.setString(l++, checkedPayment.getTerminal());
        ps.setString(l++, checkedPayment.getProviderId());
        ps.setString(l++, checkedPayment.getBankCountry());

        ps.setString(l++, checkedPayment.getPartyId());
        ps.setString(l++, checkedPayment.getShopId());

        ps.setLong(l++, checkedPayment.getAmount());
        ps.setString(l++, checkedPayment.getCurrency());

        ps.setObject(l++, checkedPayment.getPaymentStatus());
        ps.setString(l++, checkedPayment.getErrorCode());
        ps.setString(l++, checkedPayment.getErrorReason());

        ps.setString(l++, checkedPayment.getPayerType());
        ps.setString(l, checkedPayment.getTokenProvider());
    }

    @Override
    public int getBatchSize() {
        return batch.size();
    }
}