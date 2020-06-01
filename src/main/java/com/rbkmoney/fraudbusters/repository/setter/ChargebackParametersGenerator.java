package com.rbkmoney.fraudbusters.repository.setter;

import com.rbkmoney.fraudbusters.constant.field.PaymentField;
import com.rbkmoney.fraudbusters.domain.Chargeback;
import com.rbkmoney.fraudbusters.domain.Payment;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChargebackParametersGenerator {

    @NotNull
    public static Map<String, Object> generateParamsByFraudModel(Chargeback value) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PaymentField.timestamp.name(), value.getTimestamp());
        parameters.put(PaymentField.eventTimeHour.name(), value.getEventTimeHour());
        parameters.put(PaymentField.eventTime.name(), value.getEventTime());

        parameters.put(PaymentField.partyId.name(), value.getPartyId());
        parameters.put(PaymentField.shopId.name(), value.getShopId());

        parameters.put(PaymentField.amount.name(), value.getAmount());
        parameters.put(PaymentField.currency.name(), value.getCurrency());

        Optional.ofNullable(value.getEmail()).ifPresent(v -> parameters.put(PaymentField.email.name(), v));
        Optional.ofNullable(value.getProviderId()).ifPresent(v -> parameters.put(PaymentField.providerId.name(), v));
        Optional.ofNullable(value.getTerminal()).ifPresent(v -> parameters.put(PaymentField.terminal.name(), v));

        Optional.ofNullable(value.getStatus()).ifPresent(v -> parameters.put(PaymentField.status.name(), v));
        Optional.ofNullable(value.getErrorCode()).ifPresent(v -> parameters.put(PaymentField.errorCode.name(), v));
        Optional.ofNullable(value.getErrorReason()).ifPresent(v -> parameters.put(PaymentField.errorReason.name(), v));

        Optional.ofNullable(value.getInvoiceId()).ifPresent(v -> parameters.put(PaymentField.invoiceId.name(), v));
        Optional.ofNullable(value.getPaymentId()).ifPresent(v -> parameters.put(PaymentField.paymentId.name(), v));

        Optional.ofNullable(value.getIp()).ifPresent(v -> parameters.put(PaymentField.ip.name(), v));
        Optional.ofNullable(value.getBin()).ifPresent(v -> parameters.put(PaymentField.bin.name(), v));
        Optional.ofNullable(value.getMaskedPan()).ifPresent(v -> parameters.put(PaymentField.maskedPan.name(), v));
        Optional.ofNullable(value.getPaymentTool()).ifPresent(v -> parameters.put(PaymentField.paymentTool.name(), v));

        Optional.ofNullable(value.getFingerprint()).ifPresent(v -> parameters.put(PaymentField.fingerprint.name(), v));
        Optional.ofNullable(value.getCardToken()).ifPresent(v -> parameters.put(PaymentField.cardToken.name(), v));
        Optional.ofNullable(value.getPaymentSystem()).ifPresent(v -> parameters.put(PaymentField.paymentSystem.name(), v));

        Optional.ofNullable(value.getPaymentCountry()).ifPresent(v -> parameters.put(PaymentField.paymentCountry.name(), v));
        Optional.ofNullable(value.getBankCountry()).ifPresent(v -> parameters.put(PaymentField.bankCountry.name(), v));

        return parameters;
    }

}