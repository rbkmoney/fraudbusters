package com.rbkmoney.fraudbusters;

import com.rbkmoney.damsel.domain.RiskScore;
import com.rbkmoney.damsel.proxy_inspector.Context;
import com.rbkmoney.damsel.proxy_inspector.InspectorProxySrv;
import com.rbkmoney.fraudbusters.constant.TemplateLevel;
import com.rbkmoney.fraudbusters.domain.RuleTemplate;
import com.rbkmoney.fraudbusters.serde.FraudoModelSerializer;
import com.rbkmoney.fraudbusters.util.BeanUtil;
import com.rbkmoney.fraudbusters.util.KeyGenerator;
import com.rbkmoney.fraudo.model.FraudModel;
import com.rbkmoney.woody.thrift.impl.http.THClientBuilder;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.web.server.LocalServerPort;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class ApiInspectorTestTest extends KafkaAbstractTest {

    public static final String TEMPLATE = "rule: 12 >= 1\n" +
            " -> accept;";

    private InspectorProxySrv.Iface client;

    @LocalServerPort
    int serverPort;

    private static String SERVICE_URL = "http://localhost:%s/v1/fraud_inspector";

    @Before
    public void init() throws ExecutionException, InterruptedException {
        Producer<String, FraudModel> producerNew = createProducerGlobal();

        ProducerRecord<String, FraudModel> producerRecordNew = new ProducerRecord<>("global_topic",
                TemplateLevel.GLOBAL.toString(), null);
        producerNew.send(producerRecordNew).get();

        Producer<String, RuleTemplate> producer = createProducer();

        RuleTemplate ruleTemplate = new RuleTemplate();
        ruleTemplate.setLvl(TemplateLevel.GLOBAL);
        ruleTemplate.setTemplate(TEMPLATE);
        ProducerRecord<String, RuleTemplate> producerRecord = new ProducerRecord<>(templateTopic,
                TemplateLevel.GLOBAL.toString(), ruleTemplate);
        producer.send(producerRecord).get();
    }

    @Test
    public void test() throws URISyntaxException, TException, InterruptedException {

        THClientBuilder clientBuilder = new THClientBuilder()
                .withAddress(new URI(String.format(SERVICE_URL, serverPort)))
                .withNetworkTimeout(300000);
        client = clientBuilder.build(InspectorProxySrv.Iface.class);

        Context context = BeanUtil.createContext();
        RiskScore riskScore = client.inspectPayment(context);

        Assert.assertEquals(riskScore, RiskScore.low);
    }

    public static Producer<String, FraudModel> createProducerGlobal() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, KeyGenerator.generateKey("global"));
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, FraudoModelSerializer.class.getName());
        return new KafkaProducer<>(props);
    }
}