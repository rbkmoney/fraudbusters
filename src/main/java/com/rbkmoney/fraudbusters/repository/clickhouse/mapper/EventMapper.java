package com.rbkmoney.fraudbusters.repository.clickhouse.mapper;

import com.rbkmoney.fraudbusters.constant.FraudResultField;
import com.rbkmoney.fraudbusters.constant.PaymentField;
import com.rbkmoney.fraudbusters.domain.Event;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EventMapper implements RowMapper<Event> {

    @Override
    public Event mapRow(ResultSet rs, int i) throws SQLException {
        Event event = new Event();
        event.setEventTime(rs.getLong(PaymentField.EVENT_TIME.getValue()));
        event.setPaymentId(rs.getString(PaymentField.ID.getValue()));
        event.setInvoiceId(rs.getString(PaymentField.INVOICE_ID.getValue()));
        event.setEmail(rs.getString(PaymentField.EMAIL.getValue()));
        event.setIp(rs.getString(PaymentField.IP.getValue()));
        event.setFingerprint(rs.getString(PaymentField.FINGERPRINT.getValue()));
        event.setCardToken(rs.getString(PaymentField.CARD_TOKEN.getValue()));
        event.setBankCountry(rs.getString(PaymentField.BANK_COUNTRY.getValue()));
        event.setPartyId(rs.getString(PaymentField.PARTY_ID.getValue()));
        event.setShopId(rs.getString(PaymentField.SHOP_ID.getValue()));
        event.setAmount(rs.getLong(PaymentField.AMOUNT.getValue()));
        event.setCurrency(rs.getString(PaymentField.CURRENCY.getValue()));
        event.setMaskedPan(rs.getString(PaymentField.MASKED_PAN.getValue()));
        event.setBin(rs.getString(PaymentField.BIN.getValue()));
        event.setBankName(rs.getString(PaymentField.BANK_NAME.getValue()));
        event.setResultStatus(rs.getString(FraudResultField.RESULT_STATUS.getValue()));
        event.setCheckedRule(rs.getString(FraudResultField.CHECKED_RULE.getValue()));
        event.setCheckedTemplate(rs.getString(FraudResultField.CHECKED_TEMPLATE.getValue()));
        event.setMobile(rs.getBoolean(PaymentField.MOBILE.getValue()));
        event.setRecurrent(rs.getBoolean(PaymentField.RECURRENT.getValue()));
        return event;
    }
}
