package com.rbkmoney.fraudbusters.repository.setter;

import com.rbkmoney.fraudbusters.constant.ClickhouseUtilsValue;
import com.rbkmoney.fraudbusters.domain.FraudPaymentRow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class FraudPaymentBatchPreparedStatementSetter implements BatchPreparedStatementSetter {

    private final List<FraudPaymentRow> payments;

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        FraudPaymentRow checkedPayment = payments.get(i);
        int l = 1;
        ps.setObject(l++, checkedPayment.getTimestamp());
        ps.setLong(l++, checkedPayment.getEventTimeHour());
        ps.setLong(l++, checkedPayment.getEventTime());
        ps.setString(l++, checkedPayment.getId());

        ps.setString(l++, checkedPayment.getType());
        ps.setString(l++, checkedPayment.getComment());

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

        ps.setLong(l++, checkedPayment.getAmount() != null ? checkedPayment.getAmount() : 0L);
        ps.setString(l++, checkedPayment.getCurrency() != null ? checkedPayment.getCurrency() :
                ClickhouseUtilsValue.UNKNOWN);

        ps.setObject(l++, checkedPayment.getPaymentStatus());
        ps.setObject(l++, checkedPayment.getErrorCode() != null ? checkedPayment.getErrorCode() :
                ClickhouseUtilsValue.UNKNOWN);
        ps.setObject(l++, checkedPayment.getErrorReason() != null ? checkedPayment.getErrorReason() :
                ClickhouseUtilsValue.UNKNOWN);

        ps.setObject(l, checkedPayment.getPaymentCountry());
    }

    @Override
    public int getBatchSize() {
        return payments.size();
    }

}
