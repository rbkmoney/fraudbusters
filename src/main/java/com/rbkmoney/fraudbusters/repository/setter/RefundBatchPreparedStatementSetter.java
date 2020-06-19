package com.rbkmoney.fraudbusters.repository.setter;

import com.rbkmoney.damsel.fraudbusters.Error;
import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.fraudbusters.constant.FraudPaymentTool;
import com.rbkmoney.fraudbusters.domain.TimeProperties;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
public class RefundBatchPreparedStatementSetter implements BatchPreparedStatementSetter {

    public final static String FIELDS = " timestamp, eventTimeHour, eventTime, " +
            "id, " +
            "email, ip, fingerprint, " +
            "bin, maskedPan, cardToken, paymentSystem, paymentTool , " +
            "terminal, providerId, bankCountry" +
            "partyId, shopId, " +
            "amount, currency, " +
            "status";

    public final static String FIELDS_MARK = "?";

    private final List<Refund> batch;

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        Refund event = batch.get(i);
        int l = 1;
        TimeProperties timeProperties = TimestampUtil.generateTimePropertiesByString(event.getEventTime());
        ps.setObject(l++, timeProperties.getTimestamp());
        ps.setLong(l++, timeProperties.getEventTimeHour());
        ps.setLong(l++, timeProperties.getEventTime());

        ps.setString(l++, event.getId());

        ClientInfo clientInfo = event.getClientInfo();
        ps.setString(l++, clientInfo.getEmail());
        ps.setString(l++, clientInfo.getIp());
        ps.setString(l++, clientInfo.getFingerprint());

        PaymentTool paymentTool = event.getPaymentTool();
        if (paymentTool.isSetBankCard()) {
            BankCard bankCard = paymentTool.getBankCard();
            ps.setString(l++, bankCard.getBin());
            ps.setString(l++, bankCard.getMaskedPan());
            ps.setString(l++, bankCard.getCardToken());
            ps.setString(l++, bankCard.getPaymentSystem());
            ps.setString(l++, FraudPaymentTool.BANK_CARD.name());
        }

        ProviderInfo providerInfo = event.getProviderInfo();
        ps.setString(l++, providerInfo.getTerminalId());
        ps.setString(l++, providerInfo.getProviderId());
        ps.setString(l++, providerInfo.getCountry());

        ReferenceInfo referenceInfo = event.getReferenceInfo();
        if (referenceInfo.isSetMerchantInfo()) {
            MerchantInfo merchantInfo = referenceInfo.getMerchantInfo();
            ps.setString(l++, merchantInfo.getPartyId());
            ps.setString(l++, merchantInfo.getShopId());
        }

        ps.setObject(l++, event.getStatus());

        Error error = event.getError();
        ps.setObject(l++, error.getErrorCode());
        ps.setString(l++, error.getErrorReason());

        ps.setLong(l++, event.getCost().getAmount());
        ps.setString(l, event.getCost().getCurrency().getSymbolicCode());
    }

    @Override
    public int getBatchSize() {
        return batch.size();
    }
}