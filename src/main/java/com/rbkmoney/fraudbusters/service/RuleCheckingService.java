package com.rbkmoney.fraudbusters.service;

import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.service.dto.CascadingTemplateDto;

import java.util.Map;

public interface RuleCheckingService {

    Map<String, CheckedResultModel> checkSingleRule(Map<String, PaymentModel> paymentModelMap, String templateString);

    Map<String, CheckedResultModel> checkRuleWithinRuleset(
            Map<String, PaymentModel> paymentModelMap,
            CascadingTemplateDto cascadingTemplateDto
    );

}
