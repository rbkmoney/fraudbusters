package com.rbkmoney.fraudbusters;

import com.rbkmoney.damsel.domain.RiskScore;
import com.rbkmoney.damsel.geo_ip.LocationInfo;
import com.rbkmoney.damsel.proxy_inspector.Context;
import com.rbkmoney.damsel.proxy_inspector.InspectorProxySrv;
import com.rbkmoney.fraudbusters.constant.CommandType;
import com.rbkmoney.fraudbusters.constant.TemplateLevel;
import com.rbkmoney.fraudbusters.domain.RuleTemplate;
import com.rbkmoney.fraudbusters.util.BeanUtil;
import com.rbkmoney.fraudbusters.util.FileUtil;
import com.rbkmoney.woody.thrift.impl.http.THClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.ClickHouseContainer;
import ru.yandex.clickhouse.ClickHouseDataSource;
import ru.yandex.clickhouse.settings.ClickHouseProperties;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.any;

@Slf4j
@ContextConfiguration(initializers = EndToEndIntegrationTest.Initializer.class)
public class EndToEndIntegrationTest extends KafkaAbstractTest {

    private static final String TEMPLATE =
            "rule: count(\"email\", 10) >= 1  AND count(\"email\", 10) < 2 " +
                    "AND sum(\"email\", 10) >= 90 " +
                    "AND country() = \"RU\"\n" +
            " -> decline;";

    private static final String TEMPLATE_CONCRETE =
            "rule:  sum(\"email\", 10) >= 200  -> decline;";

    private static final String TEMPLATE_CONCRETE_SHOP =
            "rule:  sum(\"email\", 10) >= 90  -> accept;";

    private static final int COUNTRY_GEO_ID = 12345;
    public static final String P_ID = "test";

    private InspectorProxySrv.Iface client;

    @LocalServerPort
    int serverPort;

    @Value("${kafka.global.stream.topic}")
    public String GLOBAL_TOPIC;

    private static String SERVICE_URL = "http://localhost:%s/v1/fraud_inspector";

    @ClassRule
    public static ClickHouseContainer clickHouseContainer = new ClickHouseContainer();

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            log.info("clickhouse.db.url={}", clickHouseContainer.getJdbcUrl());
            TestPropertyValues
                    .of("clickhouse.db.url=" + clickHouseContainer.getJdbcUrl())
                    .applyTo(configurableApplicationContext.getEnvironment());
            LocationInfo info = new LocationInfo();
            info.setCountryGeoId(COUNTRY_GEO_ID);
        }
    }

    private Connection getSystemConn() throws SQLException {
        ClickHouseProperties properties = new ClickHouseProperties();
        ClickHouseDataSource dataSource = new ClickHouseDataSource(clickHouseContainer.getJdbcUrl(), properties);
        return dataSource.getConnection();
    }

    @Before
    public void init() throws ExecutionException, InterruptedException, SQLException, TException {
        Connection connection = getSystemConn();
        String sql = FileUtil.getFile("sql/db_init.sql");
        String[] split = sql.split(";");
        for (String exec : split) {
            connection.createStatement().execute(exec);
        }
        connection.close();
        Producer<String, RuleTemplate> producer = createProducer();
        RuleTemplate ruleTemplate = new RuleTemplate();
        ruleTemplate.setLvl(TemplateLevel.GLOBAL);
        ruleTemplate.setTemplate(TEMPLATE);
        ruleTemplate.setCommandType(CommandType.UPDATE);
        ProducerRecord<String, RuleTemplate> producerRecord = new ProducerRecord<>(templateTopic,
                TemplateLevel.GLOBAL.toString(), ruleTemplate);
        producer.send(producerRecord).get();
        producer.close();

        createRule(P_ID, TEMPLATE_CONCRETE);

        createRule(P_ID + "_" + BeanUtil.ID_VALUE_SHOP, TEMPLATE_CONCRETE_SHOP);

        Thread.sleep(3000L);

        Mockito.when(geoIpServiceSrv.getLocationIsoCode(any())).thenReturn("RU");
    }

    private void createRule(String localId, String template) throws InterruptedException, ExecutionException {
        Producer<String, RuleTemplate> producer;
        RuleTemplate ruleTemplate;
        ProducerRecord<String, RuleTemplate> producerRecord;
        producer = createProducer();
        ruleTemplate = new RuleTemplate();
        ruleTemplate.setLvl(TemplateLevel.CONCRETE);
        ruleTemplate.setTemplate(template);
        ruleTemplate.setLocalId(localId);
        ruleTemplate.setCommandType(CommandType.UPDATE);
        producerRecord = new ProducerRecord<>(templateTopic, localId, ruleTemplate);
        producer.send(producerRecord).get();
        producer.close();
    }

    @Test
    public void test() throws URISyntaxException, TException, InterruptedException {
        THClientBuilder clientBuilder = new THClientBuilder()
                .withAddress(new URI(String.format(SERVICE_URL, serverPort)))
                .withNetworkTimeout(300000);
        client = clientBuilder.build(InspectorProxySrv.Iface.class);

        Context context = BeanUtil.createContext();
        RiskScore riskScore = client.inspectPayment(context);
        Assert.assertEquals(RiskScore.high, riskScore);

        context = BeanUtil.createContext();
        riskScore = client.inspectPayment(context);
        Assert.assertEquals(RiskScore.fatal, riskScore);

        context = BeanUtil.createContext(P_ID);
        riskScore = client.inspectPayment(context);
        Assert.assertEquals(RiskScore.low, riskScore);
    }

}