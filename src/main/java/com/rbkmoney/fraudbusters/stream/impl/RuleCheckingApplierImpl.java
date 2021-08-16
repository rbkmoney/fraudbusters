package com.rbkmoney.fraudbusters.stream.impl;

import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import com.rbkmoney.fraudbusters.fraud.FraudContextParser;
import com.rbkmoney.fraudbusters.stream.RuleCheckingApplier;
import com.rbkmoney.fraudbusters.util.CheckedResultFactory;
import com.rbkmoney.fraudbusters.util.CheckedResultModelUtil;
import com.rbkmoney.fraudo.FraudoPaymentParser;
import com.rbkmoney.fraudo.model.BaseModel;
import com.rbkmoney.fraudo.model.ResultModel;
import com.rbkmoney.fraudo.visitor.TemplateVisitor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class RuleCheckingApplierImpl<T extends BaseModel> implements RuleCheckingApplier<T> {

    private final TemplateVisitor<T, ResultModel> templateVisitor;
    private final CheckedResultFactory checkedResultFactory;
    private final FraudContextParser<FraudoPaymentParser.ParseContext> paymentContextParser;

    @Override
    public Optional<CheckedResultModel> apply(T model, String templateString) {
        FraudoPaymentParser.ParseContext parseContext = paymentContextParser.parse(templateString);
        return applyWithContext(model, templateString, parseContext);
    }

    @Override
    public Optional<CheckedResultModel> applyForAny(T model, List<String> templateStrings) {
        if (templateStrings != null) {
            List<String> notifications = new ArrayList<>();
            for (String templateKey : templateStrings) {
                Optional<CheckedResultModel> optionalResult = apply(model, templateKey);
                // for each non terminal result, add notifications and continue processing
                if (optionalResult.isPresent()) {
                    CheckedResultModel result = optionalResult.get();
                    if (CheckedResultModelUtil.isTerminal(result)) {
                        return Optional.of(
                                CheckedResultModelUtil.finalizeCheckedResultModel(result, notifications)
                        );
                    } else {
                        notifications.addAll(CheckedResultModelUtil.extractNotifications(optionalResult));
                    }
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<CheckedResultModel> applyWithContext(T model, String templateString,
                                                        FraudoPaymentParser.ParseContext parseContext) {
        if (parseContext != null) {
            ResultModel resultModel = templateVisitor.visit(parseContext, model);
            return Optional.of(checkedResultFactory.createCheckedResultWithNotifications(templateString, resultModel));
        }
        return Optional.empty();
    }


}
