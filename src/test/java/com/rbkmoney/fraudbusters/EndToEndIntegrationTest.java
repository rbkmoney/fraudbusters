package com.rbkmoney.fraudbusters;

import com.rbkmoney.damsel.domain.RiskScore;
import com.rbkmoney.damsel.geo_ip.LocationInfo;
import com.rbkmoney.damsel.proxy_inspector.Context;
import com.rbkmoney.damsel.proxy_inspector.InspectorProxySrv;
import com.rbkmoney.fraudbusters.constant.CommandType;
import com.rbkmoney.fraudbusters.constant.TemplateLevel;
import com.rbkmoney.fraudbusters.domain.RuleTemplate;
import com.rbkmoney.fraudbusters.serde.FraudRequestSerializer;
import com.rbkmoney.fraudbusters.util.BeanUtil;
import com.rbkmoney.fraudbusters.util.FileUtil;
import com.rbkmoney.fraudbusters.util.KeyGenerator;
import com.rbkmoney.fraudo.model.FraudModel;
import com.rbkmoney.woody.thrift.impl.http.THClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
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
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.any;

@Slf4j
@ContextConfiguration(initializers = EndToEndIntegrationTest.Initializer.class)
public class EndToEndIntegrationTest extends KafkaAbstractTest {

    private static final String TEMPLATE =
            "rule: count(\"email\", 10) >= 1 " +
                    "AND sum(\"email\", 10) >= 90 " +
                    "AND country() = \"RU\"\n" +
            " -> decline;";
    private static final String GLOBAL_TOPIC = "global_topic";
    private static final int COUNTRY_GEO_ID = 12345;

    private InspectorProxySrv.Iface client;

    @LocalServerPort
    int serverPort;

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

        Producer<String, FraudModel> producerNew = createProducerGlobal();
        ProducerRecord<String, FraudModel> producerRecordNew = new ProducerRecord<>(GLOBAL_TOPIC,
                TemplateLevel.GLOBAL.toString(), null);
        producerNew.send(producerRecordNew).get();
        producerNew.close();

        Producer<String, RuleTemplate> producer = createProducer();
        RuleTemplate ruleTemplate = new RuleTemplate();
        ruleTemplate.setLvl(TemplateLevel.GLOBAL);
        ruleTemplate.setTemplate(TEMPLATE);
        ruleTemplate.setCommandType(CommandType.UPDATE);
        ProducerRecord<String, RuleTemplate> producerRecord = new ProducerRecord<>(templateTopic,
                TemplateLevel.GLOBAL.toString(), ruleTemplate);
        producer.send(producerRecord).get();
        producer.close();

        LocationInfo info = new LocationInfo();
        info.setCountryGeoId(COUNTRY_GEO_ID);
        Mockito.when(geoIpServiceSrv.getLocationIsoCode(any())).thenReturn("RU");
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

        Thread.sleep(1000L);
        context = BeanUtil.createContext("test");
        riskScore = client.inspectPayment(context);

        Assert.assertEquals(RiskScore.fatal, riskScore);
    }

    public static Producer<String, FraudModel> createProducerGlobal() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, KeyGenerator.generateKey("global"));
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, FraudRequestSerializer.class.getName());
        return new KafkaProducer<>(props);
    }

}