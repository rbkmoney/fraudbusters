package com.rbkmoney.fraudbusters;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.CommandBody;
import com.rbkmoney.damsel.fraudbusters.TemplateReference;
import com.rbkmoney.fraudbusters.constant.TemplateLevel;
import com.rbkmoney.fraudbusters.serde.CommandDeserializer;
import com.rbkmoney.fraudbusters.template.pool.Pool;
import org.antlr.v4.runtime.ParserRuleContext;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = FraudBustersApplication.class, properties = "kafka.listen.result.concurrency=1")
public class DispatchTemplateTest extends KafkaAbstractTest {

    public static final String TEMPLATE = "rule: 12 >= 1\n" +
            " -> accept;";
    public static final int TIMEOUT = 20;

    @Autowired
    private Pool<ParserRuleContext> templatePoolImpl;
    @Autowired
    private Pool<String> referencePoolImpl;

    @Test
    public void testPools() throws ExecutionException, InterruptedException {

        String id = UUID.randomUUID().toString();

        produceTemplate(id, TEMPLATE, kafkaTopics.getTemplate());

        //check message in topic
        try (Consumer<String, Object> consumer = createConsumer(CommandDeserializer.class)) {
            consumer.subscribe(List.of(kafkaTopics.getTemplate()));
            Unreliables.retryUntilTrue(TIMEOUT, TimeUnit.SECONDS, () -> {
                ConsumerRecords<String, Object> records = consumer.poll(Duration.ofSeconds(1L));
                return !records.isEmpty();
            });
        }

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