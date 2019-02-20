package com.rbkmoney.fraudbusters;

import com.rbkmoney.damsel.domain.RiskScore;
import com.rbkmoney.damsel.proxy_inspector.Context;
import com.rbkmoney.damsel.proxy_inspector.InspectorProxySrv;
import com.rbkmoney.fraudbusters.constant.CommandType;
import com.rbkmoney.fraudbusters.constant.TemplateLevel;
import com.rbkmoney.fraudbusters.domain.FraudRequest;
import com.rbkmoney.fraudbusters.domain.RuleTemplate;
import com.rbkmoney.fraudbusters.util.BeanUtil;
import com.rbkmoney.woody.thrift.impl.http.THClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;


@Slf4j
@ContextConfiguration(initializers = ApiInspectorTest.Initializer.class)
public class ApiInspectorTest extends KafkaAbstractTest {

    public static final String TEMPLATE = "rule: 12 >= 1\n" +
            " -> accept;";
    public static final String GLOBAL_TOPIC = "global_topic";

    private InspectorProxySrv.Iface client;

    @LocalServerPort
    int serverPort;

    private static String SERVICE_URL = "http://localhost:%s/v1/fraud_inspector";

    @Before
    public void init() throws ExecutionException, InterruptedException {
        Producer<String, RuleTemplate> producer = createProducer();
        RuleTemplate ruleTemplate = new RuleTemplate();
        ruleTemplate.setLvl(TemplateLevel.GLOBAL);
        ruleTemplate.setCommandType(CommandType.UPDATE);
        ruleTemplate.setTemplate(TEMPLATE);
        ProducerRecord<String, RuleTemplate> producerRecord = new ProducerRecord<>(templateTopic,
                TemplateLevel.GLOBAL.toString(), ruleTemplate);
        producer.send(producerRecord).get();
        producer.close();
    }

    @Test
    public void test() throws URISyntaxException, TException {
        THClientBuilder clientBuilder = new THClientBuilder()
                .withAddress(new URI(String.format(SERVICE_URL, serverPort)))
                .withNetworkTimeout(300000);
        client = clientBuilder.build(InspectorProxySrv.Iface.class);

        Context context = BeanUtil.createContext();
        RiskScore riskScore = client.inspectPayment(context);

        Assert.assertEquals(riskScore, RiskScore.low);
    }

}