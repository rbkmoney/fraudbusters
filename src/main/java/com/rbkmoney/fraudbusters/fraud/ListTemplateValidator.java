package com.rbkmoney.fraudbusters.fraud;

import com.rbkmoney.damsel.fraudbusters.Template;
import com.rbkmoney.damsel.fraudbusters.ValidateTemplateResponse;

import java.util.List;

public interface ListTemplateValidator {

    ValidateTemplateResponse validateCompilationTemplate(List<Template> list);

}
