package com.rbkmoney.fraudbusters.repository.mapper;

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
        payment.setEventTime(rs.getLong("eventTime"));
        payment.setId(rs.getString("id"));
        payment.setEmail(rs.getString("email"));
        payment.setIp(rs.getString("ip"));
        payment.setFingerprint(rs.getString("fingerprint"));
        payment.setCardToken(rs.getString("cardToken"));
        payment.setPaymentSystem(rs.getString("paymentSystem"));
        payment.setPaymentTool(rs.getString("paymentTool"));
        payment.setTerminal(rs.getString("terminal"));
        payment.setProviderId(rs.getString("providerId"));
        payment.setBankCountry(rs.getString("bankCountry"));
        payment.setPartyId(rs.getString("partyId"));
        payment.setShopId(rs.getString("shopId"));
        payment.setAmount(rs.getLong("amount"));
        payment.setCurrency(rs.getString("currency"));
        payment.setPaymentStatus(rs.getString("status"));
        payment.setErrorCode(rs.getString("errorCode"));
        payment.setErrorReason(rs.getString("errorReason"));
        payment.setTokenProvider(rs.getString("tokenProvider"));
        return payment;
    }
}
