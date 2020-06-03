package com.rbkmoney.fraudbusters.repository.setter;

import com.rbkmoney.fraudbusters.constant.field.PaymentField;
import com.rbkmoney.fraudbusters.domain.Payment;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentParametersGenerator {

    @NotNull
    public static Map<String, Object> generateParamsByFraudModel(Payment value) {
        Map<String, Object> parameters = BaseRawParametersGenerator.generateParamsByFraudModel(value);

        Optional.ofNullable(value.getBin()).ifPresent(v -> parameters.put(PaymentField.bin.name(), v));
        Optional.ofNullable(value.getMaskedPan()).ifPresent(v -> parameters.put(PaymentField.maskedPan.name(), v));
        Optional.ofNullable(value.getPaymentTool()).ifPresent(v -> parameters.put(PaymentField.paymentTool.name(), v));

        return parameters;
    }

}