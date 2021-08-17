package com.rbkmoney.fraudbusters.service;

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

import static com.rbkmoney.fraudbusters.util.ReferenceKeyGenerator.generateTemplateKey;

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
        String partyShopKey = generateTemplateKey(partyId, paymentModel.getShopId());
        List<String> notifications = new ArrayList<>();
        return applyGroupTemplateByAttribute(paymentModel, timestamp, notifications, partyId)
                .orElseGet(() -> applyGroupTemplateByAttribute(paymentModel, timestamp, notifications, partyShopKey)
                        .orElseGet(() -> applyTemplateByPartyId(paymentModel, timestamp, notifications, partyId,
                                dto, parseContext)
                                .orElseGet(() -> applyTemplateByPartyShopKey(paymentModel, timestamp, notifications,
                                        partyShopKey, dto, parseContext)
                                        .orElseGet(() -> createDefaultResult(dto.getTemplate(),
                                                notifications)
                                        ))));
    }

    private Optional<CheckedResultModel> applyGroupTemplateByAttribute(PaymentModel paymentModel,
                                                                       Long timestamp,
                                                                       List<String> notifications,
                                                                       String referenceAttribute) {
        Optional<CheckedResultModel> result = ruleCheckingApplier.applyForAny(
                paymentModel,
                timeGroupPoolImpl.get(timeGroupReferencePoolImpl.get(referenceAttribute, timestamp), timestamp)
        );
        return processRuleCheckingApplierResult(result, notifications);
    }

    private Optional<CheckedResultModel> applyTemplateByPartyId(PaymentModel paymentModel,
                                                                Long timestamp,
                                                                List<String> notifications,
                                                                String partyId,
                                                                CascadingTemplateDto dto,
                                                                FraudoPaymentParser.ParseContext parseContext) {
        if (isSubstituteOnPartyShopLevel(partyId, dto)) {
            return applyExactRule(paymentModel, dto.getTemplate(), parseContext, notifications);
        } else {
            return applyTemplateByAttribute(paymentModel, timestamp, notifications, partyId);
        }
    }

    private Optional<CheckedResultModel> applyTemplateByPartyShopKey(PaymentModel paymentModel,
                                                                     Long timestamp,
                                                                     List<String> notifications,
                                                                     String partyShopKey,
                                                                     CascadingTemplateDto dto,
                                                                     FraudoPaymentParser.ParseContext parseContext) {
        if (isSubstituteOnPartyLevel(partyShopKey, dto)) {
            return applyExactRule(paymentModel, dto.getTemplate(), parseContext, notifications);
        } else {
            return applyTemplateByAttribute(paymentModel, timestamp, notifications, partyShopKey);
        }
    }

    private Optional<CheckedResultModel> applyTemplateByAttribute(PaymentModel paymentModel,
                                                                  Long timestamp,
                                                                  List<String> notifications,
                                                                  String referenceAttribute) {
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


    private boolean isSubstituteOnPartyLevel(String modelPartyShopKey, CascadingTemplateDto dto) {
        return dto.getShopId() != null
                && modelPartyShopKey.equals(generateTemplateKey(dto.getPartyId(), dto.getShopId()));
    }

    private boolean isSubstituteOnPartyShopLevel(String modelPartyId, CascadingTemplateDto dto) {
        return dto.getShopId() == null && modelPartyId.equals(dto.getPartyId());
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
