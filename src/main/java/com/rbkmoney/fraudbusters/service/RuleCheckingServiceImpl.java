package com.rbkmoney.fraudbusters.service;

import com.rbkmoney.fraudbusters.exception.InvalidTemplateException;
import com.rbkmoney.fraudbusters.fraud.FraudContextParser;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.fraud.payment.validator.PaymentTemplateValidator;
import com.rbkmoney.fraudo.FraudoPaymentParser;
import com.rbkmoney.fraudo.model.ResultModel;
import com.rbkmoney.fraudo.visitor.TemplateVisitor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RuleCheckingServiceImpl implements RuleCheckingService {

    private final PaymentTemplateValidator paymentTemplateValidator;
    private final FraudContextParser<FraudoPaymentParser.ParseContext> paymentContextParser;
    private final TemplateVisitor<PaymentModel, ResultModel> paymentRuleVisitor;

    @Override
    public Map<String, ResultModel> checkSingleRule(Map<String, PaymentModel> paymentModelMap, String templateString) {
        validateTemplate(templateString);
        final FraudoPaymentParser.ParseContext parseContext = paymentContextParser.parse(templateString);
        return paymentModelMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> paymentRuleVisitor.visit(parseContext, entry.getValue())
                ));
    }

    private void validateTemplate(String templateString) {
        List<String> validationErrors = paymentTemplateValidator.validate(templateString);
        if (!CollectionUtils.isEmpty(validationErrors)) {
            throw new InvalidTemplateException(templateString, validationErrors);
        }
    }
}
