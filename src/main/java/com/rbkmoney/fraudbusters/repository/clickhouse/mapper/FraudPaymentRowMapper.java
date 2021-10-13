package com.rbkmoney.fraudbusters.repository.clickhouse.mapper;

import com.rbkmoney.fraudbusters.constant.FraudPaymentField;
import com.rbkmoney.fraudbusters.constant.PaymentField;
import com.rbkmoney.fraudbusters.domain.FraudPaymentRow;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FraudPaymentRowMapper implements RowMapper<FraudPaymentRow> {

    @Override
    public FraudPaymentRow mapRow(ResultSet rs, int i) throws SQLException {
        FraudPaymentRow fraudPaymentRow = new FraudPaymentRow();
        fraudPaymentRow.setComment(rs.getString(FraudPaymentField.COMMENT.getValue()));
        fraudPaymentRow.setType(rs.getString(FraudPaymentField.FRAUD_TYPE.getValue()));
        fraudPaymentRow.setEventTime(rs.getLong(PaymentField.EVENT_TIME.getValue()));
        fraudPaymentRow.setId(rs.getString(PaymentField.ID.getValue()));
        fraudPaymentRow.setEmail(rs.getString(PaymentField.EMAIL.getValue()));
        fraudPaymentRow.setIp(rs.getString(PaymentField.IP.getValue()));
        fraudPaymentRow.setFingerprint(rs.getString(PaymentField.FINGERPRINT.getValue()));
        fraudPaymentRow.setCardToken(rs.getString(PaymentField.CARD_TOKEN.getValue()));
        fraudPaymentRow.setBin(rs.getString(PaymentField.BIN.getValue()));
        fraudPaymentRow.setMaskedPan(rs.getString(PaymentField.MASKED_PAN.getValue()));
        fraudPaymentRow.setPaymentSystem(rs.getString(PaymentField.PAYMENT_SYSTEM.getValue()));
        fraudPaymentRow.setPaymentTool(rs.getString(PaymentField.PAYMENT_TOOL.getValue()));
        fraudPaymentRow.setTerminal(rs.getString(PaymentField.TERMINAL.getValue()));
        fraudPaymentRow.setProviderId(rs.getString(PaymentField.PROVIDER_ID.getValue()));
        fraudPaymentRow.setBankCountry(rs.getString(PaymentField.BANK_COUNTRY.getValue()));
        fraudPaymentRow.setPartyId(rs.getString(PaymentField.PARTY_ID.getValue()));
        fraudPaymentRow.setShopId(rs.getString(PaymentField.SHOP_ID.getValue()));
        fraudPaymentRow.setAmount(rs.getLong(PaymentField.AMOUNT.getValue()));
        fraudPaymentRow.setCurrency(rs.getString(PaymentField.CURRENCY.getValue()));
        fraudPaymentRow.setPaymentStatus(rs.getString(PaymentField.STATUS.getValue()));
        fraudPaymentRow.setErrorCode(rs.getString(PaymentField.ERROR_CODE.getValue()));
        fraudPaymentRow.setErrorReason(rs.getString(PaymentField.ERROR_REASON.getValue()));
        return fraudPaymentRow;
    }
}
