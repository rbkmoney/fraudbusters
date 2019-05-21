package com.rbkmoney.fraudbusters;

import com.rbkmoney.damsel.domain.RiskScore;
import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.damsel.fraudbusters.CommandBody;
import com.rbkmoney.damsel.fraudbusters.Template;
import com.rbkmoney.damsel.fraudbusters.TemplateReference;
import com.rbkmoney.damsel.proxy_inspector.Context;
import com.rbkmoney.damsel.proxy_inspector.InspectorProxySrv;
import com.rbkmoney.fraudbusters.constant.TemplateLevel;
import com.rbkmoney.fraudbusters.util.BeanUtil;
import com.rbkmoney.woody.thrift.impl.http.THClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;


@Slf4j
@ContextConfiguration(initializers = PreLoadTest.Initializer.class)
public class PreLoadTest extends KafkaAbstractTest {

    private static final String TEMPLATE = "rule: 12 >= 1\n" +
            " -> accept;";
    private static final String TEST = "test";

    private InspectorProxySrv.Iface client;

    @LocalServerPort
    int serverPort;

    private static String SERVICE_URL = "http://localhost:%s/v1/fraud_inspector";

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues
                    .of("kafka.bootstrap.servers=" + kafka.getBootstrapServers())
                    .applyTo(configurableApplicationContext.getEnvironment());
            try {
                createTemplate();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        private static String createTemplate() throws InterruptedException, ExecutionException {
            Producer<String, Command> producer = createProducer();
            Command command = new Command();
            Template template = new Template();
            String id = TEST;
            template.setId(id);
            template.setTemplate(TEMPLATE.getBytes());
            command.setCommandBody(CommandBody.template(template));
            command.setCommandType(com.rbkmoney.damsel.fraudbusters.CommandType.CREATE);
            ProducerRecord<String, Command> producerRecord = new ProducerRecord<>("template",
                    id, command);
            producer.send(producerRecord).get();
            producer.close();
            return id;
        }
    }

    @Before
    public void init() throws ExecutionException, InterruptedException {
        createGlobalReferenceToTemplate(TEST);
    }

    private void createGlobalReferenceToTemplate(String id) throws InterruptedException, ExecutionException {
        Producer<String, Command> producer;
        Command command;
        ProducerRecord<String, Command> producerRecord;

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
        producer.close();
    }

    @Test
    public void inspectPaymentTest() throws URISyntaxException, TException, ExecutionException, InterruptedException {
        THClientBuilder clientBuilder = new THClientBuilder()
                .withAddress(new URI(String.format(SERVICE_URL, serverPort)))
                .withNetworkTimeout(300000);
        client = clientBuilder.build(InspectorProxySrv.Iface.class);
        createGlobalReferenceToTemplate(TEST);

        Context context = BeanUtil.createContext();
        RiskScore riskScore = client.inspectPayment(context);

        Assert.assertEquals(riskScore, RiskScore.low);
    }

}