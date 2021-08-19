package com.rbkmoney.fraudbusters.stream;

import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;
import java.util.Optional;

public interface RuleCheckingApplier<T> {

    Optional<CheckedResultModel> apply(T model, String templateKey, Long timestamp);

    Optional<CheckedResultModel> applyForAny(T model, List<String> templateKeys, Long timestamp);

    Optional<CheckedResultModel> applyWithContext(T model, String templateString, ParserRuleContext parseContext);

}
