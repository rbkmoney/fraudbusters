package com.rbkmoney.fraudbusters;

import com.rbkmoney.fraudbusters.constant.CommandType;
import com.rbkmoney.fraudbusters.constant.TemplateLevel;
import com.rbkmoney.fraudbusters.domain.RuleTemplate;
import com.rbkmoney.fraudbusters.template.pool.RuleTemplatePool;
import com.rbkmoney.fraudo.FraudoParser;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutionException;

public class DispatchTemplateTest extends KafkaAbstractTest {

    public static final String CONCRETE = "concrete";
    public static final String TEMPLATE = "rule: 12 >= 1\n" +
            " -> accept;";
    public static final long SLEEP = 1000L;
    @Autowired
    private RuleTemplatePool pool;

    @Test
    public void testGlobal() throws ExecutionException, InterruptedException, TException {
        Producer<String, RuleTemplate> producer = createProducer();
        RuleTemplate ruleTemplate = new RuleTemplate();
        ruleTemplate.setLvl(TemplateLevel.GLOBAL);
        ruleTemplate.setTemplate(TEMPLATE);
        ruleTemplate.setCommandType(CommandType.UPDATE);
        ProducerRecord<String, RuleTemplate> producerRecord = new ProducerRecord<>(templateTopic,
                TemplateLevel.GLOBAL.toString(), ruleTemplate);
        producer.send(producerRecord).get();
        producer.close();

        Thread.sleep(SLEEP);

        FraudoParser.ParseContext parseContext = pool.get(TemplateLevel.GLOBAL.toString());
        Assert.assertNotNull(parseContext);
    }

    @Test
    public void testConcrete() throws ExecutionException, InterruptedException, TException {
        RuleTemplate ruleTemplate = new RuleTemplate();
        ruleTemplate.setLvl(TemplateLevel.CONCRETE);
        ruleTemplate.setLocalId(CONCRETE);
        ruleTemplate.setCommandType(CommandType.UPDATE);
        ruleTemplate.setTemplate(TEMPLATE);
        ProducerRecord<String, RuleTemplate> producerRecord = new ProducerRecord<>(templateTopic,
                CONCRETE, ruleTemplate);
        Producer<String, RuleTemplate> producer = createProducer();
        producer.send(producerRecord).get();
        producer.close();
        Thread.sleep(SLEEP);

        FraudoParser.ParseContext parseContext = pool.get(CONCRETE);
        Assert.assertNotNull(parseContext);
    }

}