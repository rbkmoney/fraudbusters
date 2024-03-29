package com.rbkmoney.fraudbusters;

import com.rbkmoney.damsel.fraudbusters.Payment;
import com.rbkmoney.damsel.fraudbusters.PaymentServiceSrv;
import com.rbkmoney.damsel.fraudbusters.PaymentStatus;
import com.rbkmoney.damsel.fraudbusters.WithdrawalStatus;
import com.rbkmoney.fraudbusters.constant.EventSource;
import com.rbkmoney.fraudo.constant.ResultStatus;
import com.rbkmoney.woody.thrift.impl.http.THClientBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static com.rbkmoney.fraudbusters.util.BeanUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Slf4j
@ActiveProfiles("full-prod")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = FraudBustersApplication.class,
        properties = {"kafka.listen.result.concurrency=1", "kafka.historical.listener.enable=true",
                "kafka.aggr.payment.min.bytes=1"})
class LoadDataIntegrationTest extends JUnit5IntegrationTest {

    public static final String PAYMENT_1 = "payment_1";
    public static final String PAYMENT_2 = "payment_2";
    public static final String PAYMENT_0 = "payment_0";

    private static final String TEMPLATE = """
            rule:TEMPLATE: sum("card_token", 1000, "party_id", "shop_id", "mobile") > 0
             and unique("email", "ip", 1444, "recurrent") < 2 and isRecurrent() == false
             and count("card_token", 1000, "party_id", "shop_id") > 5  -> decline
            """;
    private static final String TEMPLATE_2 =
            "rule:TEMPLATE: count(\"card_token\", 1000, \"party_id\", \"shop_id\") > 2  -> decline;";
    private static final String TEMPLATE_CONCRETE =
            "rule:TEMPLATE_CONCRETE: count(\"card_token\", 10) > 0  -> accept;";
    private final String globalRef = UUID.randomUUID().toString();

    @Autowired
    JdbcTemplate jdbcTemplate;

    @LocalServerPort
    int serverPort;

    @BeforeEach
    public void init() throws ExecutionException, InterruptedException, TException {
        produceTemplate(globalRef, TEMPLATE, kafkaTopics.getFullTemplate());
        produceReference(true, null, null, globalRef);
        waitingTopic(kafkaTopics.getFullTemplate());
        Mockito.when(geoIpServiceSrv.getLocationIsoCode(any())).thenReturn("RUS");
    }

    @Test
    @SneakyThrows
    public void testLoadData() {
        final String oldTime = String.valueOf(LocalDateTime.now());
        produceTemplate(globalRef, TEMPLATE_2, kafkaTopics.getFullTemplate());
        Thread.sleep(TIMEOUT);

        THClientBuilder clientBuilder = new THClientBuilder()
                .withAddress(new URI(String.format("http://localhost:%s/fraud_payment_validator/v1/", serverPort)))
                .withNetworkTimeout(300000);
        PaymentServiceSrv.Iface client = clientBuilder.build(PaymentServiceSrv.Iface.class);

        checkInsertingBatch(client);

        Payment payment = createPayment(PaymentStatus.processed);
        payment.setId(PAYMENT_1);
        insertWithTimeout(client, List.of(payment));
        insertListDefaultPayments(client, PaymentStatus.captured, PaymentStatus.failed);
        checkPayment(PAYMENT_1, ResultStatus.DECLINE, 1);

        //check in past
        payment.setId(PAYMENT_0);
        payment.setEventTime(oldTime);
        insertWithTimeout(client, payment);
        checkPayment(PAYMENT_0, ResultStatus.THREE_DS, 1);

        String localId = UUID.randomUUID().toString();
        produceTemplate(localId, TEMPLATE_CONCRETE, kafkaTopics.getFullTemplate());
        produceReference(true, null, null, localId);
        Thread.sleep(TIMEOUT);

        payment.setId(PAYMENT_2);
        payment.setEventTime(String.valueOf(LocalDateTime.now()));
        insertWithTimeout(client, payment);
        checkPayment(PAYMENT_2, ResultStatus.ACCEPT, 1);

        //Chargeback
        client.insertChargebacks(List.of(
                createChargeback(com.rbkmoney.damsel.fraudbusters.ChargebackStatus.accepted),
                createChargeback(com.rbkmoney.damsel.fraudbusters.ChargebackStatus.cancelled)
        ));
        Thread.sleep(TIMEOUT);

        List<Map<String, Object>> maps =
                jdbcTemplate.queryForList("SELECT * from " + EventSource.FRAUD_EVENTS_CHARGEBACK.getTable());
        assertEquals(2, maps.size());

        //Refund
        client.insertRefunds(List.of(
                createRefund(com.rbkmoney.damsel.fraudbusters.RefundStatus.succeeded),
                createRefund(com.rbkmoney.damsel.fraudbusters.RefundStatus.failed)
        ));
        Thread.sleep(TIMEOUT);

        maps = jdbcTemplate.queryForList("SELECT * from " + EventSource.FRAUD_EVENTS_REFUND.getTable());
        assertEquals(2, maps.size());

        //Withdrawal
        client.insertWithdrawals(List.of(
                createChargeback(WithdrawalStatus.pending),
                createChargeback(WithdrawalStatus.failed),
                createChargeback(WithdrawalStatus.succeeded)
        ));

        Thread.sleep(TIMEOUT);

        maps = jdbcTemplate.queryForList("SELECT * from " + EventSource.FRAUD_EVENTS_WITHDRAWAL.getTable());
        assertEquals(3, maps.size());
    }

    private void checkInsertingBatch(PaymentServiceSrv.Iface client) throws TException, InterruptedException {
        insertWithTimeout(
                client,
                List.of(createPayment(PaymentStatus.processed),
                        createPayment(PaymentStatus.processed),
                        createPayment(PaymentStatus.processed),
                        createPayment(PaymentStatus.processed),
                        createPayment(PaymentStatus.processed)
                )
        );
        List<Map<String, Object>> maps =
                jdbcTemplate.queryForList("SELECT * from " + EventSource.FRAUD_EVENTS_PAYMENT.getTable());
        assertEquals(5, maps.size());
        assertEquals("email", maps.get(0).get("email"));
        Thread.sleep(TIMEOUT);
    }

    private void insertWithTimeout(PaymentServiceSrv.Iface client, Payment payment)
            throws TException, InterruptedException {
        insertWithTimeout(client, List.of(payment));
    }

    private void insertWithTimeout(PaymentServiceSrv.Iface client, List<Payment> payments)
            throws TException, InterruptedException {
        client.insertPayments(payments);
        Thread.sleep(TIMEOUT * 5);
    }

    private void checkPayment(String payment1, ResultStatus status, int expectedCount) {
        List<Map<String, Object>> maps =
                jdbcTemplate.queryForList(String.format("SELECT * from fraud.payment where id='%s'", payment1));
        log.info("SELECT : {}", maps);
        assertEquals(expectedCount, maps.size());
        assertEquals(status.name(), maps.get(0).get("resultStatus"));
    }

    private void insertListDefaultPayments(
            PaymentServiceSrv.Iface client,
            PaymentStatus processed,
            PaymentStatus processed2) throws TException, InterruptedException {
        insertWithTimeout(client, List.of(createPayment(processed), createPayment(processed2)));
    }

}
