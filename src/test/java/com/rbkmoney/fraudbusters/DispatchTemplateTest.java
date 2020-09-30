package com.rbkmoney.fraudbusters;

import com.rbkmoney.clickhouse.initializer.ChInitializer;
import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.CommandBody;
import com.rbkmoney.damsel.fraudbusters.TemplateReference;
import com.rbkmoney.fraudbusters.constant.TemplateLevel;
import com.rbkmoney.fraudbusters.pool.Pool;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.ParserRuleContext;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;
import org.testcontainers.containers.ClickHouseContainer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Slf4j
@ActiveProfiles("full-prod")
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = FraudBustersApplication.class, properties = "kafka.listen.result.concurrency=1")
@ContextConfiguration(initializers = DispatchTemplateTest.Initializer.class)
public class DispatchTemplateTest extends IntegrationTest {

    public static final String TEMPLATE = "rule: 12 >= 1\n" +
            " -> accept;";
    public static final int TIMEOUT = 20;

    @Autowired
    private Pool<ParserRuleContext> templatePoolImpl;
    @Autowired
    private Pool<String> referencePoolImpl;
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
            ChInitializer.initAllScripts(clickHouseContainer, List.of("sql/db_init.sql",
                    "sql/V2__create_events_p2p.sql",
                    "sql/V3__create_fraud_payments.sql",
                    "sql/V4__create_payment.sql",
                    "sql/V5__add_fields.sql",
                    "sql/V6__add_result_fields_payment.sql",
                    "sql/V7__add_fields.sql"));
        }
    }

    @Test
    public void testPools() throws ExecutionException, InterruptedException {

        String id = UUID.randomUUID().toString();

        produceTemplate(id, TEMPLATE, kafkaTopics.getTemplate());

        //check message in topic
        waitingTopic(kafkaTopics.getTemplate());

        //check parse context created
        Unreliables.retryUntilTrue(TIMEOUT, TimeUnit.SECONDS, () -> {
            ParserRuleContext parseContext = templatePoolImpl.get(id);
            return parseContext != null;
        });

        //create global template reference
        try (Producer<String, Command> producer = createProducer()) {
            Command command = new Command();
            TemplateReference value = new TemplateReference();
            value.setIsGlobal(true);
            value.setTemplateId(id);
            command.setCommandBody(CommandBody.reference(value));
            command.setCommandType(com.rbkmoney.damsel.fraudbusters.CommandType.CREATE);
            command.setCommandTime(LocalDateTime.now().toString());

            ProducerRecord<String, Command> producerRecord = new ProducerRecord<>(kafkaTopics.getReference(),
                    TemplateLevel.GLOBAL.name(), command);
            producer.send(producerRecord).get();
        }

        //check that global reference created
        Unreliables.retryUntilTrue(TIMEOUT, TimeUnit.SECONDS, () -> {
            String result = referencePoolImpl.get(TemplateLevel.GLOBAL.name());
            if (StringUtils.isEmpty(result)) {
                return false;
            }
            Assert.assertEquals(id, result);
            return true;
        });
    }

}
