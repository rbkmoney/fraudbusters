package com.rbkmoney.fraudbusters.service;

import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudo.model.ResultModel;

import java.util.Map;

public interface RuleTestingService {

    Map<String, ResultModel> applySingleRule(Map<String, PaymentModel> paymentModelMap, String templateString);

}
