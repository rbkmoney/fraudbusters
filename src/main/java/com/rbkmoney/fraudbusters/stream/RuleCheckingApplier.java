package com.rbkmoney.fraudbusters.stream;

import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import com.rbkmoney.fraudo.FraudoPaymentParser;

import java.util.List;
import java.util.Optional;

public interface RuleCheckingApplier<T> {

    Optional<CheckedResultModel> apply(T model, String templateKey);

    Optional<CheckedResultModel> applyForAny(T model, List<String> templateKeys);

    Optional<CheckedResultModel> applyWithContext(
            T model,
            String templateString,
            FraudoPaymentParser.ParseContext parseContext
    );

}
