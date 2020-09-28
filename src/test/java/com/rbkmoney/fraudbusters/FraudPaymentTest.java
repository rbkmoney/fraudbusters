package com.rbkmoney.fraudbusters;

import com.rbkmoney.damsel.fraudbusters.PaymentServiceSrv;
import com.rbkmoney.damsel.fraudbusters.PaymentStatus;
import com.rbkmoney.fraudbusters.repository.FraudPaymentRepositoryTest;
import com.rbkmoney.fraudbusters.util.ChInitializer;
import com.rbkmoney.woody.thrift.impl.http.THClientBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.ClickHouseContainer;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static com.rbkmoney.fraudbusters.util.BeanUtil.createPayment;
import static com.rbkmoney.fraudbusters.util.ChInitializer.execAllInFile;
import static com.rbkmoney.fraudbusters.util.ChInitializer.getSystemConn;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Slf4j
@RunWith(SpringRunner.class)
@ActiveProfiles("full-prod")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = FraudBustersApplication.class, properties = {"kafka.listen.result.concurrency=1"})
@ContextConfiguration(initializers = FraudPaymentTest.Initializer.class)
public class FraudPaymentTest extends IntegrationTest {

    public static final String ID_PAYMENT = "inv";
    public static final String EMAIL = "kek@kek.ru";

    @Autowired
    JdbcTemplate jdbcTemplate;

    @LocalServerPort
    int serverPort;

    @ClassRule
    public static EmbeddedKafkaRule kafka = createKafka();

    @ClassRule
    public static ClickHouseContainer clickHouseContainer = new ClickHouseContainer("yandex/clickhouse-server:19.17");

    @Override
    protected String getBrokersAsString() {
        return kafka.getEmbeddedKafka().getBrokersAsString();
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @SneakyThrows
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            log.info("clickhouse.db.url={}", clickHouseContainer.getJdbcUrl());
            log.info("kafka.bootstrap.servers={}", kafka.getEmbeddedKafka().getBrokersAsString());
            TestPropertyValues.of("clickhouse.db.url=" + clickHouseContainer.getJdbcUrl(),
                    "clickhouse.db.user=" + clickHouseContainer.getUsername(),
                    "clickhouse.db.password=" + clickHouseContainer.getPassword(),
                    "kafka.bootstrap.servers=" + kafka.getEmbeddedKafka().getBrokersAsString())
                    .applyTo(configurableApplicationContext.getEnvironment());
            ChInitializer.initAllScripts(clickHouseContainer);
        }
    }

    @SneakyThrows
    @Test
    public void testFraudPayment() {
        execAllInFile(getSystemConn(clickHouseContainer), "sql/V8__mv_fraud_payment.sql");

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
        Thread.sleep(TIMEOUT);

        //Check join and view working
        List<Map<String, Object>> maps = jdbcTemplate.queryForList("SELECT * from fraud.fraud_payment_full");
        Assert.assertEquals(1, maps.size());
        Assert.assertEquals(EMAIL, maps.get(0).get("email"));
    }

    private void insertWithTimeout(PaymentServiceSrv.Iface client, List<com.rbkmoney.damsel.fraudbusters.Payment> payments) throws TException, InterruptedException {
        client.insertPayments(payments);
        Thread.sleep(TIMEOUT * 10);
    }
}
