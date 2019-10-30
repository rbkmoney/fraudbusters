package com.rbkmoney.fraudbusters.stream;

import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import com.rbkmoney.fraudo.model.PaymentModel;

import java.util.List;
import java.util.Optional;

public interface RuleApplier {

    Optional<CheckedResultModel> apply(PaymentModel fraudModel, String templateKey);

    Optional<CheckedResultModel> applyForAny(PaymentModel fraudModel, List<String> templateKeys);

}
