package com.rbkmoney.fraudbusters;

import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.service.RuleCheckingServiceImpl;
import com.rbkmoney.fraudo.constant.ResultStatus;
import com.rbkmoney.fraudo.model.ResultModel;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;
import java.util.UUID;

import static com.rbkmoney.fraudbusters.util.BeanUtil.createPaymentModel;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Slf4j
@ActiveProfiles("full-prod")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = FraudBustersApplication.class,
        properties = {"kafka.listen.result.concurrency=1", "kafka.historical.listener.enable=true"})
public class RuleCheckingServiceIntegrationTest extends JUnit5IntegrationTest {

    @Autowired
    private RuleCheckingServiceImpl ruleTestingService;

    private static final String TEMPLATE = "rule: amount() > 10 \n" +
            "-> accept;";
    private static final String RULE_CHECKED = "0";

    @Test
    void applyOneRuleOnly() {
        PaymentModel firstTransaction = createPaymentModel();
        firstTransaction.setAmount(25L);
        PaymentModel secondTransaction = createPaymentModel();
        secondTransaction.setAmount(2L);
        String firstTransactionId = UUID.randomUUID().toString();
        String secondTransactionId = UUID.randomUUID().toString();

        Map<String, ResultModel> result = ruleTestingService.checkSingleRule(
                Map.of(firstTransactionId, firstTransaction,
                        secondTransactionId, secondTransaction),
                TEMPLATE
        );

        assertEquals(2, result.size());
        assertEquals(1, result.get(firstTransactionId).getRuleResults().size());
        assertEquals(ResultStatus.ACCEPT, result.get(firstTransactionId).getRuleResults().get(0).getResultStatus());
        assertEquals(RULE_CHECKED, result.get(firstTransactionId).getRuleResults().get(0).getRuleChecked());
        assertTrue(result.get(secondTransactionId).getRuleResults().isEmpty());
    }

}
