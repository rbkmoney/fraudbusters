package com.rbkmoney.fraudbusters.repository.generator;

import com.rbkmoney.damsel.fraudbusters.Chargeback;
import com.rbkmoney.fraudbusters.constant.field.ChargebackField;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChargebackParametersGenerator {

    @NotNull
    public static Map<String, Object> generateParamsByFraudModel(Chargeback value) {
        Map<String, Object> parameters = BaseRawParametersGenerator.generateParamsByFraudModel(value);

        Optional.ofNullable(value.getCategory()).ifPresent(v -> parameters.put(ChargebackField.category.name(), v));
        Optional.ofNullable(value.getChargebackCode()).ifPresent(v -> parameters.put(ChargebackField.chargebackCode.name(), v));
        Optional.ofNullable(value.getChargebackId()).ifPresent(v -> parameters.put(ChargebackField.chargebackId.name(), v));
        Optional.ofNullable(value.getStage()).ifPresent(v -> parameters.put(ChargebackField.stage.name(), v));

        return parameters;
    }

}