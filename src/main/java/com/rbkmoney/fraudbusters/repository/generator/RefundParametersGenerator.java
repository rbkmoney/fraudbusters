package com.rbkmoney.fraudbusters.repository.generator;

import com.rbkmoney.damsel.fraudbusters.Refund;
import com.rbkmoney.fraudbusters.constant.field.RefundField;
import com.rbkmoney.fraudbusters.repository.generator.BaseRawParametersGenerator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RefundParametersGenerator {

    @NotNull
    public static Map<String, Object> generateParamsByFraudModel(Refund value) {
        Map<String, Object> parameters = BaseRawParametersGenerator.generateParamsByFraudModel(value);

        Optional.ofNullable(value.getReason()).ifPresent(v -> parameters.put(RefundField.reason.name(), v));
        Optional.ofNullable(value.getRefundId()).ifPresent(v -> parameters.put(RefundField.refundId.name(), v));

        return parameters;
    }

}