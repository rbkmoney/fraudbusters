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
import com.rbkmoney.fraudbusters.util.CheckedResultFactory;
import com.rbkmoney.fraudo.FraudoPaymentParser;
import com.rbkmoney.fraudo.constant.ResultStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {RuleCheckingServiceImpl.class})
class RuleCheckingServiceImplTest {

    @Autowired
    private RuleCheckingServiceImpl service;

    @MockBean
    private PaymentTemplateValidator paymentTemplateValidator;

    @MockBean
    private FraudContextParser<FraudoPaymentParser.ParseContext> paymentContextParser;

    @MockBean
    private RuleCheckingApplierImpl<PaymentModel> ruleCheckingApplier;

    @MockBean
    private HistoricalPool<List<String>> timeGroupPoolImpl;

    @MockBean(name = "timeReferencePoolImpl")
    private HistoricalPool<String> timeReferencePoolImpl;

    @MockBean(name = "timeGroupReferencePoolImpl")
    private HistoricalPool<String> timeGroupReferencePoolImpl;

    @MockBean
    private CheckedResultFactory checkedResultFactory;

    private static final Long TIMESTAMP = Instant.now().toEpochMilli();
    private static final String TEMPLATE = "rule: amount > 100 -> accept;";
    private static final List<String> GROUP_PARTY_TEMPLATE = List.of("rule: amount > 20 -> accept;");
    private static final List<String> GROUP_SHOP_TEMPLATE = List.of("rule: amount > 21 -> accept;");
    private static final String PARTY_TEMPLATE = "rule: amount > 25 -> accept;";
    private static final String SHOP_TEMPLATE = "rule: amount > 26 -> accept;";

    private static final String GROUP_REF_PARTY = "GROUP_REF_PARTY";
    private static final String GROUP_REF_SHOP = "GROUP_REF_SHOP";

    private static final String TEMPLATE_REF_PARTY_LEVEL = "TEMPLATE_REF_PARTY_LEVEL";
    private static final String TEMPLATE_REF_SHOP_LEVEL = "TEMPLATE_REF_SHOP_LEVEL";
    private static final String GROUP_REF_PARTY_LEVEL = "GROUP_REF_PARTY_LEVEL";
    private static final String GROUP_REF_SHOP_LEVEL = "GROUP_REF_SHOP_LEVEL";
    private static final String PARTY = "party_id";
    private static final String SHOP = "shop_id";
    private static final String PARTY_SHOP_KEY = PARTY + "_" + SHOP;

    @Test
    void applySingleRuleThrowsInvalidTemplateException() {
        when(paymentTemplateValidator.validate(TEMPLATE)).thenReturn(List.of("123", "321"));
        assertThrows(InvalidTemplateException.class, () -> service.checkSingleRule(
                Map.of(
                        UUID.randomUUID().toString(), createPaymentModel(0L),
                        UUID.randomUUID().toString(), createPaymentModel(1L)),
                TEMPLATE
        ));
    }

    @Test
    void checkSingleRule() {
        FraudoPaymentParser.ParseContext context = new FraudoPaymentParser.ParseContext(null, 0);
        CheckedResultModel firstResultModel = createResultModel(ResultStatus.ACCEPT);
        CheckedResultModel secondResultModel = createResultModel(ResultStatus.DECLINE);
        when(paymentTemplateValidator.validate(anyString())).thenReturn(new ArrayList<>());
        when(paymentContextParser.parse(anyString())).thenReturn(context);
        when(ruleCheckingApplier
                .applyWithContext(any(PaymentModel.class), anyString(), any(FraudoPaymentParser.ParseContext.class))
        )
                .thenReturn(Optional.of(firstResultModel))
                .thenReturn(Optional.of(secondResultModel));

        String firstId = UUID.randomUUID().toString();
        String secondId = UUID.randomUUID().toString();
        PaymentModel firstPaymentModel = createPaymentModel(25L);
        PaymentModel secondPaymentModel = createPaymentModel(2L);
        Map<String, PaymentModel> paymentModelMap = new LinkedHashMap<>();
        paymentModelMap.put(firstId, firstPaymentModel);
        paymentModelMap.put(secondId, secondPaymentModel);

        Map<String, CheckedResultModel> actual = service.checkSingleRule(paymentModelMap, TEMPLATE);

        //result verification
        assertEquals(2, actual.size());
        assertEquals(firstResultModel, actual.get(firstId));
        assertEquals(secondResultModel, actual.get(secondId));

        //mock cheсks
        ArgumentCaptor<String> validatorTemplateStringCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> contextParserTemplateStringCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> ruleCheckingApplierTemplateStringCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<FraudoPaymentParser.ParseContext> contextCaptor =
                ArgumentCaptor.forClass(FraudoPaymentParser.ParseContext.class);
        ArgumentCaptor<PaymentModel> paymentModelCaptor = ArgumentCaptor.forClass(PaymentModel.class);

        verify(paymentTemplateValidator, times(1))
                .validate(validatorTemplateStringCaptor.capture());
        verify(paymentContextParser, times(1))
                .parse(contextParserTemplateStringCaptor.capture());
        verify(ruleCheckingApplier, times(2))
                .applyWithContext(
                        paymentModelCaptor.capture(),
                        ruleCheckingApplierTemplateStringCaptor.capture(),
                        contextCaptor.capture()
                );
        verify(checkedResultFactory, times(0))
                .createNotificationOnlyResultModel(anyString(), anyList());

        //template validator verification
        assertEquals(1, validatorTemplateStringCaptor.getAllValues().size());
        assertEquals(TEMPLATE, validatorTemplateStringCaptor.getValue());

        //context parser verification
        assertEquals(1, contextParserTemplateStringCaptor.getAllValues().size());
        assertEquals(TEMPLATE, contextParserTemplateStringCaptor.getValue());

        //template visitor verification
        assertEquals(2, paymentModelCaptor.getAllValues().size());
        assertEquals(firstPaymentModel, paymentModelCaptor.getAllValues().get(0));
        assertEquals(secondPaymentModel, paymentModelCaptor.getAllValues().get(1));
        assertEquals(2, ruleCheckingApplierTemplateStringCaptor.getAllValues().size());
        assertEquals(TEMPLATE, ruleCheckingApplierTemplateStringCaptor.getAllValues().get(0));
        assertEquals(TEMPLATE, ruleCheckingApplierTemplateStringCaptor.getAllValues().get(1));
        assertEquals(2, contextCaptor.getAllValues().size());
        assertEquals(context, contextCaptor.getAllValues().get(0));
        assertEquals(context, contextCaptor.getAllValues().get(1));
    }

    @Test
    void checkRuleWithinRulesetThrowsInvalidTemplateException() {
        CascadingTemplateDto dto = new CascadingTemplateDto();
        dto.setTemplate(TEMPLATE);
        when(paymentTemplateValidator.validate(TEMPLATE)).thenReturn(List.of("123", "321"));
        assertThrows(InvalidTemplateException.class, () -> service.checkRuleWithinRuleset(
                Map.of(
                        UUID.randomUUID().toString(), createPaymentModel(0L),
                        UUID.randomUUID().toString(), createPaymentModel(1L)),
                dto
        ));
    }

    @Test
    void checkRuleWithinRulesetGroupTemplateByParty() {
        FraudoPaymentParser.ParseContext context = new FraudoPaymentParser.ParseContext(null, 0);
        PaymentModel paymentModel = createPaymentModel(ThreadLocalRandom.current().nextLong());

        when(paymentTemplateValidator.validate(anyString())).thenReturn(new ArrayList<>());
        when(paymentContextParser.parse(anyString())).thenReturn(context);
        //group template by party
        when(timeGroupReferencePoolImpl.get(PARTY, TIMESTAMP)).thenReturn(GROUP_REF_PARTY_LEVEL);
        when(timeGroupPoolImpl.get(GROUP_REF_PARTY_LEVEL, TIMESTAMP)).thenReturn(GROUP_PARTY_TEMPLATE);
        when(ruleCheckingApplier.applyForAny(paymentModel, GROUP_PARTY_TEMPLATE))
                .thenReturn(Optional.of(createResultModel(GROUP_PARTY_TEMPLATE.get(0), ResultStatus.ACCEPT)));


        String transactionId = UUID.randomUUID().toString();
        Map<String, CheckedResultModel> actual =
                service.checkRuleWithinRuleset(Map.of(transactionId, paymentModel), createCascadingDto(TEMPLATE));

        assertEquals(1, actual.size());
        CheckedResultModel expected = createResultModel(GROUP_PARTY_TEMPLATE.get(0), ResultStatus.ACCEPT);
        assertEquals(expected, actual.get(transactionId));
        verify(paymentTemplateValidator, times(1)).validate(anyString());
        verify(paymentContextParser, times(1)).parse(anyString());
        verify(timeGroupReferencePoolImpl, times(1)).get(anyString(), anyLong());
        verify(timeGroupPoolImpl, times(1)).get(anyString(), anyLong());
        verify(ruleCheckingApplier, times(1)).applyForAny(any(PaymentModel.class), anyList());
        verify(checkedResultFactory, times(0))
                .createNotificationOnlyResultModel(anyString(), anyList());
    }

    @Test
    void checkRuleWithinRulesetGroupTemplateByPartyShop() {
        PaymentModel paymentModel = createPaymentModel(ThreadLocalRandom.current().nextLong());
        FraudoPaymentParser.ParseContext context = new FraudoPaymentParser.ParseContext(null, 0);

        when(paymentTemplateValidator.validate(anyString())).thenReturn(new ArrayList<>());
        when(paymentContextParser.parse(anyString())).thenReturn(context);
        //group template by party
        when(timeGroupReferencePoolImpl.get(PARTY, TIMESTAMP)).thenReturn(GROUP_REF_PARTY);
        when(timeGroupPoolImpl.get(GROUP_REF_PARTY, TIMESTAMP)).thenReturn(GROUP_PARTY_TEMPLATE);
        when(ruleCheckingApplier.applyForAny(paymentModel, GROUP_PARTY_TEMPLATE))
                .thenReturn(Optional.of(createNotificationOnlyCheckedResult(GROUP_REF_PARTY_LEVEL)));
        //group template by party-shop-key
        when(timeGroupReferencePoolImpl.get(PARTY_SHOP_KEY, TIMESTAMP)).thenReturn(GROUP_REF_SHOP);
        when(timeGroupPoolImpl.get(GROUP_REF_SHOP, TIMESTAMP)).thenReturn(GROUP_SHOP_TEMPLATE);
        when(ruleCheckingApplier.applyForAny(paymentModel, GROUP_SHOP_TEMPLATE))
                .thenReturn(Optional.of(createResultModel(GROUP_SHOP_TEMPLATE.get(0), ResultStatus.ACCEPT)));


        String transactionId = UUID.randomUUID().toString();
        Map<String, CheckedResultModel> actual =
                service.checkRuleWithinRuleset(Map.of(transactionId, paymentModel), createCascadingDto(TEMPLATE));

        assertEquals(1, actual.size());
        CheckedResultModel expected = createResultModel(GROUP_SHOP_TEMPLATE.get(0), ResultStatus.ACCEPT);
        expected.getResultModel().setNotificationsRule(List.of(GROUP_REF_PARTY_LEVEL));
        assertEquals(expected, actual.get(transactionId));
        verify(paymentTemplateValidator, times(1)).validate(anyString());
        verify(paymentContextParser, times(1)).parse(anyString());
        verify(timeGroupReferencePoolImpl, times(2)).get(anyString(), anyLong());
        verify(timeGroupPoolImpl, times(2)).get(anyString(), anyLong());
        verify(ruleCheckingApplier, times(2)).applyForAny(any(PaymentModel.class), anyList());
        verify(checkedResultFactory, times(0))
                .createNotificationOnlyResultModel(anyString(), anyList());
    }

    @Test
    void checkRuleWithinRulesetTemplateByPartyIdDifferentPartyId() {
        String differentPartyId = UUID.randomUUID().toString();
        PaymentModel paymentModel = createPaymentModel(ThreadLocalRandom.current().nextLong());
        paymentModel.setPartyId(differentPartyId);
        FraudoPaymentParser.ParseContext context = new FraudoPaymentParser.ParseContext(null, 0);

        when(paymentTemplateValidator.validate(anyString())).thenReturn(new ArrayList<>());
        when(paymentContextParser.parse(anyString())).thenReturn(context);
        //group template by party
        when(timeGroupReferencePoolImpl.get(differentPartyId, TIMESTAMP)).thenReturn(GROUP_REF_PARTY);
        when(timeGroupPoolImpl.get(GROUP_REF_PARTY, TIMESTAMP)).thenReturn(GROUP_PARTY_TEMPLATE);
        when(ruleCheckingApplier.applyForAny(paymentModel, GROUP_PARTY_TEMPLATE))
                .thenReturn(Optional.of(createNotificationOnlyCheckedResult(GROUP_REF_PARTY_LEVEL)));
        //group template by party-shop-key
        String differentPartyShopKey = differentPartyId + "_" + SHOP;
        when(timeGroupReferencePoolImpl.get(differentPartyShopKey, TIMESTAMP)).thenReturn(GROUP_REF_SHOP);
        when(timeGroupPoolImpl.get(GROUP_REF_SHOP, TIMESTAMP)).thenReturn(GROUP_SHOP_TEMPLATE);
        when(ruleCheckingApplier.applyForAny(paymentModel, GROUP_SHOP_TEMPLATE))
                .thenReturn(Optional.of(createNotificationOnlyCheckedResult(GROUP_REF_SHOP_LEVEL)));
        //template by party (not substituted by template from request)
        when(timeReferencePoolImpl.get(differentPartyId, TIMESTAMP)).thenReturn(PARTY_TEMPLATE);
        when(ruleCheckingApplier.apply(paymentModel, PARTY_TEMPLATE))
                .thenReturn(Optional.of(createResultModel(PARTY_TEMPLATE, ResultStatus.ACCEPT)));


        String transactionId = UUID.randomUUID().toString();
        Map<String, CheckedResultModel> actual =
                service.checkRuleWithinRuleset(Map.of(transactionId, paymentModel), createCascadingDto(TEMPLATE));

        assertEquals(1, actual.size());
        CheckedResultModel expected = createResultModel(PARTY_TEMPLATE, ResultStatus.ACCEPT);
        expected.getResultModel().setNotificationsRule(List.of(
                GROUP_REF_PARTY_LEVEL,
                GROUP_REF_SHOP_LEVEL
        ));
        assertEquals(expected, actual.get(transactionId));
        verify(paymentTemplateValidator, times(1)).validate(anyString());
        verify(paymentContextParser, times(1)).parse(anyString());
        verify(timeReferencePoolImpl, times(1)).get(anyString(), anyLong());
        verify(ruleCheckingApplier, times(1)).apply(any(PaymentModel.class), anyString());
        verify(timeGroupReferencePoolImpl, times(2)).get(anyString(), anyLong());
        verify(timeGroupPoolImpl, times(2)).get(anyString(), anyLong());
        verify(ruleCheckingApplier, times(2)).applyForAny(any(PaymentModel.class), anyList());
        verify(checkedResultFactory, times(0))
                .createNotificationOnlyResultModel(anyString(), anyList());
    }

    @Test
    void checkRuleWithinRulesetTemplateByPartyShopKeyDifferentPartyShopKey() {
        String differentPartyId = UUID.randomUUID().toString();
        String differentShopId = UUID.randomUUID().toString();
        PaymentModel paymentModel = createPaymentModel(ThreadLocalRandom.current().nextLong());
        paymentModel.setPartyId(differentPartyId);
        paymentModel.setShopId(differentShopId);
        FraudoPaymentParser.ParseContext context = new FraudoPaymentParser.ParseContext(null, 0);

        when(paymentTemplateValidator.validate(anyString())).thenReturn(new ArrayList<>());
        when(paymentContextParser.parse(anyString())).thenReturn(context);
        //group template by party
        when(timeGroupReferencePoolImpl.get(differentPartyId, TIMESTAMP)).thenReturn(GROUP_REF_PARTY);
        when(timeGroupPoolImpl.get(GROUP_REF_PARTY, TIMESTAMP)).thenReturn(GROUP_PARTY_TEMPLATE);
        when(ruleCheckingApplier.applyForAny(paymentModel, GROUP_PARTY_TEMPLATE))
                .thenReturn(Optional.of(createNotificationOnlyCheckedResult(GROUP_REF_PARTY_LEVEL)));
        //group template by party-shop key
        String differentPartyShopKey = differentPartyId + "_" + differentShopId;
        when(timeGroupReferencePoolImpl.get(differentPartyShopKey, TIMESTAMP)).thenReturn(GROUP_REF_SHOP);
        when(timeGroupPoolImpl.get(GROUP_REF_SHOP, TIMESTAMP)).thenReturn(GROUP_SHOP_TEMPLATE);
        when(ruleCheckingApplier.applyForAny(paymentModel, GROUP_SHOP_TEMPLATE))
                .thenReturn(Optional.of(createNotificationOnlyCheckedResult(GROUP_REF_SHOP_LEVEL)));
        //template by party (not substituted by template from request)
        when(timeReferencePoolImpl.get(differentPartyId, TIMESTAMP)).thenReturn(PARTY_TEMPLATE);
        when(ruleCheckingApplier.apply(paymentModel, PARTY_TEMPLATE))
                .thenReturn(Optional.of(createNotificationOnlyCheckedResult(TEMPLATE_REF_PARTY_LEVEL)));
        //template by party-shop key (not substituted by template from request)
        when(timeReferencePoolImpl.get(differentPartyShopKey, TIMESTAMP)).thenReturn(SHOP_TEMPLATE);
        when(ruleCheckingApplier.apply(paymentModel, SHOP_TEMPLATE))
                .thenReturn(Optional.of(createResultModel(SHOP_TEMPLATE, ResultStatus.ACCEPT)));


        String transactionId = UUID.randomUUID().toString();
        Map<String, CheckedResultModel> actual =
                service.checkRuleWithinRuleset(Map.of(transactionId, paymentModel), createCascadingDto(TEMPLATE));

        assertEquals(1, actual.size());
        CheckedResultModel expected = createResultModel(SHOP_TEMPLATE, ResultStatus.ACCEPT);
        expected.getResultModel().setNotificationsRule(List.of(
                GROUP_REF_PARTY_LEVEL,
                GROUP_REF_SHOP_LEVEL,
                TEMPLATE_REF_PARTY_LEVEL
        ));
        assertEquals(expected, actual.get(transactionId));
        verify(paymentTemplateValidator, times(1)).validate(anyString());
        verify(paymentContextParser, times(1)).parse(anyString());
        verify(timeReferencePoolImpl, times(2)).get(anyString(), anyLong());
        verify(ruleCheckingApplier, times(2)).apply(any(PaymentModel.class), anyString());
        verify(timeGroupReferencePoolImpl, times(2)).get(anyString(), anyLong());
        verify(timeGroupPoolImpl, times(2)).get(anyString(), anyLong());
        verify(ruleCheckingApplier, times(2)).applyForAny(any(PaymentModel.class), anyList());
        verify(checkedResultFactory, times(0))
                .createNotificationOnlyResultModel(anyString(), anyList());
    }

    @Test
    void checkRuleWithinRulesetTemplateByPartyIdSamePartyId() {
        PaymentModel paymentModel = createPaymentModel(ThreadLocalRandom.current().nextLong());
        FraudoPaymentParser.ParseContext context = new FraudoPaymentParser.ParseContext(null, 0);

        when(paymentTemplateValidator.validate(anyString())).thenReturn(new ArrayList<>());
        when(paymentContextParser.parse(anyString())).thenReturn(context);
        //group template by party
        when(timeGroupReferencePoolImpl.get(PARTY, TIMESTAMP)).thenReturn(GROUP_REF_PARTY);
        when(timeGroupPoolImpl.get(GROUP_REF_PARTY, TIMESTAMP)).thenReturn(GROUP_PARTY_TEMPLATE);
        when(ruleCheckingApplier.applyForAny(paymentModel, GROUP_PARTY_TEMPLATE))
                .thenReturn(Optional.of(createNotificationOnlyCheckedResult(GROUP_REF_PARTY_LEVEL)));
        //group template by party-shop-key
        when(timeGroupReferencePoolImpl.get(PARTY_SHOP_KEY, TIMESTAMP)).thenReturn(GROUP_REF_SHOP);
        when(timeGroupPoolImpl.get(GROUP_REF_SHOP, TIMESTAMP)).thenReturn(GROUP_SHOP_TEMPLATE);
        when(ruleCheckingApplier.applyForAny(paymentModel, GROUP_SHOP_TEMPLATE))
                .thenReturn(Optional.of(createNotificationOnlyCheckedResult(GROUP_REF_SHOP_LEVEL)));
        //template by party (substituted by template from request)
        when(ruleCheckingApplier.applyWithContext(paymentModel, TEMPLATE, context))
                .thenReturn(Optional.of(createResultModel(TEMPLATE, ResultStatus.ACCEPT)));


        String transactionId = UUID.randomUUID().toString();
        Map<String, CheckedResultModel> actual =
                service.checkRuleWithinRuleset(Map.of(transactionId, paymentModel), createCascadingDto(TEMPLATE));

        assertEquals(1, actual.size());
        CheckedResultModel expected = createResultModel(TEMPLATE, ResultStatus.ACCEPT);
        expected.getResultModel().setNotificationsRule(List.of(GROUP_REF_PARTY_LEVEL, GROUP_REF_SHOP_LEVEL));
        assertEquals(expected, actual.get(transactionId));
        verify(paymentTemplateValidator, times(1)).validate(anyString());
        verify(paymentContextParser, times(1)).parse(anyString());
        verify(timeGroupReferencePoolImpl, times(2)).get(anyString(), anyLong());
        verify(timeGroupPoolImpl, times(2)).get(anyString(), anyLong());
        verify(ruleCheckingApplier, times(2)).applyForAny(any(PaymentModel.class), anyList());
        verify(ruleCheckingApplier, times(1))
                .applyWithContext(any(PaymentModel.class), anyString(), any(FraudoPaymentParser.ParseContext.class));
        verify(checkedResultFactory, times(0))
                .createNotificationOnlyResultModel(anyString(), anyList());
    }

    @Test
    void checkRuleWithinRulesetTemplateByPartyShopKeySamePartyShopKey() {
        PaymentModel paymentModel = createPaymentModel(ThreadLocalRandom.current().nextLong());
        FraudoPaymentParser.ParseContext context = new FraudoPaymentParser.ParseContext(null, 0);

        when(paymentTemplateValidator.validate(anyString())).thenReturn(new ArrayList<>());
        when(paymentContextParser.parse(anyString())).thenReturn(context);
        //group template by party
        when(timeGroupReferencePoolImpl.get(PARTY, TIMESTAMP)).thenReturn(GROUP_REF_PARTY);
        when(timeGroupPoolImpl.get(GROUP_REF_PARTY, TIMESTAMP)).thenReturn(GROUP_PARTY_TEMPLATE);
        when(ruleCheckingApplier.applyForAny(paymentModel, GROUP_PARTY_TEMPLATE))
                .thenReturn(Optional.of(createNotificationOnlyCheckedResult(GROUP_REF_PARTY_LEVEL)));
        //group template by party-shop key
        when(timeGroupReferencePoolImpl.get(PARTY_SHOP_KEY, TIMESTAMP)).thenReturn(GROUP_REF_SHOP);
        when(timeGroupPoolImpl.get(GROUP_REF_SHOP, TIMESTAMP)).thenReturn(GROUP_SHOP_TEMPLATE);
        when(ruleCheckingApplier.applyForAny(paymentModel, GROUP_SHOP_TEMPLATE))
                .thenReturn(Optional.of(createNotificationOnlyCheckedResult(GROUP_REF_SHOP_LEVEL)));
        //template by party (not substituted by template from request)
        when(timeReferencePoolImpl.get(PARTY, TIMESTAMP)).thenReturn(PARTY_TEMPLATE);
        when(ruleCheckingApplier.apply(paymentModel, PARTY_TEMPLATE))
                .thenReturn(Optional.of(createNotificationOnlyCheckedResult(TEMPLATE_REF_PARTY_LEVEL)));
        //template by party-shop key (substituted by template from request)
        when(ruleCheckingApplier.applyWithContext(paymentModel, TEMPLATE, context))
                .thenReturn(Optional.of(createResultModel(TEMPLATE, ResultStatus.ACCEPT)));


        String transactionId = UUID.randomUUID().toString();
        Map<String, CheckedResultModel> actual =
                service.checkRuleWithinRuleset(Map.of(transactionId, paymentModel), createCascadingDto(TEMPLATE));

        assertEquals(1, actual.size());
        CheckedResultModel expected = createResultModel(TEMPLATE, ResultStatus.ACCEPT);
        expected.getResultModel().setNotificationsRule(List.of(
                GROUP_REF_PARTY_LEVEL,
                GROUP_REF_SHOP_LEVEL,
                TEMPLATE_REF_PARTY_LEVEL
        ));
        assertEquals(expected, actual.get(transactionId));
        verify(paymentTemplateValidator, times(1)).validate(anyString());
        verify(paymentContextParser, times(1)).parse(anyString());
        verify(timeGroupReferencePoolImpl, times(2)).get(anyString(), anyLong());
        verify(timeGroupPoolImpl, times(2)).get(anyString(), anyLong());
        verify(ruleCheckingApplier, times(2)).applyForAny(any(PaymentModel.class), anyList());
        verify(ruleCheckingApplier, times(1)).apply(any(PaymentModel.class), anyString());
        verify(ruleCheckingApplier, times(1))
                .applyWithContext(any(PaymentModel.class), anyString(), any(FraudoPaymentParser.ParseContext.class));
        verify(checkedResultFactory, times(0))
                .createNotificationOnlyResultModel(anyString(), anyList());
    }

    @Test
    void checkRuleWithinRulesetDefaultResult() {
        String differentPartyId = UUID.randomUUID().toString();
        String differentShopId = UUID.randomUUID().toString();
        PaymentModel paymentModel = createPaymentModel(ThreadLocalRandom.current().nextLong());
        paymentModel.setPartyId(differentPartyId);
        paymentModel.setShopId(differentShopId);
        FraudoPaymentParser.ParseContext context = new FraudoPaymentParser.ParseContext(null, 0);

        when(paymentTemplateValidator.validate(anyString())).thenReturn(new ArrayList<>());
        when(paymentContextParser.parse(anyString())).thenReturn(context);
        //group template by party
        when(timeGroupReferencePoolImpl.get(differentPartyId, TIMESTAMP)).thenReturn(GROUP_REF_PARTY);
        when(timeGroupPoolImpl.get(GROUP_REF_PARTY, TIMESTAMP)).thenReturn(GROUP_PARTY_TEMPLATE);
        when(ruleCheckingApplier.applyForAny(paymentModel, GROUP_PARTY_TEMPLATE))
                .thenReturn(Optional.of(createNotificationOnlyCheckedResult(GROUP_REF_PARTY_LEVEL)));
        //group template by party-shop key
        String differentPartyShopKey = differentPartyId + "_" + differentShopId;
        when(timeGroupReferencePoolImpl.get(differentPartyShopKey, TIMESTAMP)).thenReturn(GROUP_REF_SHOP);
        when(timeGroupPoolImpl.get(GROUP_REF_SHOP, TIMESTAMP)).thenReturn(GROUP_SHOP_TEMPLATE);
        when(ruleCheckingApplier.applyForAny(paymentModel, GROUP_SHOP_TEMPLATE))
                .thenReturn(Optional.of(createNotificationOnlyCheckedResult(GROUP_REF_SHOP_LEVEL)));
        //template by party (not substituted by template from request)
        when(timeReferencePoolImpl.get(differentPartyId, TIMESTAMP)).thenReturn(PARTY_TEMPLATE);
        when(ruleCheckingApplier.apply(paymentModel, PARTY_TEMPLATE))
                .thenReturn(Optional.of(createNotificationOnlyCheckedResult(TEMPLATE_REF_PARTY_LEVEL)));
        //template by party-shop key (not substituted by template from request)
        when(timeReferencePoolImpl.get(differentPartyShopKey, TIMESTAMP)).thenReturn(SHOP_TEMPLATE);
        when(ruleCheckingApplier.apply(paymentModel, SHOP_TEMPLATE))
                .thenReturn(Optional.of(createNotificationOnlyCheckedResult(TEMPLATE_REF_SHOP_LEVEL)));
        //default result
        List<String> notifications = List.of(
                GROUP_REF_PARTY_LEVEL,
                GROUP_REF_SHOP_LEVEL,
                TEMPLATE_REF_PARTY_LEVEL,
                TEMPLATE_REF_SHOP_LEVEL
        );
        when(checkedResultFactory.createNotificationOnlyResultModel(TEMPLATE, notifications))
                .thenReturn(createDefaultResult(TEMPLATE, notifications));

        String transactionId = UUID.randomUUID().toString();
        Map<String, CheckedResultModel> actual =
                service.checkRuleWithinRuleset(Map.of(transactionId, paymentModel), createCascadingDto(TEMPLATE));

        assertEquals(1, actual.size());
        CheckedResultModel defaultResult = createDefaultResult(TEMPLATE, notifications);
        assertEquals(defaultResult, actual.get(transactionId));
        verify(paymentTemplateValidator, times(1)).validate(anyString());
        verify(paymentContextParser, times(1)).parse(anyString());
        verify(timeReferencePoolImpl, times(2)).get(anyString(), anyLong());
        verify(ruleCheckingApplier, times(2)).apply(any(PaymentModel.class), anyString());
        verify(timeGroupReferencePoolImpl, times(2)).get(anyString(), anyLong());
        verify(timeGroupPoolImpl, times(2)).get(anyString(), anyLong());
        verify(ruleCheckingApplier, times(2)).applyForAny(any(PaymentModel.class), anyList());
        verify(checkedResultFactory, times(1))
                .createNotificationOnlyResultModel(anyString(), anyList());
    }

    @Test
    void checkRuleWithinRulesetDefaultResultNoSetTimestamp() {
        String differentPartyId = UUID.randomUUID().toString();
        String differentShopId = UUID.randomUUID().toString();
        Long differentTimestamp = ThreadLocalRandom.current().nextLong();
        PaymentModel paymentModel = createPaymentModel(ThreadLocalRandom.current().nextLong(), differentTimestamp);
        paymentModel.setPartyId(differentPartyId);
        paymentModel.setShopId(differentShopId);
        FraudoPaymentParser.ParseContext context = new FraudoPaymentParser.ParseContext(null, 0);

        when(paymentTemplateValidator.validate(anyString())).thenReturn(new ArrayList<>());
        when(paymentContextParser.parse(anyString())).thenReturn(context);
        //group template by party
        when(timeGroupReferencePoolImpl.get(differentPartyId, differentTimestamp)).thenReturn(GROUP_REF_PARTY);
        when(timeGroupPoolImpl.get(GROUP_REF_PARTY, differentTimestamp)).thenReturn(GROUP_PARTY_TEMPLATE);
        when(ruleCheckingApplier.applyForAny(paymentModel, GROUP_PARTY_TEMPLATE))
                .thenReturn(Optional.of(createNotificationOnlyCheckedResult(GROUP_REF_PARTY_LEVEL)));
        //group template by party-shop key
        String differentPartyShopKey = differentPartyId + "_" + differentShopId;
        when(timeGroupReferencePoolImpl.get(differentPartyShopKey, differentTimestamp)).thenReturn(GROUP_REF_SHOP);
        when(timeGroupPoolImpl.get(GROUP_REF_SHOP, differentTimestamp)).thenReturn(GROUP_SHOP_TEMPLATE);
        when(ruleCheckingApplier.applyForAny(paymentModel, GROUP_SHOP_TEMPLATE))
                .thenReturn(Optional.of(createNotificationOnlyCheckedResult(GROUP_REF_SHOP_LEVEL)));
        //template by party (not substituted by template from request)
        when(timeReferencePoolImpl.get(differentPartyId, differentTimestamp)).thenReturn(PARTY_TEMPLATE);
        when(ruleCheckingApplier.apply(paymentModel, PARTY_TEMPLATE))
                .thenReturn(Optional.of(createNotificationOnlyCheckedResult(TEMPLATE_REF_PARTY_LEVEL)));
        //template by party-shop key (not substituted by template from request)
        when(timeReferencePoolImpl.get(differentPartyShopKey, differentTimestamp)).thenReturn(SHOP_TEMPLATE);
        when(ruleCheckingApplier.apply(paymentModel, SHOP_TEMPLATE))
                .thenReturn(Optional.of(createNotificationOnlyCheckedResult(TEMPLATE_REF_SHOP_LEVEL)));
        //default result
        List<String> notifications = List.of(
                GROUP_REF_PARTY_LEVEL,
                GROUP_REF_SHOP_LEVEL,
                TEMPLATE_REF_PARTY_LEVEL,
                TEMPLATE_REF_SHOP_LEVEL
        );
        when(checkedResultFactory.createNotificationOnlyResultModel(TEMPLATE, notifications))
                .thenReturn(createDefaultResult(TEMPLATE, notifications));

        String transactionId = UUID.randomUUID().toString();
        Map<String, CheckedResultModel> actual = service.checkRuleWithinRuleset(
                Map.of(transactionId, paymentModel),
                createCascadingDto(TEMPLATE, null)
        );

        assertEquals(1, actual.size());
        CheckedResultModel defaultResult = createDefaultResult(TEMPLATE, notifications);
        assertEquals(defaultResult, actual.get(transactionId));
        verify(paymentTemplateValidator, times(1)).validate(anyString());
        verify(paymentContextParser, times(1)).parse(anyString());
        verify(timeReferencePoolImpl, times(2)).get(anyString(), anyLong());
        verify(ruleCheckingApplier, times(2)).apply(any(PaymentModel.class), anyString());
        verify(timeGroupReferencePoolImpl, times(2)).get(anyString(), anyLong());
        verify(timeGroupPoolImpl, times(2)).get(anyString(), anyLong());
        verify(ruleCheckingApplier, times(2)).applyForAny(any(PaymentModel.class), anyList());
        verify(checkedResultFactory, times(1))
                .createNotificationOnlyResultModel(anyString(), anyList());
    }

    private PaymentModel createPaymentModel(Long amount) {
        return createPaymentModel(amount, TIMESTAMP);
    }

    private PaymentModel createPaymentModel(Long amount, Long timestamp) {
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setAmount(amount);
        paymentModel.setPartyId(PARTY);
        paymentModel.setShopId(SHOP);
        paymentModel.setTimestamp(timestamp);

        return paymentModel;
    }

    private CheckedResultModel createResultModel(ResultStatus resultStatus) {
        return createResultModel(TEMPLATE, resultStatus);
    }

    private CheckedResultModel createResultModel(String template, ResultStatus resultStatus) {
        ConcreteResultModel concreteResultModel = new ConcreteResultModel();
        concreteResultModel.setResultStatus(resultStatus);
        concreteResultModel.setRuleChecked(template);
        concreteResultModel.setNotificationsRule(new ArrayList<>());
        CheckedResultModel checkedResultModel = new CheckedResultModel();
        checkedResultModel.setResultModel(concreteResultModel);

        return checkedResultModel;
    }

    private CheckedResultModel createNotificationOnlyCheckedResult(String level) {
        ConcreteResultModel concreteResultModel = new ConcreteResultModel();
        concreteResultModel.setNotificationsRule(List.of(level));
        concreteResultModel.setResultStatus(ResultStatus.NOTIFY);
        CheckedResultModel checkedResultModel = new CheckedResultModel();
        checkedResultModel.setResultModel(concreteResultModel);
        return checkedResultModel;
    }

    private CascadingTemplateDto createCascadingDto(String template) {
        return createCascadingDto(template, TIMESTAMP);
    }

    private CascadingTemplateDto createCascadingDto(String template, Long timestamp) {
        CascadingTemplateDto dto = new CascadingTemplateDto();
        dto.setTemplate(template);
        dto.setPartyId(PARTY);
        dto.setShopId(SHOP);
        dto.setTimestamp(timestamp);
        return dto;
    }

    private CheckedResultModel createDefaultResult(String template, List<String> notifications) {
        ConcreteResultModel resultModel = new ConcreteResultModel();
        resultModel.setNotificationsRule(notifications);
        CheckedResultModel checkedResultModel = new CheckedResultModel();
        checkedResultModel.setResultModel(resultModel);
        checkedResultModel.setCheckedTemplate(template);
        return checkedResultModel;
    }
}
