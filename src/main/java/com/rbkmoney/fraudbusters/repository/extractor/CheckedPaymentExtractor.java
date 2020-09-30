package com.rbkmoney.fraudbusters.repository.extractor;

import com.rbkmoney.fraudbusters.domain.CheckedPayment;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class CheckedPaymentExtractor implements ResultSetExtractor<CheckedPayment> {
    @Override
    public CheckedPayment extractData(ResultSet rs) throws SQLException, DataAccessException {
        if (rs.next()) {
            CheckedPayment payment = new CheckedPayment();
            payment.setTimestamp(LocalDate.parse(rs.getDate("timestamp").toString()));
            payment.setEventTimeHour(rs.getLong("eventTimeHour"));
            payment.setEventTime(rs.getLong("eventTime"));
            payment.setId(rs.getString("id"));
            payment.setEmail(rs.getString("email"));
            payment.setIp(rs.getString("ip"));
            payment.setFingerprint(rs.getString("fingerprint"));
            payment.setBin(rs.getString("bin"));
            payment.setMaskedPan(rs.getString("maskedPan"));
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
            payment.setPayerType(rs.getString("payerType"));
            payment.setTokenProvider(rs.getString("tokenProvider"));
            payment.setCheckedTemplate(rs.getString("checkedTemplate"));
            payment.setCheckedRule(rs.getString("checkedRule"));
            payment.setResultStatus(rs.getString("resultStatus"));
            payment.setCheckedResultsJson(rs.getString("checkedResultsJson"));
            payment.setMobile(rs.getBoolean("mobile"));
            payment.setRecurrent(rs.getBoolean("recurrent"));
            return payment;
        }
        return null;
    }
}

