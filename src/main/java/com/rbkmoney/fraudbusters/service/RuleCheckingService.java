package com.rbkmoney.fraudbusters.service;

import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudo.model.ResultModel;

import java.util.Map;

public interface RuleCheckingService {

    Map<String, ResultModel> checkSingleRule(Map<String, PaymentModel> paymentModelMap, String templateString);

}
