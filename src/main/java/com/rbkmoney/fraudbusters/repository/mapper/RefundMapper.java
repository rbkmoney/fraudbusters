package com.rbkmoney.fraudbusters.repository.mapper;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.fraudbusters.ClientInfo;
import com.rbkmoney.damsel.fraudbusters.Error;
import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.fraudbusters.constant.PaymentField;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class RefundMapper implements RowMapper<Refund> {

    @Override
    public Refund mapRow(ResultSet rs, int i) throws SQLException { // TODO надо будет добавить внутреннюю модель
        ReferenceInfo referenceInfo = new ReferenceInfo();
        referenceInfo.setMerchantInfo(
                new MerchantInfo()
                        .setPartyId(rs.getString(PaymentField.PARTY_ID.getValue()))
                        .setShopId(rs.getString(PaymentField.SHOP_ID.getValue())));
        PaymentTool paymentTool = new PaymentTool();
        BankCard bankCard = new BankCard();
        paymentTool.setBankCard(bankCard);
        bankCard.setToken(rs.getString(PaymentField.CARD_TOKEN.getValue()));
        bankCard.setBin(rs.getString(PaymentField.BIN.getValue()));
        bankCard.setLastDigits(rs.getString(PaymentField.MASKED_PAN.getValue()));
        bankCard.setPaymentSystem(new PaymentSystemRef().setId(rs.getString(PaymentField.PAYMENT_SYSTEM.getValue())));
        return new Refund()
                .setId(rs.getString(PaymentField.ID.getValue()))
                .setPaymentId(rs.getString(PaymentField.PAYMENT_ID.getValue()))
                .setEventTime(TimestampUtil.getStringDate(rs.getLong(PaymentField.EVENT_TIME.getValue())))
                .setClientInfo(new ClientInfo()
                        .setFingerprint(rs.getString(PaymentField.FINGERPRINT.getValue()))
                        .setIp(rs.getString(PaymentField.IP.getValue()))
                        .setEmail(rs.getString(PaymentField.EMAIL.getValue())))
                .setReferenceInfo(referenceInfo)
                .setError(new Error()
                        .setErrorCode(rs.getString(PaymentField.ERROR_CODE.getValue()))
                        .setErrorReason(rs.getString(PaymentField.ERROR_REASON.getValue())))
                .setCost(new Cash()
                        .setAmount(rs.getLong(PaymentField.AMOUNT.getValue()))
                        .setCurrency(new CurrencyRef()
                                .setSymbolicCode(rs.getString(PaymentField.CURRENCY.getValue()))))
                .setStatus(RefundStatus.valueOf(rs.getString(PaymentField.STATUS.getValue())))
                .setPaymentTool(paymentTool)
                .setProviderInfo(new ProviderInfo()
                        .setProviderId(rs.getString(PaymentField.PROVIDER_ID.getValue()))
                        .setCountry(rs.getString(PaymentField.BANK_COUNTRY.getValue()))
                        .setTerminalId(rs.getString(PaymentField.TERMINAL.getValue())));
    }
}
