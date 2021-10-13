package com.rbkmoney.fraudbusters.repository.clickhouse.setter;

import com.rbkmoney.damsel.domain.PaymentTool;
import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.fraudbusters.constant.PaymentToolType;
import com.rbkmoney.fraudbusters.domain.TimeProperties;
import com.rbkmoney.fraudbusters.util.PaymentTypeByContextResolver;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.mamsel.PaymentSystemUtil;
import com.rbkmoney.mamsel.TokenProviderUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.rbkmoney.fraudbusters.constant.ClickhouseUtilsValue.UNKNOWN;

@RequiredArgsConstructor
public class ChargebackBatchPreparedStatementSetter implements BatchPreparedStatementSetter {

    public static final String FIELDS = """
            timestamp, eventTimeHour, eventTime,
            id,
            email, ip, fingerprint,
            bin, maskedPan, cardToken, paymentSystem, paymentTool ,
            terminal, providerId, bankCountry,
            partyId, shopId,
            amount, currency,
            status, category, chargebackCode, paymentId,
            payerType, tokenProvider
            """;

    public static final String FIELDS_MARK = "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?";

    private final List<Chargeback> batch;
    private final PaymentTypeByContextResolver paymentTypeByContextResolver;

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        Chargeback event = batch.get(i);
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

        ps.setString(l++, paymentTool.isSetBankCard() ? paymentTool.getBankCard().getBin() : UNKNOWN);
        ps.setString(l++, paymentTool.isSetBankCard() ? paymentTool.getBankCard().getLastDigits() : UNKNOWN);
        ps.setString(l++, paymentTool.isSetBankCard() ? paymentTool.getBankCard().getToken() : UNKNOWN);
        ps.setString(l++, paymentTool.isSetBankCard()
                ? PaymentSystemUtil.getPaymentSystemName(paymentTool.getBankCard())
                : UNKNOWN);
        ps.setString(l++, TBaseUtil.unionFieldToEnum(paymentTool, PaymentToolType.class).name());

        ProviderInfo providerInfo = event.getProviderInfo();
        ps.setString(
                l++,
                providerInfo != null && providerInfo.isSetTerminalId() ? providerInfo.getTerminalId() : UNKNOWN
        );
        ps.setString(
                l++,
                providerInfo != null && providerInfo.isSetProviderId() ? providerInfo.getProviderId() : UNKNOWN
        );
        ps.setString(l++, providerInfo != null && providerInfo.isSetCountry() ? providerInfo.getCountry() : UNKNOWN);

        ReferenceInfo referenceInfo = event.getReferenceInfo();
        MerchantInfo merchantInfo = event.getReferenceInfo().getMerchantInfo();
        ps.setString(l++, referenceInfo.isSetMerchantInfo() ? merchantInfo.getPartyId() : UNKNOWN);
        ps.setString(l++, referenceInfo.isSetMerchantInfo() ? merchantInfo.getShopId() : UNKNOWN);

        ps.setLong(l++, event.getCost().getAmount());
        ps.setString(l++, event.getCost().getCurrency().getSymbolicCode());

        ps.setObject(l++, event.getStatus());

        ps.setObject(l++, event.getCategory());
        ps.setString(l++, event.getChargebackCode());

        ps.setString(l++, event.getPaymentId());

        ps.setString(l++, event.isSetPayerType() ? event.getPayerType().name() : UNKNOWN);
        ps.setString(l, paymentTool.isSetBankCard() && paymentTypeByContextResolver.isMobile(paymentTool.getBankCard())
                ? TokenProviderUtil.getTokenProviderName(paymentTool.getBankCard())
                : UNKNOWN
        );
    }

    @Override
    public int getBatchSize() {
        return batch.size();
    }
}
