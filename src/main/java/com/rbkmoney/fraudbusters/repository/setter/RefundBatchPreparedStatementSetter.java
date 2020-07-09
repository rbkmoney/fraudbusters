package com.rbkmoney.fraudbusters.repository.setter;

import com.rbkmoney.damsel.domain.PaymentTool;
import com.rbkmoney.damsel.fraudbusters.Error;
import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.fraudbusters.constant.PaymentToolType;
import com.rbkmoney.fraudbusters.domain.TimeProperties;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import com.rbkmoney.geck.common.util.TBaseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.rbkmoney.fraudbusters.constant.ClickhouseUtilsValue.UNKNOWN;

@RequiredArgsConstructor
public class RefundBatchPreparedStatementSetter implements BatchPreparedStatementSetter {

    public static final String FIELDS = " timestamp, eventTimeHour, eventTime, " +
            "id, " +
            "email, ip, fingerprint, " +
            "bin, maskedPan, cardToken, paymentSystem, paymentTool , " +
            "terminal, providerId, bankCountry, " +
            "partyId, shopId, " +
            "amount, currency, " +
            "status, errorCode, errorReason, paymentId, " +
            "payerType, tokenProvider";

    public static final String FIELDS_MARK = "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?";

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
        ps.setString(l++, TBaseUtil.unionFieldToEnum(paymentTool, PaymentToolType.class).name());

        ps.setString(l++, paymentTool.isSetBankCard() ? paymentTool.getBankCard().getBin() : UNKNOWN);
        ps.setString(l++, paymentTool.isSetBankCard() ? paymentTool.getBankCard().getLastDigits() : UNKNOWN);
        ps.setString(l++, paymentTool.isSetBankCard() ? paymentTool.getBankCard().getToken() : UNKNOWN);
        ps.setString(l++, paymentTool.isSetBankCard() ? paymentTool.getBankCard().getPaymentSystem().name() : UNKNOWN);

        ProviderInfo providerInfo = event.getProviderInfo();
        ps.setString(l++, providerInfo.getTerminalId());
        ps.setString(l++, providerInfo.getProviderId());
        ps.setString(l++, providerInfo.getCountry());

        ReferenceInfo referenceInfo = event.getReferenceInfo();
        MerchantInfo merchantInfo = event.getReferenceInfo().getMerchantInfo();
        ps.setString(l++, referenceInfo.isSetMerchantInfo() ? merchantInfo.getPartyId() : UNKNOWN);
        ps.setString(l++, referenceInfo.isSetMerchantInfo() ? merchantInfo.getShopId() : UNKNOWN);

        ps.setLong(l++, event.getCost().getAmount());
        ps.setString(l++, event.getCost().getCurrency().getSymbolicCode());

        ps.setObject(l++, event.getStatus());

        Error error = event.getError();
        ps.setString(l++, error == null ? null : error.getErrorCode());
        ps.setString(l++, error == null ? null : error.getErrorReason());
        ps.setString(l++, event.getPaymentId());

        ps.setString(l++, event.isSetPayerType() ? event.getPayerType().name() : UNKNOWN);
        ps.setString(l, paymentTool.isSetBankCard() && paymentTool.getBankCard().isSetTokenProvider() ?
                paymentTool.getBankCard().getTokenProvider().name() : UNKNOWN);
    }

    @Override
    public int getBatchSize() {
        return batch.size();
    }
}