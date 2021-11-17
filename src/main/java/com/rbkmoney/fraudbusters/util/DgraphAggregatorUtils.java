package com.rbkmoney.fraudbusters.util;

import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DgraphAggregatorUtils {

    public static boolean doesNotContainField(PaymentCheckedField countField,
                                        List<PaymentCheckedField> fields) {
        if (fields == null) {
            return true;
        }
        return fields.stream()
                .noneMatch(field -> field == countField);
    }

}
