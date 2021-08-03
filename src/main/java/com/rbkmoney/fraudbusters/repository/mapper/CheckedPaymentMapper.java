package com.rbkmoney.fraudbusters.repository.mapper;

import com.rbkmoney.fraudbusters.constant.PaymentField;
import com.rbkmoney.fraudbusters.domain.CheckedPayment;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class CheckedPaymentMapper implements RowMapper<CheckedPayment> {

    @Override
    public CheckedPayment mapRow(ResultSet rs, int i) throws SQLException {
        CheckedPayment payment = new CheckedPayment();
        payment.setEventTime(rs.getLong(PaymentField.EVENT_TIME.getValue()));
        payment.setId(rs.getString(PaymentField.ID.getValue()));
        payment.setEmail(rs.getString(PaymentField.EMAIL.getValue()));
        payment.setIp(rs.getString(PaymentField.IP.getValue()));
        payment.setFingerprint(rs.getString(PaymentField.FINGERPRINT.getValue()));
        payment.setCardToken(rs.getString(PaymentField.CARD_TOKEN.getValue()));
        payment.setBin(rs.getString(PaymentField.BIN.getValue()));
        payment.setMaskedPan(rs.getString(PaymentField.MASKED_PAN.getValue()));
        payment.setPaymentSystem(rs.getString(PaymentField.PAYMENT_SYSTEM.getValue()));
        payment.setPaymentTool(rs.getString(PaymentField.PAYMENT_TOOL.getValue()));
        payment.setTerminal(rs.getString(PaymentField.TERMINAL.getValue()));
        payment.setProviderId(rs.getString(PaymentField.PROVIDER_ID.getValue()));
        payment.setBankCountry(rs.getString(PaymentField.BANK_COUNTRY.getValue()));
        payment.setPartyId(rs.getString(PaymentField.PARTY_ID.getValue()));
        payment.setShopId(rs.getString(PaymentField.SHOP_ID.getValue()));
        payment.setAmount(rs.getLong(PaymentField.AMOUNT.getValue()));
        payment.setCurrency(rs.getString(PaymentField.CURRENCY.getValue()));
        payment.setPaymentStatus(rs.getString(PaymentField.STATUS.getValue()));
        payment.setErrorCode(rs.getString(PaymentField.ERROR_CODE.getValue()));
        payment.setErrorReason(rs.getString(PaymentField.ERROR_REASON.getValue()));
        payment.setMobile(rs.getBoolean(PaymentField.MOBILE.getValue()));
        payment.setRecurrent(rs.getBoolean(PaymentField.RECURRENT.getValue()));
        return payment;
    }
}
