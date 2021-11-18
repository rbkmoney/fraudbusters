package com.rbkmoney.fraudbusters.util;

import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
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

    public static List<PaymentCheckedField> createFiltersList(PaymentCheckedField mainField,
                                                              List<PaymentCheckedField> groupingFields) {
        List<PaymentCheckedField> filters = groupingFields == null ? new ArrayList<>() : new ArrayList<>(groupingFields);
        if (groupingFields.isEmpty() || DgraphAggregatorUtils.doesNotContainField(mainField, groupingFields)) {
            filters.add(mainField);
        }
        return filters;
    }

    public static Instant getTimestamp(PaymentModel paymentModel) {
        return paymentModel.getTimestamp() != null
                ? Instant.ofEpochMilli(paymentModel.getTimestamp())
                : Instant.now();
    }

}
