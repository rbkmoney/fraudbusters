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

import static com.rbkmoney.fraudbusters.TestObjectsFactory.createCascadingTemplateDto;
import static com.rbkmoney.fraudbusters.TestObjectsFactory.createCheckedResultModel;
import static com.rbkmoney.fraudbusters.TestObjectsFactory.createPaymentModel;
import static com.rbkmoney.fraudbusters.util.BeanUtil.PARTY_ID;
import static com.rbkmoney.fraudbusters.util.BeanUtil.SHOP_ID;
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
    private static final String PARTY_SHOP_KEY = PARTY_ID + "_" + SHOP_ID;

    @Test
    void applySingleRuleThrowsInvalidTemplateException() {
        when(paymentTemplateValidator.validate(TEMPLATE)).thenReturn(List.of("123", "321"));
        assertThrows(InvalidTemplateException.class, () -> service.checkSingleRule(
                Map.of(
                        UUID.randomUUID().toString(), createPaymentModel(0L, TIMESTAMP),
                        UUID.randomUUID().toString(), createPaymentModel(1L, TIMESTAMP)),
                TEMPLATE
        ));
    }

    @Test
    void checkSingleRule() {
        FraudoPaymentParser.ParseContext context = new FraudoPaymentParser.ParseContext(null, 0);
        CheckedResultModel firstResultModel = createCheckedResultModel(ResultStatus.ACCEPT);
        CheckedResultModel secondResultModel = createCheckedResultModel(ResultStatus.DECLINE);
        when(paymentTemplateValidator.validate(anyString())).thenReturn(new ArrayList<>());
        when(paymentContextParser.parse(anyString())).thenReturn(context);
        when(ruleCheckingApplier
                .applyWithContext(any(PaymentModel.class), anyString(), any(FraudoPaymentParser.ParseContext.class))
        )
                .thenReturn(Optional.of(firstResultModel))
                .thenReturn(Optional.of(secondResultModel));

        String firstId = UUID.randomUUID().toString();
        String secondId = UUID.randomUUID().toString();
        PaymentModel firstPaymentModel = createPaymentModel(25L, TIMESTAMP);
        PaymentModel secondPaymentModel = createPaymentModel(2L, TIMESTAMP);
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
                        UUID.randomUUID().toString(), createPaymentModel(0L, TIMESTAMP),
                        UUID.randomUUID().toString(), createPaymentModel(1L, TIMESTAMP)),
                dto
        ));
    }

    @Test
    void checkRuleWithinRulesetGroupTemplateByParty() {
        FraudoPaymentParser.ParseContext context = new FraudoPaymentParser.ParseContext(null, 0);
        PaymentModel paymentModel = createPaymentModel(ThreadLocalRandom.current().nextLong(), TIMESTAMP);

        when(paymentTemplateValidator.validate(anyString())).thenReturn(new ArrayList<>());
        when(paymentContextParser.parse(anyString())).thenReturn(context);
        //group template by party
        when(timeGroupReferencePoolImpl.get(PARTY_ID, TIMESTAMP)).thenReturn(GROUP_REF_PARTY_LEVEL);
        when(timeGroupPoolImpl.get(GROUP_REF_PARTY_LEVEL, TIMESTAMP)).thenReturn(GROUP_PARTY_TEMPLATE);
        when(ruleCheckingApplier.applyForAny(paymentModel, GROUP_PARTY_TEMPLATE, TIMESTAMP))
                .thenReturn(Optional.of(createCheckedResultModel(GROUP_PARTY_TEMPLATE.get(0), ResultStatus.ACCEPT)));


        String transactionId = UUID.randomUUID().toString();
        Map<String, CheckedResultModel> actual = service.checkRuleWithinRuleset(
                Map.of(transactionId, paymentModel),
                createCascadingTemplateDto(TEMPLATE, TIMESTAMP)
        );

        assertEquals(1, actual.size());
        CheckedResultModel expected = createCheckedResultModel(GROUP_PARTY_TEMPLATE.get(0), ResultStatus.ACCEPT);
        assertEquals(expected, actual.get(transactionId));
        verify(paymentTemplateValidator, times(1)).validate(anyString());
        verify(paymentContextParser, times(1)).parse(anyString());
        verify(timeGroupReferencePoolImpl, times(1)).get(anyString(), anyLong());
        verify(timeGroupPoolImpl, times(1)).get(anyString(), anyLong());
        verify(ruleCheckingApplier, times(1))
                .applyForAny(any(PaymentModel.class), anyList(), anyLong());
        verify(checkedResultFactory, times(0))
                .createNotificationOnlyResultModel(anyString(), anyList());
    }

    @Test
    void checkRuleWithinRulesetGroupTemplateByPartyShop() {
        PaymentModel paymentModel = createPaymentModel(ThreadLocalRandom.current().nextLong(), TIMESTAMP);
        FraudoPaymentParser.ParseContext context = new FraudoPaymentParser.ParseContext(null, 0);

        when(paymentTemplateValidator.validate(anyString())).thenReturn(new ArrayList<>());
        when(paymentContextParser.parse(anyString())).thenReturn(context);
        //group template by party
        when(timeGroupReferencePoolImpl.get(PARTY_ID, TIMESTAMP)).thenReturn(GROUP_REF_PARTY);
        when(timeGroupPoolImpl.get(GROUP_REF_PARTY, TIMESTAMP)).thenReturn(GROUP_PARTY_TEMPLATE);
        when(ruleCheckingApplier.applyForAny(paymentModel, GROUP_PARTY_TEMPLATE, TIMESTAMP))
                .thenReturn(Optional.of(createCheckedResultModel(
                        null, null, ResultStatus.NOTIFY, List.of(GROUP_REF_PARTY_LEVEL))));
        //group template by party-shop-key
        when(timeGroupReferencePoolImpl.get(PARTY_SHOP_KEY, TIMESTAMP)).thenReturn(GROUP_REF_SHOP);
        when(timeGroupPoolImpl.get(GROUP_REF_SHOP, TIMESTAMP)).thenReturn(GROUP_SHOP_TEMPLATE);
        when(ruleCheckingApplier.applyForAny(paymentModel, GROUP_SHOP_TEMPLATE, TIMESTAMP))
                .thenReturn(Optional.of(createCheckedResultModel(GROUP_SHOP_TEMPLATE.get(0), ResultStatus.ACCEPT)));


        String transactionId = UUID.randomUUID().toString();
        Map<String, CheckedResultModel> actual = service.checkRuleWithinRuleset(
                Map.of(transactionId, paymentModel),
                createCascadingTemplateDto(TEMPLATE, TIMESTAMP)
        );

        assertEquals(1, actual.size());
        CheckedResultModel expected = createCheckedResultModel(GROUP_SHOP_TEMPLATE.get(0), ResultStatus.ACCEPT);
        expected.getResultModel().setNotificationsRule(List.of(GROUP_REF_PARTY_LEVEL));
        assertEquals(expected, actual.get(transactionId));
        verify(paymentTemplateValidator, times(1)).validate(anyString());
        verify(paymentContextParser, times(1)).parse(anyString());
        verify(timeGroupReferencePoolImpl, times(2)).get(anyString(), anyLong());
        verify(timeGroupPoolImpl, times(2)).get(anyString(), anyLong());
        verify(ruleCheckingApplier, times(2))
                .applyForAny(any(PaymentModel.class), anyList(), anyLong());
        verify(checkedResultFactory, times(0))
                .createNotificationOnlyResultModel(anyString(), anyList());
    }

    @Test
    void checkRuleWithinRulesetTemplateByPartyIdDifferentPartyId() {
        String differentPartyId = UUID.randomUUID().toString();
        PaymentModel paymentModel = createPaymentModel(ThreadLocalRandom.current().nextLong(), TIMESTAMP);
        paymentModel.setPartyId(differentPartyId);
        FraudoPaymentParser.ParseContext context = new FraudoPaymentParser.ParseContext(null, 0);

        when(paymentTemplateValidator.validate(anyString())).thenReturn(new ArrayList<>());
        when(paymentContextParser.parse(anyString())).thenReturn(context);
        //group template by party
        when(timeGroupReferencePoolImpl.get(differentPartyId, TIMESTAMP)).thenReturn(GROUP_REF_PARTY);
        when(timeGroupPoolImpl.get(GROUP_REF_PARTY, TIMESTAMP)).thenReturn(GROUP_PARTY_TEMPLATE);
        when(ruleCheckingApplier.applyForAny(paymentModel, GROUP_PARTY_TEMPLATE, TIMESTAMP))
                .thenReturn(Optional.of(createCheckedResultModel(
                        null, null, ResultStatus.NOTIFY, List.of(GROUP_REF_PARTY_LEVEL))));
        //group template by party-shop-key
        String differentPartyShopKey = differentPartyId + "_" + SHOP_ID;
        when(timeGroupReferencePoolImpl.get(differentPartyShopKey, TIMESTAMP)).thenReturn(GROUP_REF_SHOP);
        when(timeGroupPoolImpl.get(GROUP_REF_SHOP, TIMESTAMP)).thenReturn(GROUP_SHOP_TEMPLATE);
        when(ruleCheckingApplier.applyForAny(paymentModel, GROUP_SHOP_TEMPLATE, TIMESTAMP))
                .thenReturn(Optional.of(createCheckedResultModel(
                        null, null, ResultStatus.NOTIFY, List.of(GROUP_REF_SHOP_LEVEL))));
        //template by party (not substituted by template from request)
        when(timeReferencePoolImpl.get(differentPartyId, TIMESTAMP)).thenReturn(PARTY_TEMPLATE);
        when(ruleCheckingApplier.apply(paymentModel, PARTY_TEMPLATE, TIMESTAMP))
                .thenReturn(Optional.of(createCheckedResultModel(PARTY_TEMPLATE, ResultStatus.ACCEPT)));


        String transactionId = UUID.randomUUID().toString();
        Map<String, CheckedResultModel> actual = service.checkRuleWithinRuleset(
                Map.of(transactionId, paymentModel),
                createCascadingTemplateDto(TEMPLATE, TIMESTAMP)
        );

        assertEquals(1, actual.size());
        CheckedResultModel expected = createCheckedResultModel(PARTY_TEMPLATE, ResultStatus.ACCEPT);
        expected.getResultModel().setNotificationsRule(List.of(
                GROUP_REF_PARTY_LEVEL,
                GROUP_REF_SHOP_LEVEL
        ));
        assertEquals(expected, actual.get(transactionId));
        verify(paymentTemplateValidator, times(1)).validate(anyString());
        verify(paymentContextParser, times(1)).parse(anyString());
        verify(timeReferencePoolImpl, times(1)).get(anyString(), anyLong());
        verify(ruleCheckingApplier, times(1))
                .apply(any(PaymentModel.class), anyString(), anyLong());
        verify(timeGroupReferencePoolImpl, times(2)).get(anyString(), anyLong());
        verify(timeGroupPoolImpl, times(2)).get(anyString(), anyLong());
        verify(ruleCheckingApplier, times(2))
                .applyForAny(any(PaymentModel.class), anyList(), anyLong());
        verify(checkedResultFactory, times(0))
                .createNotificationOnlyResultModel(anyString(), anyList());
    }

    @Test
    void checkRuleWithinRulesetTemplateByPartyShopKeyDifferentPartyShopKey() {
        String differentPartyId = UUID.randomUUID().toString();
        String differentShopId = UUID.randomUUID().toString();
        PaymentModel paymentModel = createPaymentModel(ThreadLocalRandom.current().nextLong(), TIMESTAMP);
        paymentModel.setPartyId(differentPartyId);
        paymentModel.setShopId(differentShopId);
        FraudoPaymentParser.ParseContext context = new FraudoPaymentParser.ParseContext(null, 0);

        when(paymentTemplateValidator.validate(anyString())).thenReturn(new ArrayList<>());
        when(paymentContextParser.parse(anyString())).thenReturn(context);
        //group template by party
        when(timeGroupReferencePoolImpl.get(differentPartyId, TIMESTAMP)).thenReturn(GROUP_REF_PARTY);
        when(timeGroupPoolImpl.get(GROUP_REF_PARTY, TIMESTAMP)).thenReturn(GROUP_PARTY_TEMPLATE);
        when(ruleCheckingApplier.applyForAny(paymentModel, GROUP_PARTY_TEMPLATE, TIMESTAMP))
                .thenReturn(Optional.of(createCheckedResultModel(
                        null, null, ResultStatus.NOTIFY, List.of(GROUP_REF_PARTY_LEVEL))));
        //group template by party-shop key
        String differentPartyShopKey = differentPartyId + "_" + differentShopId;
        when(timeGroupReferencePoolImpl.get(differentPartyShopKey, TIMESTAMP)).thenReturn(GROUP_REF_SHOP);
        when(timeGroupPoolImpl.get(GROUP_REF_SHOP, TIMESTAMP)).thenReturn(GROUP_SHOP_TEMPLATE);
        when(ruleCheckingApplier.applyForAny(paymentModel, GROUP_SHOP_TEMPLATE, TIMESTAMP))
                .thenReturn(Optional.of(createCheckedResultModel(
                        null, null, ResultStatus.NOTIFY, List.of(GROUP_REF_SHOP_LEVEL))));
        //template by party (not substituted by template from request)
        when(timeReferencePoolImpl.get(differentPartyId, TIMESTAMP)).thenReturn(PARTY_TEMPLATE);
        when(ruleCheckingApplier.apply(paymentModel, PARTY_TEMPLATE, TIMESTAMP))
                .thenReturn(Optional.of(createCheckedResultModel(
                        null, null, ResultStatus.NOTIFY, List.of(TEMPLATE_REF_PARTY_LEVEL))));
        //template by party-shop key (not substituted by template from request)
        when(timeReferencePoolImpl.get(differentPartyShopKey, TIMESTAMP)).thenReturn(SHOP_TEMPLATE);
        when(ruleCheckingApplier.apply(paymentModel, SHOP_TEMPLATE, TIMESTAMP))
                .thenReturn(Optional.of(createCheckedResultModel(SHOP_TEMPLATE, ResultStatus.ACCEPT)));


        String transactionId = UUID.randomUUID().toString();
        Map<String, CheckedResultModel> actual = service.checkRuleWithinRuleset(
                Map.of(transactionId, paymentModel),
                createCascadingTemplateDto(TEMPLATE, TIMESTAMP)
        );

        assertEquals(1, actual.size());
        CheckedResultModel expected = createCheckedResultModel(SHOP_TEMPLATE, ResultStatus.ACCEPT);
        expected.getResultModel().setNotificationsRule(List.of(
                GROUP_REF_PARTY_LEVEL,
                GROUP_REF_SHOP_LEVEL,
                TEMPLATE_REF_PARTY_LEVEL
        ));
        assertEquals(expected, actual.get(transactionId));
        verify(paymentTemplateValidator, times(1)).validate(anyString());
        verify(paymentContextParser, times(1)).parse(anyString());
        verify(timeReferencePoolImpl, times(2)).get(anyString(), anyLong());
        verify(ruleCheckingApplier, times(2))
                .apply(any(PaymentModel.class), anyString(), anyLong());
        verify(timeGroupReferencePoolImpl, times(2)).get(anyString(), anyLong());
        verify(timeGroupPoolImpl, times(2)).get(anyString(), anyLong());
        verify(ruleCheckingApplier, times(2))
                .applyForAny(any(PaymentModel.class), anyList(), anyLong());
        verify(checkedResultFactory, times(0))
                .createNotificationOnlyResultModel(anyString(), anyList());
    }

    @Test
    void checkRuleWithinRulesetTemplateByPartyIdSamePartyId() {
        PaymentModel paymentModel = createPaymentModel(ThreadLocalRandom.current().nextLong(), TIMESTAMP);
        FraudoPaymentParser.ParseContext context = new FraudoPaymentParser.ParseContext(null, 0);

        when(paymentTemplateValidator.validate(anyString())).thenReturn(new ArrayList<>());
        when(paymentContextParser.parse(anyString())).thenReturn(context);
        //group template by party
        when(timeGroupReferencePoolImpl.get(PARTY_ID, TIMESTAMP)).thenReturn(GROUP_REF_PARTY);
        when(timeGroupPoolImpl.get(GROUP_REF_PARTY, TIMESTAMP)).thenReturn(GROUP_PARTY_TEMPLATE);
        when(ruleCheckingApplier.applyForAny(paymentModel, GROUP_PARTY_TEMPLATE, TIMESTAMP))
                .thenReturn(Optional.of(createCheckedResultModel(
                        null, null, ResultStatus.NOTIFY, List.of(GROUP_REF_PARTY_LEVEL))));
        //group template by party-shop-key
        when(timeGroupReferencePoolImpl.get(PARTY_SHOP_KEY, TIMESTAMP)).thenReturn(GROUP_REF_SHOP);
        when(timeGroupPoolImpl.get(GROUP_REF_SHOP, TIMESTAMP)).thenReturn(GROUP_SHOP_TEMPLATE);
        when(ruleCheckingApplier.applyForAny(paymentModel, GROUP_SHOP_TEMPLATE, TIMESTAMP))
                .thenReturn(Optional.of(createCheckedResultModel(
                        null, null, ResultStatus.NOTIFY, List.of(GROUP_REF_SHOP_LEVEL))));
        //template by party (substituted by template from request)
        when(ruleCheckingApplier.applyWithContext(paymentModel, TEMPLATE, context))
                .thenReturn(Optional.of(createCheckedResultModel(TEMPLATE, ResultStatus.ACCEPT)));


        String transactionId = UUID.randomUUID().toString();
        Map<String, CheckedResultModel> actual = service.checkRuleWithinRuleset(
                Map.of(transactionId, paymentModel),
                createCascadingTemplateDto(TEMPLATE, TIMESTAMP)
        );

        assertEquals(1, actual.size());
        CheckedResultModel expected = createCheckedResultModel(TEMPLATE, ResultStatus.ACCEPT);
        expected.getResultModel().setNotificationsRule(List.of(GROUP_REF_PARTY_LEVEL, GROUP_REF_SHOP_LEVEL));
        assertEquals(expected, actual.get(transactionId));
        verify(paymentTemplateValidator, times(1)).validate(anyString());
        verify(paymentContextParser, times(1)).parse(anyString());
        verify(timeGroupReferencePoolImpl, times(2)).get(anyString(), anyLong());
        verify(timeGroupPoolImpl, times(2)).get(anyString(), anyLong());
        verify(ruleCheckingApplier, times(2))
                .applyForAny(any(PaymentModel.class), anyList(), anyLong());
        verify(ruleCheckingApplier, times(1))
                .applyWithContext(any(PaymentModel.class), anyString(), any(FraudoPaymentParser.ParseContext.class));
        verify(checkedResultFactory, times(0))
                .createNotificationOnlyResultModel(anyString(), anyList());
    }

    @Test
    void checkRuleWithinRulesetTemplateByPartyShopKeySamePartyShopKey() {
        PaymentModel paymentModel = createPaymentModel(ThreadLocalRandom.current().nextLong(), TIMESTAMP);
        FraudoPaymentParser.ParseContext context = new FraudoPaymentParser.ParseContext(null, 0);

        when(paymentTemplateValidator.validate(anyString())).thenReturn(new ArrayList<>());
        when(paymentContextParser.parse(anyString())).thenReturn(context);
        //group template by party
        when(timeGroupReferencePoolImpl.get(PARTY_ID, TIMESTAMP)).thenReturn(GROUP_REF_PARTY);
        when(timeGroupPoolImpl.get(GROUP_REF_PARTY, TIMESTAMP)).thenReturn(GROUP_PARTY_TEMPLATE);
        when(ruleCheckingApplier.applyForAny(paymentModel, GROUP_PARTY_TEMPLATE, TIMESTAMP))
                .thenReturn(Optional.of(createCheckedResultModel(
                        null, null, ResultStatus.NOTIFY, List.of(GROUP_REF_PARTY_LEVEL))));
        //group template by party-shop key
        when(timeGroupReferencePoolImpl.get(PARTY_SHOP_KEY, TIMESTAMP)).thenReturn(GROUP_REF_SHOP);
        when(timeGroupPoolImpl.get(GROUP_REF_SHOP, TIMESTAMP)).thenReturn(GROUP_SHOP_TEMPLATE);
        when(ruleCheckingApplier.applyForAny(paymentModel, GROUP_SHOP_TEMPLATE, TIMESTAMP))
                .thenReturn(Optional.of(createCheckedResultModel(
                        null, null, ResultStatus.NOTIFY, List.of(GROUP_REF_SHOP_LEVEL))));
        //template by party (not substituted by template from request)
        when(timeReferencePoolImpl.get(PARTY_ID, TIMESTAMP)).thenReturn(PARTY_TEMPLATE);
        when(ruleCheckingApplier.apply(paymentModel, PARTY_TEMPLATE, TIMESTAMP))
                .thenReturn(Optional.of(createCheckedResultModel(
                        null, null, ResultStatus.NOTIFY, List.of(TEMPLATE_REF_PARTY_LEVEL))));
        //template by party-shop key (substituted by template from request)
        when(ruleCheckingApplier.applyWithContext(paymentModel, TEMPLATE, context))
                .thenReturn(Optional.of(createCheckedResultModel(TEMPLATE, ResultStatus.ACCEPT)));


        String transactionId = UUID.randomUUID().toString();
        Map<String, CheckedResultModel> actual = service.checkRuleWithinRuleset(
                Map.of(transactionId, paymentModel),
                createCascadingTemplateDto(TEMPLATE, TIMESTAMP)
        );

        assertEquals(1, actual.size());
        CheckedResultModel expected = createCheckedResultModel(TEMPLATE, ResultStatus.ACCEPT);
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
        verify(ruleCheckingApplier, times(2))
                .applyForAny(any(PaymentModel.class), anyList(), anyLong());
        verify(ruleCheckingApplier, times(1))
                .apply(any(PaymentModel.class), anyString(), anyLong());
        verify(ruleCheckingApplier, times(1))
                .applyWithContext(any(PaymentModel.class), anyString(), any(FraudoPaymentParser.ParseContext.class));
        verify(checkedResultFactory, times(0))
                .createNotificationOnlyResultModel(anyString(), anyList());
    }

    @Test
    void checkRuleWithinRulesetDefaultResult() {
        String differentPartyId = UUID.randomUUID().toString();
        String differentShopId = UUID.randomUUID().toString();
        PaymentModel paymentModel = createPaymentModel(ThreadLocalRandom.current().nextLong(), TIMESTAMP);
        paymentModel.setPartyId(differentPartyId);
        paymentModel.setShopId(differentShopId);
        FraudoPaymentParser.ParseContext context = new FraudoPaymentParser.ParseContext(null, 0);

        when(paymentTemplateValidator.validate(anyString())).thenReturn(new ArrayList<>());
        when(paymentContextParser.parse(anyString())).thenReturn(context);
        //group template by party
        when(timeGroupReferencePoolImpl.get(differentPartyId, TIMESTAMP)).thenReturn(GROUP_REF_PARTY);
        when(timeGroupPoolImpl.get(GROUP_REF_PARTY, TIMESTAMP)).thenReturn(GROUP_PARTY_TEMPLATE);
        when(ruleCheckingApplier.applyForAny(paymentModel, GROUP_PARTY_TEMPLATE, TIMESTAMP))
                .thenReturn(Optional.of(createCheckedResultModel(
                        null, null, ResultStatus.NOTIFY, List.of(GROUP_REF_PARTY_LEVEL))));
        //group template by party-shop key
        String differentPartyShopKey = differentPartyId + "_" + differentShopId;
        when(timeGroupReferencePoolImpl.get(differentPartyShopKey, TIMESTAMP)).thenReturn(GROUP_REF_SHOP);
        when(timeGroupPoolImpl.get(GROUP_REF_SHOP, TIMESTAMP)).thenReturn(GROUP_SHOP_TEMPLATE);
        when(ruleCheckingApplier.applyForAny(paymentModel, GROUP_SHOP_TEMPLATE, TIMESTAMP))
                .thenReturn(Optional.of(createCheckedResultModel(
                        null, null, ResultStatus.NOTIFY, List.of(GROUP_REF_SHOP_LEVEL))));
        //template by party (not substituted by template from request)
        when(timeReferencePoolImpl.get(differentPartyId, TIMESTAMP)).thenReturn(PARTY_TEMPLATE);
        when(ruleCheckingApplier.apply(paymentModel, PARTY_TEMPLATE, TIMESTAMP))
                .thenReturn(Optional.of(createCheckedResultModel(
                        null, null, ResultStatus.NOTIFY, List.of(TEMPLATE_REF_PARTY_LEVEL))));
        //template by party-shop key (not substituted by template from request)
        when(timeReferencePoolImpl.get(differentPartyShopKey, TIMESTAMP)).thenReturn(SHOP_TEMPLATE);
        when(ruleCheckingApplier.apply(paymentModel, SHOP_TEMPLATE, TIMESTAMP))
                .thenReturn(Optional.of(createCheckedResultModel(
                        null, null, ResultStatus.NOTIFY, List.of(TEMPLATE_REF_SHOP_LEVEL))));
        //default result
        List<String> notifications = List.of(
                GROUP_REF_PARTY_LEVEL,
                GROUP_REF_SHOP_LEVEL,
                TEMPLATE_REF_PARTY_LEVEL,
                TEMPLATE_REF_SHOP_LEVEL
        );
        when(checkedResultFactory.createNotificationOnlyResultModel(TEMPLATE, notifications))
                .thenReturn(createCheckedResultModel(TEMPLATE, null, null, notifications));

        String transactionId = UUID.randomUUID().toString();
        Map<String, CheckedResultModel> actual = service.checkRuleWithinRuleset(
                Map.of(transactionId, paymentModel),
                createCascadingTemplateDto(TEMPLATE, TIMESTAMP)
        );

        assertEquals(1, actual.size());
        CheckedResultModel defaultResult =
                createCheckedResultModel(TEMPLATE, null, null, notifications);
        assertEquals(defaultResult, actual.get(transactionId));
        verify(paymentTemplateValidator, times(1)).validate(anyString());
        verify(paymentContextParser, times(1)).parse(anyString());
        verify(timeReferencePoolImpl, times(2)).get(anyString(), anyLong());
        verify(ruleCheckingApplier, times(2))
                .apply(any(PaymentModel.class), anyString(), anyLong());
        verify(timeGroupReferencePoolImpl, times(2)).get(anyString(), anyLong());
        verify(timeGroupPoolImpl, times(2)).get(anyString(), anyLong());
        verify(ruleCheckingApplier, times(2))
                .applyForAny(any(PaymentModel.class), anyList(), anyLong());
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
        when(ruleCheckingApplier.applyForAny(paymentModel, GROUP_PARTY_TEMPLATE, differentTimestamp))
                .thenReturn(Optional.of(createCheckedResultModel(
                        null, null, ResultStatus.NOTIFY, List.of(GROUP_REF_PARTY_LEVEL))));
        //group template by party-shop key
        String differentPartyShopKey = differentPartyId + "_" + differentShopId;
        when(timeGroupReferencePoolImpl.get(differentPartyShopKey, differentTimestamp)).thenReturn(GROUP_REF_SHOP);
        when(timeGroupPoolImpl.get(GROUP_REF_SHOP, differentTimestamp)).thenReturn(GROUP_SHOP_TEMPLATE);
        when(ruleCheckingApplier.applyForAny(paymentModel, GROUP_SHOP_TEMPLATE, differentTimestamp))
                .thenReturn(Optional.of(createCheckedResultModel(
                        null, null, ResultStatus.NOTIFY, List.of(GROUP_REF_SHOP_LEVEL))));
        //template by party (not substituted by template from request)
        when(timeReferencePoolImpl.get(differentPartyId, differentTimestamp)).thenReturn(PARTY_TEMPLATE);
        when(ruleCheckingApplier.apply(paymentModel, PARTY_TEMPLATE, differentTimestamp))
                .thenReturn(Optional.of(createCheckedResultModel(
                        null, null, ResultStatus.NOTIFY, List.of(TEMPLATE_REF_PARTY_LEVEL))));
        //template by party-shop key (not substituted by template from request)
        when(timeReferencePoolImpl.get(differentPartyShopKey, differentTimestamp)).thenReturn(SHOP_TEMPLATE);
        when(ruleCheckingApplier.apply(paymentModel, SHOP_TEMPLATE, differentTimestamp))
                .thenReturn(Optional.of(createCheckedResultModel(
                        null, null, ResultStatus.NOTIFY, List.of(TEMPLATE_REF_SHOP_LEVEL))));
        //default result
        List<String> notifications = List.of(
                GROUP_REF_PARTY_LEVEL,
                GROUP_REF_SHOP_LEVEL,
                TEMPLATE_REF_PARTY_LEVEL,
                TEMPLATE_REF_SHOP_LEVEL
        );
        when(checkedResultFactory.createNotificationOnlyResultModel(TEMPLATE, notifications))
                .thenReturn(createCheckedResultModel(TEMPLATE, null, null, notifications));

        String transactionId = UUID.randomUUID().toString();
        Map<String, CheckedResultModel> actual = service.checkRuleWithinRuleset(
                Map.of(transactionId, paymentModel),
                createCascadingTemplateDto(TEMPLATE, null)
        );

        assertEquals(1, actual.size());
        CheckedResultModel defaultResult =
                createCheckedResultModel(TEMPLATE, null, null, notifications);
        assertEquals(defaultResult, actual.get(transactionId));
        verify(paymentTemplateValidator, times(1)).validate(anyString());
        verify(paymentContextParser, times(1)).parse(anyString());
        verify(timeReferencePoolImpl, times(2)).get(anyString(), anyLong());
        verify(ruleCheckingApplier, times(2))
                .apply(any(PaymentModel.class), anyString(), anyLong());
        verify(timeGroupReferencePoolImpl, times(2)).get(anyString(), anyLong());
        verify(timeGroupPoolImpl, times(2)).get(anyString(), anyLong());
        verify(ruleCheckingApplier, times(2))
                .applyForAny(any(PaymentModel.class), anyList(), anyLong());
        verify(checkedResultFactory, times(1))
                .createNotificationOnlyResultModel(anyString(), anyList());
    }
}
