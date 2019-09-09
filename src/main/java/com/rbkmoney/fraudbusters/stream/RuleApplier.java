package com.rbkmoney.fraudbusters.stream;

import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import com.rbkmoney.fraudo.model.FraudModel;

import java.util.List;
import java.util.Optional;

public interface RuleApplier {

    Optional<CheckedResultModel> apply(FraudModel fraudModel, String templateKey);

    Optional<CheckedResultModel> applyForList(FraudModel fraudModel, List<String> templateKeys);

}
