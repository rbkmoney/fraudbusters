package com.rbkmoney.fraudbusters;

import com.rbkmoney.fraudbusters.constant.TemplateLevel;
import com.rbkmoney.fraudbusters.domain.RuleTemplate;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

public class FraudBustersApplicationTest extends KafkaAbstractTest {

    public static final String CONCRETE = "concrete";
    public static final String TEMPLATE = "rule: 12 >= 1\n" +
            " -> accept;";
    public static final long SLEEP = 1000L;

    @Test
    public void testGlobal() throws ExecutionException, InterruptedException, TException {
        Producer<String, RuleTemplate> producer = createProducer();

        RuleTemplate ruleTemplate = new RuleTemplate();
        ruleTemplate.setLvl(TemplateLevel.GLOBAL);
        ruleTemplate.setTemplate(TEMPLATE);
        ProducerRecord<String, RuleTemplate> producerRecord = new ProducerRecord<>(templateTopic,
                TemplateLevel.GLOBAL.toString(), ruleTemplate);
        producer.send(producerRecord).get();
        Thread.sleep(SLEEP);
        KafkaStreams kafkaStreams = streamPool.get(TemplateLevel.GLOBAL.toString());
        Assert.assertNotNull(kafkaStreams);
    }

    @Test
    public void testConcrete() throws ExecutionException, InterruptedException, TException {
        RuleTemplate ruleTemplate = new RuleTemplate();

        ruleTemplate.setLvl(TemplateLevel.CONCRETE);
        ruleTemplate.setLocalId(CONCRETE);
        ruleTemplate.setTemplate(TEMPLATE);
        ProducerRecord<String, RuleTemplate> producerRecord = new ProducerRecord<>(templateTopic,
                CONCRETE, ruleTemplate);
        Producer<String, RuleTemplate> producer = createProducer();

        producer.send(producerRecord).get();

        Thread.sleep(SLEEP);
        KafkaStreams kafkaStreams = streamPool.get(CONCRETE);
        Assert.assertNotNull(kafkaStreams);
    }

}