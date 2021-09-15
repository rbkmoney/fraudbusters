package com.rbkmoney.fraudbusters.repository.setter;

import com.rbkmoney.damsel.fraudbusters.Resource;
import com.rbkmoney.damsel.fraudbusters.Withdrawal;
import com.rbkmoney.fraudbusters.domain.TimeProperties;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import com.rbkmoney.mamsel.PaymentSystemUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.rbkmoney.fraudbusters.constant.ClickhouseUtilsValue.UNKNOWN;

@RequiredArgsConstructor
public class WithdrawalBatchPreparedStatementSetter implements BatchPreparedStatementSetter {

    public static final String FIELDS = """
            timestamp, eventTimeHour, eventTime, id, amount, currency, bin, maskedPan, cardToken, paymentSystem,
            terminal, providerId, bankCountry, identityId, accountId, accountCurrency, status, errorCode,
            errorReason""";

    public static final String FIELDS_MARK = "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?";

    private final List<Withdrawal> batch;

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        Withdrawal withdrawal = batch.get(i);
        int l = 1;
        TimeProperties timeProperties = TimestampUtil.generateTimePropertiesByString(withdrawal.getEventTime());
        ps.setObject(l++, timeProperties.getTimestamp());
        ps.setLong(l++, timeProperties.getEventTimeHour());
        ps.setLong(l++, timeProperties.getEventTime());

        ps.setString(l++, withdrawal.getId());

        ps.setLong(l++, withdrawal.getCost().getAmount());
        ps.setString(l++, withdrawal.getCost().getCurrency().getSymbolicCode());

        final Resource destinationResource = withdrawal.getDestinationResource();
        ps.setString(l++, destinationResource.isSetBankCard() ? destinationResource.getBankCard().getBin() : UNKNOWN);
        ps.setString(
                l++,
                destinationResource.isSetBankCard() ? destinationResource.getBankCard().getLastDigits() : UNKNOWN
        );
        ps.setString(l++, destinationResource.isSetBankCard() ? destinationResource.getBankCard().getToken() : UNKNOWN);
        ps.setString(l++, destinationResource.isSetBankCard()
                ? PaymentSystemUtil.getPaymentSystemName(destinationResource.getBankCard())
                : UNKNOWN
        );

        ps.setString(l++, withdrawal.isSetProviderInfo() ? withdrawal.getProviderInfo().getTerminalId() : UNKNOWN);
        ps.setString(l++, withdrawal.isSetProviderInfo() ? withdrawal.getProviderInfo().getProviderId() : UNKNOWN);
        ps.setString(l++,
                withdrawal.isSetProviderInfo() && withdrawal.getProviderInfo().isSetCountry()
                        ? withdrawal.getProviderInfo().getCountry()
                        : UNKNOWN
        );

        ps.setString(l++, withdrawal.getAccount().getIdentity());
        ps.setString(l++, withdrawal.getAccount().getId());
        ps.setString(l++, withdrawal.getAccount().getCurrency().getSymbolicCode());

        ps.setObject(l++, withdrawal.getStatus());
        ps.setObject(l++, withdrawal.isSetError() ? withdrawal.getError().getErrorCode() : null);
        ps.setObject(l, withdrawal.isSetError() ? withdrawal.getError().getErrorReason() : null);
    }

    @Override
    public int getBatchSize() {
        return batch.size();
    }
}
