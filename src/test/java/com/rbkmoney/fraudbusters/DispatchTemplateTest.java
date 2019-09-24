package com.rbkmoney.fraudbusters;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.CommandBody;
import com.rbkmoney.damsel.fraudbusters.Template;
import com.rbkmoney.damsel.fraudbusters.TemplateReference;
import com.rbkmoney.fraudbusters.constant.TemplateLevel;
import com.rbkmoney.fraudbusters.listener.StartupListener;
import com.rbkmoney.fraudbusters.serde.CommandDeserializer;
import com.rbkmoney.fraudbusters.template.pool.Pool;
import com.rbkmoney.fraudo.FraudoParser;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.thrift.TException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = FraudBustersApplication.class)
public class DispatchTemplateTest extends KafkaAbstractTest {

    public static final String TEMPLATE = "rule: 12 >= 1\n" +
            " -> accept;";

    @Autowired
    private Pool<FraudoParser.ParseContext> pool;
    @Autowired
    private Pool<String> referencePoolImpl;

    @Test
    public void testGlobal() throws ExecutionException, InterruptedException {

        String id = UUID.randomUUID().toString();
        Producer<String, Command> producer = createProducer();
        Command command = new Command();
        Template template = new Template();
        template.setId(id);
        template.setTemplate(TEMPLATE.getBytes());
        command.setCommandBody(CommandBody.template(template));
        command.setCommandType(com.rbkmoney.damsel.fraudbusters.CommandType.CREATE);
        ProducerRecord<String, Command> producerRecord = new ProducerRecord<>(templateTopic,
                id, command);
        producer.send(producerRecord).get();
        producer.close();

        Consumer<String, Object> consumer = createConsumer(CommandDeserializer.class);
        consumer.subscribe(List.of(templateTopic));
        recurPolling(consumer);
        consumer.close();

        FraudoParser.ParseContext parseContext = pool.get(id);
        Assert.assertNotNull(parseContext);

        producer = createProducer();
        command = new Command();
        TemplateReference value = new TemplateReference();
        value.setIsGlobal(true);
        value.setTemplateId(id);
        command.setCommandBody(CommandBody.reference(value));
        command.setCommandType(com.rbkmoney.damsel.fraudbusters.CommandType.CREATE);
        producerRecord = new ProducerRecord<>(referenceTopic,
                TemplateLevel.GLOBAL.name(), command);
        producer.send(producerRecord).get();

        Thread.sleep(5000L);

        String result = referencePoolImpl.get(TemplateLevel.GLOBAL.name());

        Assert.assertEquals(id, result);
    }

    @Test
    public void testConcrete() throws ExecutionException, InterruptedException, TException {
        Command command = new Command();
        Template template = new Template();
        String id = UUID.randomUUID().toString();
        template.setId(id);
        template.setTemplate(TEMPLATE.getBytes());
        command.setCommandBody(CommandBody.template(template));
        command.setCommandType(com.rbkmoney.damsel.fraudbusters.CommandType.CREATE);
        ProducerRecord<String, Command> producerRecord = new ProducerRecord<>(templateTopic,
                id, command);

        try (Producer<String, Command> producer = createProducer()) {
            producer.send(producerRecord).get();
        }

        try (Consumer<String, Object> consumer = createConsumer(CommandDeserializer.class)) {
            consumer.subscribe(List.of(templateTopic));
            recurPolling(consumer);
        }

        FraudoParser.ParseContext parseContext = pool.get(id);
        Assert.assertNotNull(parseContext);
    }
}