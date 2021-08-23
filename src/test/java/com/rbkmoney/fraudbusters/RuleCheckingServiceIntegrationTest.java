package com.rbkmoney.fraudbusters;

import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import com.rbkmoney.fraudbusters.fraud.FraudContextParser;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.pool.HistoricalPool;
import com.rbkmoney.fraudbusters.service.RuleCheckingServiceImpl;
import com.rbkmoney.fraudbusters.service.dto.CascadingTemplateDto;
import com.rbkmoney.fraudbusters.util.ReferenceKeyGenerator;
import com.rbkmoney.fraudo.FraudoPaymentParser;
import com.rbkmoney.fraudo.constant.ResultStatus;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.ParserRuleContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.rbkmoney.fraudbusters.TestObjectsFactory.createPaymentModel;
import static com.rbkmoney.fraudbusters.util.BeanUtil.PARTY_ID;
import static com.rbkmoney.fraudbusters.util.BeanUtil.SHOP_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Slf4j
@ActiveProfiles("full-prod")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = FraudBustersApplication.class,
        properties = {"kafka.listen.result.concurrency=1", "kafka.historical.listener.enable=true"})
public class RuleCheckingServiceIntegrationTest extends JUnit5IntegrationTest {

    @Autowired
    private RuleCheckingServiceImpl ruleTestingService;

    @Autowired
    private HistoricalPool<List<String>> timeGroupPoolImpl;

    @Autowired
    private HistoricalPool<String> timeReferencePoolImpl;

    @Autowired
    private HistoricalPool<String> timeGroupReferencePoolImpl;

    @Autowired
    private HistoricalPool<ParserRuleContext> timeTemplatePoolImpl;

    @Autowired
    private FraudContextParser<FraudoPaymentParser.ParseContext> paymentContextParser;

    private static final String FIRST_GROUP_TEMPLATE_PARTY_KEY = "FIRST_GROUP_TEMPLATE_PARTY_KEY";
    private static final String SECOND_GROUP_TEMPLATE_PARTY_KEY = "SECOND_GROUP_TEMPLATE_PARTY_KEY";
    private static final String FIRST_GROUP_TEMPLATE_SHOP_KEY = "FIRST_GROUP_TEMPLATE_SHOP_KEY";
    private static final String SECOND_GROUP_TEMPLATE_SHOP_KEY = "SECOND_GROUP_TEMPLATE_SHOP_KEY";
    private static final String TEMPLATE_PARTY_KEY = "TEMPLATE_PARTY_KEY";
    private static final String TEMPLATE_SHOP_KEY = "TEMPLATE_SHOP_KEY";
    private static final String PREVIOUS_TEMPLATE_PARTY_KEY = "PREVIOUS_TEMPLATE_PARTY_KEY";
    private static final String PREVIOUS_TEMPLATE_SHOP_KEY = "PREVIOUS_TEMPLATE_SHOP_KEY";
    private static final String FIRST_GROUP_TEMPLATE_PARTY = "rule: amount() > 110 \n" +
            "-> accept;";
    private static final String SECOND_GROUP_TEMPLATE_PARTY = "rule: amount() > 100 \n" +
            "-> accept;";
    private static final String FIRST_GROUP_TEMPLATE_SHOP = "rule: amount() > 85 \n" +
            "-> accept;";
    private static final String SECOND_GROUP_TEMPLATE_SHOP = "rule: amount() > 80 \n" +
            "-> accept;";
    private static final String TEMPLATE = "rule: amount() > 5 \n" +
            "-> accept;";
    private static final String TEMPLATE_PARTY = "rule: amount() > 60 \n" +
            "-> accept;";
    private static final String TEMPLATE_SHOP = "rule: amount() > 55 \n" +
            "-> accept;";
    private static final String PREVIOUS_TEMPLATE_PARTY = "rule: amount() > 200 \n" +
            "-> accept;";
    private static final String PREVIOUS_TEMPLATE_SHOP = "rule: amount() > 100 \n" +
            "-> accept;";
    private static final Instant now = Instant.now();
    private static final long TIMESTAMP = now.toEpochMilli();
    private static final long RULE_TIMESTAMP = TIMESTAMP - 1_000;
    private static final long PREVIOUS_TIMESTAMP = RULE_TIMESTAMP - 1_000;
    private static final long PREVIOUS_RULE_TIMESTAMP = PREVIOUS_TIMESTAMP - 1_000;
    private static final String GROUP_REF_PARTY = "GROUP_REF_PARTY";
    private static final String GROUP_REF_SHOP = "GROUP_REF_SHOP";
    private static final String PARTY_SHOP_KEY = ReferenceKeyGenerator.generateTemplateKey(PARTY_ID, SHOP_ID);

    private static final String RULE_CHECKED = "0";

    @Override
    @BeforeEach
    void setUp() {
        timeGroupPoolImpl.keySet()
                .forEach(key -> timeGroupPoolImpl.remove(key, null));
        timeReferencePoolImpl.keySet()
                .forEach(key -> timeReferencePoolImpl.remove(key, null));
        timeGroupReferencePoolImpl.keySet()
                .forEach(key -> timeGroupReferencePoolImpl.remove(key, null));
        timeTemplatePoolImpl.keySet()
                .forEach(key -> timeTemplatePoolImpl.remove(key, null));
    }

    @Test
    void applyOneRuleOnly() {
        PaymentModel firstTransaction = createPaymentModel(25L);
        PaymentModel secondTransaction = createPaymentModel(2L);
        String firstTransactionId = UUID.randomUUID().toString();
        String secondTransactionId = UUID.randomUUID().toString();

        Map<String, CheckedResultModel> result = ruleTestingService.checkSingleRule(
                Map.of(firstTransactionId, firstTransaction,
                        secondTransactionId, secondTransaction),
                TEMPLATE
        );

        assertEquals(2, result.size());
        CheckedResultModel firstCheckedResult = result.get(firstTransactionId);
        assertEquals(TEMPLATE, firstCheckedResult.getCheckedTemplate());
        assertEquals(ResultStatus.ACCEPT, firstCheckedResult.getResultModel().getResultStatus());
        assertEquals(RULE_CHECKED, firstCheckedResult.getResultModel().getRuleChecked());
        assertEquals(new ArrayList<>(), firstCheckedResult.getResultModel().getNotificationsRule());
        CheckedResultModel secondCheckedResult = result.get(secondTransactionId);
        assertNotNull(secondCheckedResult.getResultModel());
        assertNull(secondCheckedResult.getResultModel().getResultStatus());
        assertNull(secondCheckedResult.getResultModel().getRuleChecked());
        assertEquals(new ArrayList<>(), secondCheckedResult.getResultModel().getNotificationsRule());
    }

    @Test
    void applyRuleWithinRulesetNoTimestampDifferentPartyShop() {
        // single templates
        addPartyAndShopTemplateRules();
        addTemplateRule(PREVIOUS_TEMPLATE_PARTY, PREVIOUS_TEMPLATE_PARTY_KEY, PARTY_ID, PREVIOUS_RULE_TIMESTAMP);
        addTemplateRule(PREVIOUS_TEMPLATE_SHOP, PREVIOUS_TEMPLATE_SHOP_KEY, PARTY_SHOP_KEY, PREVIOUS_RULE_TIMESTAMP);

        String partyTransactionId = UUID.randomUUID().toString();
        String partyShopTransactionId = UUID.randomUUID().toString();
        String previousPartyTransactionId = UUID.randomUUID().toString();
        String previousPartyShopTransactionId = UUID.randomUUID().toString();

        CascadingTemplateDto dto = new CascadingTemplateDto();
        dto.setTemplate(TEMPLATE);
        dto.setPartyId(UUID.randomUUID().toString());
        dto.setShopId(UUID.randomUUID().toString());

        Map<String, CheckedResultModel> actual = ruleTestingService.checkRuleWithinRuleset(
                Map.of(
                        partyTransactionId, createPaymentModel(65L, TIMESTAMP),
                        partyShopTransactionId, createPaymentModel(57L, TIMESTAMP),
                        previousPartyTransactionId, createPaymentModel(210L, PREVIOUS_TIMESTAMP),
                        previousPartyShopTransactionId, createPaymentModel(110L, PREVIOUS_TIMESTAMP)
                ),
                dto
        );
        assertEquals(4, actual.size());
        assertEquals(TEMPLATE_PARTY_KEY, actual.get(partyTransactionId).getCheckedTemplate());
        assertEquals(ResultStatus.ACCEPT, actual.get(partyTransactionId).getResultModel().getResultStatus());
        assertEquals(TEMPLATE_SHOP_KEY, actual.get(partyShopTransactionId).getCheckedTemplate());
        assertEquals(ResultStatus.ACCEPT, actual.get(partyShopTransactionId).getResultModel().getResultStatus());
        assertEquals(PREVIOUS_TEMPLATE_PARTY_KEY, actual.get(previousPartyTransactionId).getCheckedTemplate());
        assertEquals(ResultStatus.ACCEPT, actual.get(previousPartyTransactionId).getResultModel().getResultStatus());
        assertEquals(PREVIOUS_TEMPLATE_SHOP_KEY, actual.get(previousPartyShopTransactionId).getCheckedTemplate());
        assertEquals(
                ResultStatus.ACCEPT,
                actual.get(previousPartyShopTransactionId).getResultModel().getResultStatus()
        );
    }

    @Test
    void applyRuleWithinRulesetGroupRules() {
        addPartyAndShopGroupTemplateRules();

        String firstPartyTransaction = UUID.randomUUID().toString();
        String secondPartyTransaction = UUID.randomUUID().toString();
        String firstPartyShopTransaction = UUID.randomUUID().toString();
        String secondPartyShopTransaction = UUID.randomUUID().toString();

        CascadingTemplateDto dto = new CascadingTemplateDto();
        dto.setTemplate(TEMPLATE);
        dto.setPartyId(PARTY_ID);
        dto.setShopId(SHOP_ID);
        dto.setTimestamp(TIMESTAMP);

        Map<String, CheckedResultModel> actual = ruleTestingService.checkRuleWithinRuleset(
                Map.of(
                        firstPartyTransaction, createPaymentModel(115L, TIMESTAMP),
                        secondPartyTransaction, createPaymentModel(105L, TIMESTAMP),
                        firstPartyShopTransaction, createPaymentModel(90L, PREVIOUS_TIMESTAMP),
                        secondPartyShopTransaction, createPaymentModel(83L, PREVIOUS_TIMESTAMP)
                ),
                dto
        );
        assertEquals(4, actual.size());
        assertEquals(FIRST_GROUP_TEMPLATE_PARTY_KEY, actual.get(firstPartyTransaction).getCheckedTemplate());
        assertEquals(ResultStatus.ACCEPT, actual.get(firstPartyTransaction).getResultModel().getResultStatus());
        assertEquals(SECOND_GROUP_TEMPLATE_PARTY_KEY, actual.get(secondPartyTransaction).getCheckedTemplate());
        assertEquals(ResultStatus.ACCEPT, actual.get(secondPartyTransaction).getResultModel().getResultStatus());
        assertEquals(FIRST_GROUP_TEMPLATE_SHOP_KEY, actual.get(firstPartyShopTransaction).getCheckedTemplate());
        assertEquals(ResultStatus.ACCEPT, actual.get(firstPartyShopTransaction).getResultModel().getResultStatus());
        assertEquals(SECOND_GROUP_TEMPLATE_SHOP_KEY, actual.get(secondPartyShopTransaction).getCheckedTemplate());
        assertEquals(
                ResultStatus.ACCEPT,
                actual.get(secondPartyShopTransaction).getResultModel().getResultStatus()
        );
    }

    @Test
    void applyRuleWithinRulesetChangeTemplateByParty() {
        addPartyAndShopTemplateRules();
        addPartyAndShopGroupTemplateRules();

        String checkTemplateTransactionId = UUID.randomUUID().toString();

        CascadingTemplateDto dto = new CascadingTemplateDto();
        dto.setTemplate(TEMPLATE);
        dto.setPartyId(PARTY_ID);
        dto.setTimestamp(TIMESTAMP);

        Map<String, CheckedResultModel> actual = ruleTestingService.checkRuleWithinRuleset(
                Map.of(checkTemplateTransactionId, createPaymentModel(10L, RULE_TIMESTAMP)),
                dto
        );
        assertEquals(1, actual.size());
        assertEquals(TEMPLATE, actual.get(checkTemplateTransactionId).getCheckedTemplate());
        assertEquals(ResultStatus.ACCEPT, actual.get(checkTemplateTransactionId).getResultModel().getResultStatus());
    }

    @Test
    void applyRuleWithinRulesetChangeTemplateByShop() {
        addPartyAndShopTemplateRules();
        addPartyAndShopGroupTemplateRules();

        String checkTemplateTransactionId = UUID.randomUUID().toString();

        CascadingTemplateDto dto = new CascadingTemplateDto();
        dto.setTemplate(TEMPLATE);
        dto.setPartyId(PARTY_ID);
        dto.setShopId(SHOP_ID);
        dto.setTimestamp(TIMESTAMP);

        Map<String, CheckedResultModel> actual = ruleTestingService.checkRuleWithinRuleset(
                Map.of(checkTemplateTransactionId, createPaymentModel(10L, RULE_TIMESTAMP)),
                dto
        );
        assertEquals(1, actual.size());
        assertEquals(TEMPLATE, actual.get(checkTemplateTransactionId).getCheckedTemplate());
        assertEquals(ResultStatus.ACCEPT, actual.get(checkTemplateTransactionId).getResultModel().getResultStatus());
    }

    @Test
    void applyRuleWithinRulesetOnlyRuleFromDtoOnPartyShopLevel() {
        String checkTemplateTransactionId = UUID.randomUUID().toString();

        CascadingTemplateDto dto = new CascadingTemplateDto();
        dto.setTemplate(TEMPLATE);
        dto.setPartyId(PARTY_ID);
        dto.setShopId(SHOP_ID);
        dto.setTimestamp(TIMESTAMP);

        Map<String, CheckedResultModel> actual = ruleTestingService.checkRuleWithinRuleset(
                Map.of(checkTemplateTransactionId, createPaymentModel(10L, RULE_TIMESTAMP)),
                dto
        );
        assertEquals(1, actual.size());
        assertEquals(TEMPLATE, actual.get(checkTemplateTransactionId).getCheckedTemplate());
        assertEquals(ResultStatus.ACCEPT, actual.get(checkTemplateTransactionId).getResultModel().getResultStatus());
    }

    @Test
    void applyRuleWithinRulesetDefaultResult() {
        // single templates
        timeReferencePoolImpl.add(PARTY_ID, RULE_TIMESTAMP, TEMPLATE_PARTY);
        timeReferencePoolImpl.add(PARTY_SHOP_KEY, RULE_TIMESTAMP, TEMPLATE_SHOP);

        //groups of rules
        timeGroupReferencePoolImpl.add(PARTY_ID, RULE_TIMESTAMP, GROUP_REF_PARTY);
        timeGroupReferencePoolImpl.add(PARTY_SHOP_KEY, RULE_TIMESTAMP, GROUP_REF_SHOP);
        timeGroupPoolImpl.add(GROUP_REF_PARTY, RULE_TIMESTAMP,
                List.of(FIRST_GROUP_TEMPLATE_PARTY, SECOND_GROUP_TEMPLATE_PARTY));
        timeGroupPoolImpl.add(GROUP_REF_SHOP, RULE_TIMESTAMP,
                List.of(FIRST_GROUP_TEMPLATE_SHOP, SECOND_GROUP_TEMPLATE_SHOP));

        String checkTemplateTransactionId = UUID.randomUUID().toString();

        CascadingTemplateDto dto = new CascadingTemplateDto();
        dto.setTemplate(TEMPLATE);
        dto.setPartyId(PARTY_ID);
        dto.setShopId(SHOP_ID);
        dto.setTimestamp(TIMESTAMP);

        Map<String, CheckedResultModel> actual = ruleTestingService.checkRuleWithinRuleset(
                Map.of(
                        checkTemplateTransactionId, createPaymentModel(-5L, RULE_TIMESTAMP)
                ),
                dto
        );

        assertEquals(1, actual.size());
        assertEquals(TEMPLATE, actual.get(checkTemplateTransactionId).getCheckedTemplate());
        assertNull(actual.get(checkTemplateTransactionId).getResultModel().getResultStatus());
    }

    private void addPartyAndShopTemplateRules() {
        addTemplateRule(TEMPLATE_PARTY, TEMPLATE_PARTY_KEY, PARTY_ID, RULE_TIMESTAMP);
        addTemplateRule(TEMPLATE_SHOP, TEMPLATE_SHOP_KEY, PARTY_SHOP_KEY, RULE_TIMESTAMP);
    }

    private void addPartyAndShopGroupTemplateRules() {
        addGroupRule(
                List.of(FIRST_GROUP_TEMPLATE_PARTY, SECOND_GROUP_TEMPLATE_PARTY),
                List.of(FIRST_GROUP_TEMPLATE_PARTY_KEY, SECOND_GROUP_TEMPLATE_PARTY_KEY),
                GROUP_REF_PARTY,
                PARTY_ID,
                RULE_TIMESTAMP
        );
        addGroupRule(
                List.of(FIRST_GROUP_TEMPLATE_SHOP, SECOND_GROUP_TEMPLATE_SHOP),
                List.of(FIRST_GROUP_TEMPLATE_SHOP_KEY, SECOND_GROUP_TEMPLATE_SHOP_KEY),
                GROUP_REF_SHOP,
                PARTY_SHOP_KEY,
                RULE_TIMESTAMP
        );
    }

    private void addTemplateRule(String template, String templateKey, String refKey, Long timestamp) {
        timeReferencePoolImpl.add(refKey, timestamp, templateKey);
        timeTemplatePoolImpl.add(templateKey, timestamp, paymentContextParser.parse(template));
    }

    private void addGroupRule(List<String> templates, List<String> templateKeys, String groupRefKey, String groupKey,
                              Long timestamp) {
        timeGroupReferencePoolImpl.add(groupKey, timestamp, groupRefKey);
        timeGroupPoolImpl.add(groupRefKey, timestamp, templateKeys);
        for (int i = 0; i < templates.size(); i++) {
            timeTemplatePoolImpl.add(templateKeys.get(i), timestamp, paymentContextParser.parse(templates.get(i)));
        }
    }

}
