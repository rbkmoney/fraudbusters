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
        return !doesContainField(countField, fields);
    }

    private static boolean doesContainField(PaymentCheckedField countField,
                                               List<PaymentCheckedField> fields) {
        return fields == null ? false : fields.contains(countField);
    }

    public static List<PaymentCheckedField> createFiltersList(PaymentCheckedField mainField,
                                                              List<PaymentCheckedField> groupingFields) {
        List<PaymentCheckedField> filters =
                groupingFields == null ? new ArrayList<>() : new ArrayList<>(groupingFields);
        if (filters.isEmpty() || DgraphAggregatorUtils.doesNotContainField(mainField, filters)) {
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
