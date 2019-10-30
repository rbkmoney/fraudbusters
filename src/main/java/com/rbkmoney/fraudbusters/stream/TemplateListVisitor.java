package com.rbkmoney.fraudbusters.stream;

import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import com.rbkmoney.fraudo.model.FraudModel;

import java.util.Map;

public interface TemplateListVisitor {

    Map<String, CheckedResultModel> visit(FraudModel fraudModel);

}
