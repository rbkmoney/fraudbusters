package com.rbkmoney.fraudbusters;

import com.rbkmoney.damsel.fraudbusters.PaymentServiceSrv;
import com.rbkmoney.damsel.fraudbusters.PaymentStatus;
import com.rbkmoney.fraudbusters.repository.FraudPaymentRepositoryTest;
import com.rbkmoney.woody.thrift.impl.http.THClientBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static com.rbkmoney.fraudbusters.util.BeanUtil.createPayment;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Slf4j
@ActiveProfiles("full-prod")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT,
        classes = FraudBustersApplication.class,
        properties = {"kafka.listen.result.concurrency=1"})
class FraudPaymentTest extends JUnit5IntegrationTest {

    public static final String ID_PAYMENT = "inv";
    public static final String EMAIL = "kek@kek.ru";

    @Autowired
    JdbcTemplate jdbcTemplate;

    @LocalServerPort
    int serverPort;

    @SneakyThrows
    @Test
    public void testFraudPayment() {
        THClientBuilder clientBuilder = new THClientBuilder()
                .withAddress(new URI(String.format("http://localhost:%s/fraud_payment/v1/", serverPort)))
                .withNetworkTimeout(300000);
        PaymentServiceSrv.Iface client = clientBuilder.build(PaymentServiceSrv.Iface.class);

        //Insert payment row
        com.rbkmoney.damsel.fraudbusters.Payment payment = createPayment(PaymentStatus.captured);
        payment.setId(ID_PAYMENT);
        payment.getClientInfo().setEmail(EMAIL);
        insertWithTimeout(client, List.of(payment));

        //Insert fraud row
        client.insertFraudPayments(List.of(FraudPaymentRepositoryTest.createFraudPayment(ID_PAYMENT)));
        Thread.sleep(TIMEOUT * 10);

        //Check join and view working
        List<Map<String, Object>> maps = jdbcTemplate.queryForList("SELECT * from fraud.fraud_payment");
        assertEquals(1, maps.size());
        assertEquals(EMAIL, maps.get(0).get("email"));
    }

    private void insertWithTimeout(
            PaymentServiceSrv.Iface client,
            List<com.rbkmoney.damsel.fraudbusters.Payment> payments) throws TException, InterruptedException {
        client.insertPayments(payments);
        Thread.sleep(TIMEOUT * 10);
    }

}
