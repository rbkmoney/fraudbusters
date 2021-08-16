package com.rbkmoney.fraudbusters.service;

import com.rbkmoney.fraudbusters.constant.TemplateLevel;
import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import com.rbkmoney.fraudbusters.domain.ConcreteResultModel;
import com.rbkmoney.fraudbusters.exception.InvalidTemplateException;
import com.rbkmoney.fraudbusters.fraud.FraudContextParser;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.fraud.payment.validator.PaymentTemplateValidator;
import com.rbkmoney.fraudbusters.pool.HistoricalPool;
import com.rbkmoney.fraudbusters.service.dto.CascadingTemplateDto;
import com.rbkmoney.fraudbusters.stream.impl.RuleCheckingApplierImpl;
import com.rbkmoney.fraudbusters.util.CheckedResultModelUtil;
import com.rbkmoney.fraudbusters.util.ReferenceKeyGenerator;
import com.rbkmoney.fraudo.FraudoPaymentParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class RuleCheckingServiceImpl implements RuleCheckingService {

    private final PaymentTemplateValidator paymentTemplateValidator;
    private final FraudContextParser<FraudoPaymentParser.ParseContext> paymentContextParser;
    private final RuleCheckingApplierImpl<PaymentModel> ruleCheckingApplier;
    private final HistoricalPool<List<String>> timeGroupPoolImpl;
    private final HistoricalPool<String> timeReferencePoolImpl;
    private final HistoricalPool<String> timeGroupReferencePoolImpl;


    @Override
    public Map<String, CheckedResultModel> checkSingleRule(Map<String, PaymentModel> paymentModelMap,
                                                           String templateString) {
        validateTemplate(templateString);
        final FraudoPaymentParser.ParseContext parseContext = paymentContextParser.parse(templateString);
        return paymentModelMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> ruleCheckingApplier
                                .applyWithContext(entry.getValue(), templateString, parseContext)
                                .orElseGet(() -> createDefaultResult(templateString, null))
                ));
    }

    @Override
    public Map<String, CheckedResultModel> checkRuleWithinRuleset(Map<String, PaymentModel> paymentModelMap,
                                                                  CascadingTemplateDto cascadingTemplateDto) {
        validateTemplate(cascadingTemplateDto.getTemplate());
        return paymentModelMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> checkWithinRuleset(entry.getValue(), cascadingTemplateDto)
                ));
    }


    private void validateTemplate(String templateString) {
        List<String> validationErrors = paymentTemplateValidator.validate(templateString);
        if (!CollectionUtils.isEmpty(validationErrors)) {
            throw new InvalidTemplateException(templateString, validationErrors);
        }
    }

    private CheckedResultModel checkWithinRuleset(PaymentModel paymentModel, CascadingTemplateDto dto) {
        log.debug("HistoricalTemplateVisitorImpl visit paymentModel: {}", paymentModel);
        final FraudoPaymentParser.ParseContext parseContext = paymentContextParser.parse(dto.getTemplate());
        Long timestamp = dto.getTimestamp() == null ? paymentModel.getTimestamp() : dto.getTimestamp();
        String partyId = paymentModel.getPartyId();
        String partyShopKey = ReferenceKeyGenerator.generateTemplateKey(partyId, paymentModel.getShopId());
        List<String> notifications = new ArrayList<>();
//         global template
        return applyGlobalTemplate(paymentModel, timestamp, notifications)
                // group template by party id
                .orElseGet(() -> applyGroupTemplateByAttribute(paymentModel, timestamp, partyId, notifications)
                        // group template by party-shop key
                        .orElseGet(() ->
                                applyGroupTemplateByAttribute(paymentModel, timestamp, partyShopKey, notifications)
                                // template by party id
                                .orElseGet(() -> (isSamePartyId(partyId, dto)
                                        ? applyExactRule(paymentModel, dto.getTemplate(), parseContext, notifications)
                                        : applyTemplateByAttribute(paymentModel, timestamp, partyId, notifications))
                                        // template by party-shop key
                                        .orElseGet(() -> (isSamePartyShopKey(partyShopKey, dto)
                                                ? applyExactRule(
                                                paymentModel, dto.getTemplate(), parseContext, notifications)
                                                : applyTemplateByAttribute(
                                                paymentModel, timestamp, partyShopKey, notifications))
                                                // no checks triggered, may contain notifications
                                                .orElseGet(() -> createDefaultResult(dto.getTemplate(), notifications)
                                                )))));
    }

    private Optional<CheckedResultModel> applyGlobalTemplate(PaymentModel paymentModel, Long timestamp,
                                                             List<String> notifications) {
        Optional<CheckedResultModel> result = ruleCheckingApplier
                .apply(paymentModel, timeReferencePoolImpl.get(TemplateLevel.GLOBAL.name(), timestamp));

        return processRuleCheckingApplierResult(result, notifications);
    }

    private Optional<CheckedResultModel> applyGroupTemplateByAttribute(PaymentModel paymentModel,
                                                                       Long timestamp,
                                                                       String referenceAttribute,
                                                                       List<String> notifications) {
        Optional<CheckedResultModel> result = ruleCheckingApplier.applyForAny(
                paymentModel,
                timeGroupPoolImpl.get(timeGroupReferencePoolImpl.get(referenceAttribute, timestamp), timestamp)
        );
        return processRuleCheckingApplierResult(result, notifications);
    }

    private Optional<CheckedResultModel> applyTemplateByAttribute(PaymentModel paymentModel,
                                                                  Long timestamp,
                                                                  String referenceAttribute,
                                                                  List<String> notifications) {
        Optional<CheckedResultModel> result =
                ruleCheckingApplier.apply(paymentModel, timeReferencePoolImpl.get(referenceAttribute, timestamp));
        return processRuleCheckingApplierResult(result, notifications);
    }

    private Optional<CheckedResultModel> applyExactRule(PaymentModel paymentModel,
                                                        String templateString,
                                                        FraudoPaymentParser.ParseContext parseContext,
                                                        List<String> notifications) {
        Optional<CheckedResultModel> result =
                ruleCheckingApplier.applyWithContext(paymentModel, templateString, parseContext);
        return processRuleCheckingApplierResult(result, notifications);
    }



    private boolean isSamePartyShopKey(String modelPartyShopKey, CascadingTemplateDto dto) {
        return modelPartyShopKey.equals(ReferenceKeyGenerator.generateTemplateKey(dto.getPartyId(), dto.getShopId()));
    }

    private boolean isSamePartyId(String modelPartyId, CascadingTemplateDto dto) {
        return modelPartyId.equals(dto.getPartyId());
    }

    private Optional<CheckedResultModel> processRuleCheckingApplierResult(Optional<CheckedResultModel> optional,
                                                                          List<String> notifications) {
        if (optional.isPresent()) {
            CheckedResultModel model = optional.get();
            if (CheckedResultModelUtil.isTerminal(model)) {
                CheckedResultModelUtil.finalizeCheckedResultModel(model, notifications);
                return Optional.of(model);
            } else {
                notifications.addAll(CheckedResultModelUtil.extractNotifications(optional));
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    @NotNull
    private CheckedResultModel createDefaultResult(String template, List<String> notifications) {
        ConcreteResultModel resultModel = new ConcreteResultModel();
        resultModel.setNotificationsRule(notifications);
        CheckedResultModel checkedResultModel = new CheckedResultModel();
        checkedResultModel.setResultModel(resultModel);
        checkedResultModel.setCheckedTemplate(template);
        return checkedResultModel;
    }
}
