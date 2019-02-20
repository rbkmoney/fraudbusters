package com.rbkmoney.fraudbusters.stream;

import com.rbkmoney.fraudo.model.FraudModel;
import com.rbkmoney.fraudo.model.ResultModel;

public interface TemplateVisitor {

    ResultModel visit(FraudModel fraudModel);

}
