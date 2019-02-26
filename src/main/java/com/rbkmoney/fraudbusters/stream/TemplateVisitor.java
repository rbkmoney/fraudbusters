package com.rbkmoney.fraudbusters.stream;

import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import com.rbkmoney.fraudo.model.FraudModel;

public interface TemplateVisitor {

    CheckedResultModel visit(FraudModel fraudModel);

}
