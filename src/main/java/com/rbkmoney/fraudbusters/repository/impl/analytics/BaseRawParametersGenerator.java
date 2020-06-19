package com.rbkmoney.fraudbusters.repository.impl.analytics;

import com.rbkmoney.fraudbusters.constant.field.BaseField;
import com.rbkmoney.fraudbusters.domain.BaseRaw;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BaseRawParametersGenerator {

    public static final String BASE_RAW_PARAMETERS = " timestamp, eventTimeHour, eventTime, " +
            "id, " +
            "email, providerId, " +
            "amount, currency, " +
            "status, errorReason, errorCode, " +
            "invoiceId, paymentId, " +
            "ip, bin, maskedPan, paymentTool, fingerprint, cardToken, " +
            "paymentSystem, paymentCountry, bankCountry, terminal ";

    public static final String BASE_RAW_PARAMETERS_MARK = "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";

    @NotNull
    public static Map<String, Object> generateParamsByFraudModel(BaseRaw value) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(BaseField.timestamp.name(), value.getTimestamp());
        parameters.put(BaseField.eventTimeHour.name(), value.getEventTimeHour());
        parameters.put(BaseField.eventTime.name(), value.getEventTime());

        parameters.put(BaseField.partyId.name(), value.getPartyId());
        parameters.put(BaseField.shopId.name(), value.getShopId());

        parameters.put(BaseField.amount.name(), value.getAmount());
        parameters.put(BaseField.currency.name(), value.getCurrency());

        Optional.ofNullable(value.getEmail()).ifPresent(v -> parameters.put(BaseField.email.name(), v));
        Optional.ofNullable(value.getProviderId()).ifPresent(v -> parameters.put(BaseField.providerId.name(), v));
        Optional.ofNullable(value.getTerminal()).ifPresent(v -> parameters.put(BaseField.terminal.name(), v));

        Optional.ofNullable(value.getStatus()).ifPresent(v -> parameters.put(BaseField.status.name(), v));
        Optional.ofNullable(value.getErrorCode()).ifPresent(v -> parameters.put(BaseField.errorCode.name(), v));
        Optional.ofNullable(value.getErrorReason()).ifPresent(v -> parameters.put(BaseField.errorReason.name(), v));

        Optional.ofNullable(value.getInvoiceId()).ifPresent(v -> parameters.put(BaseField.invoiceId.name(), v));
        Optional.ofNullable(value.getPaymentId()).ifPresent(v -> parameters.put(BaseField.paymentId.name(), v));

        Optional.ofNullable(value.getIp()).ifPresent(v -> parameters.put(BaseField.ip.name(), v));

        Optional.ofNullable(value.getFingerprint()).ifPresent(v -> parameters.put(BaseField.fingerprint.name(), v));
        Optional.ofNullable(value.getCardToken()).ifPresent(v -> parameters.put(BaseField.cardToken.name(), v));
        Optional.ofNullable(value.getPaymentSystem()).ifPresent(v -> parameters.put(BaseField.paymentSystem.name(), v));

        Optional.ofNullable(value.getPaymentCountry()).ifPresent(v -> parameters.put(BaseField.paymentCountry.name(), v));
        Optional.ofNullable(value.getBankCountry()).ifPresent(v -> parameters.put(BaseField.bankCountry.name(), v));

        return parameters;
    }

}