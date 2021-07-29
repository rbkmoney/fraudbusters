package com.rbkmoney.fraudbusters;

import com.rbkmoney.damsel.domain.BankCard;
import com.rbkmoney.damsel.domain.Cash;
import com.rbkmoney.damsel.domain.CurrencyRef;
import com.rbkmoney.damsel.domain.PaymentTool;
import com.rbkmoney.damsel.fraudbusters.ClientInfo;
import com.rbkmoney.damsel.fraudbusters.HistoricalTransactionCheck;
import com.rbkmoney.damsel.fraudbusters.MerchantInfo;
import com.rbkmoney.damsel.fraudbusters.Payment;
import com.rbkmoney.damsel.fraudbusters.ReferenceInfo;
import com.rbkmoney.damsel.fraudbusters.Template;
import com.rbkmoney.fraudbusters.service.HistoricalDataServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Slf4j
@ActiveProfiles("full-prod")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = FraudBustersApplication.class,
        properties = {"kafka.listen.result.concurrency=1", "kafka.historical.listener.enable=true"})
public class HistoricalServiceIntegrationTest extends JUnit5IntegrationTest {

    @Autowired
    private HistoricalDataServiceImpl historicalDataService;

    private static final String TEMPLATE_ID = UUID.randomUUID().toString();
    private static final String TEMPLATE = "rule: amount() > 10 \n" +
            "-> accept;";

    @Test
    public void applyOneRuleOnly() {
        Template template = new Template();
        template.setId(TEMPLATE_ID);
        template.setTemplate(TEMPLATE.getBytes(StandardCharsets.UTF_8));

        Payment bigAmountTransaction = new Payment();
        bigAmountTransaction.setId(UUID.randomUUID().toString());
        bigAmountTransaction.setEventTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        Cash cost = new Cash();
        cost.setAmount(25L);
        cost.setCurrency(new CurrencyRef("RUB"));
        bigAmountTransaction.setCost(cost);
        BankCard bankCard = new BankCard();
        bankCard.setToken("card_token");
        PaymentTool paymentTool = new PaymentTool();
        paymentTool.setBankCard(bankCard);
        bigAmountTransaction.setPaymentTool(paymentTool);
        MerchantInfo merchantInfo = new MerchantInfo();
        merchantInfo.setPartyId(UUID.randomUUID().toString());
        merchantInfo.setShopId(UUID.randomUUID().toString());
        ReferenceInfo referenceInfo = new ReferenceInfo();
        referenceInfo.setMerchantInfo(merchantInfo);
        bigAmountTransaction.setReferenceInfo(referenceInfo);
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setEmail("test@email.org");
        clientInfo.setIp("127.0.0.1");
        clientInfo.setFingerprint(UUID.randomUUID().toString());
        bigAmountTransaction.setClientInfo(clientInfo);

        Payment smallAmountTransaction = new Payment(bigAmountTransaction);
        smallAmountTransaction.getCost().setAmount(2L);

        Set<HistoricalTransactionCheck> historicalTransactionChecks =
                historicalDataService.applySingleRule(template, Set.of(bigAmountTransaction, smallAmountTransaction));

        assertEquals(2, historicalTransactionChecks.size());
        HistoricalTransactionCheck acceptedCheck =
                findInSetByPayment(historicalTransactionChecks, bigAmountTransaction);
        assertTrue(acceptedCheck.getCheckResult().getConcreteCheckResult().getResultStatus().isSetAccept());

        HistoricalTransactionCheck notAcceptedCheck =
                findInSetByPayment(historicalTransactionChecks, smallAmountTransaction);
        assertNull(notAcceptedCheck.getCheckResult().getConcreteCheckResult());
    }

    private HistoricalTransactionCheck findInSetByPayment(
            Set<HistoricalTransactionCheck> checks,
            Payment payment
    ) {
        return checks.stream()
                .filter(check -> check.getTransaction().equals(payment))
                .findFirst()
                .orElseThrow();
    }
}
