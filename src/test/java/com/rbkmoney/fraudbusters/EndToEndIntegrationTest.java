package com.rbkmoney.fraudbusters;

import com.rbkmoney.damsel.domain.RiskScore;
import com.rbkmoney.damsel.fraudbusters.PriorityId;
import com.rbkmoney.damsel.geo_ip.LocationInfo;
import com.rbkmoney.damsel.proxy_inspector.Context;
import com.rbkmoney.damsel.proxy_inspector.InspectorProxySrv;
import com.rbkmoney.fraudbusters.repository.impl.FraudResultRepository;
import com.rbkmoney.fraudbusters.repository.source.SourcePool;
import com.rbkmoney.fraudbusters.serde.CommandDeserializer;
import com.rbkmoney.fraudbusters.util.BeanUtil;
import com.rbkmoney.fraudbusters.util.ChInitializer;
import com.rbkmoney.woody.thrift.impl.http.THClientBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.ClickHouseContainer;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Slf4j
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = FraudBustersApplication.class)
@ContextConfiguration(initializers = EndToEndIntegrationTest.Initializer.class)
public class EndToEndIntegrationTest extends KafkaAbstractTest {

    private static final String TEMPLATE =
            "rule: count(\"email\", 10, 0, \"party_id\", \"shop_id\") > 1  AND count(\"email\", 10) < 3 " +
                    "AND sum(\"email\", 10) >= 18000 " +
                    "AND count(\"card_token\", 10) > 1 " +
                    "AND in(countryBy(\"country_bank\"), \"RUS\") \n" +
                    " -> decline;";

    private static final String TEMPLATE_CONCRETE =
            "rule:  sumSuccess(\"email\", 10) >= 29000  -> decline;";

    private static final String GROUP_DECLINE =
            "rule:  1 >= 0  -> decline;";

    private static final String GROUP_NORMAL =
            "rule:  1 < 0  -> decline;";

    private static final String TEMPLATE_CONCRETE_SHOP =
            "rule:  sum(\"email\", 10) >= 18000  -> accept;";

    private static final int COUNTRY_GEO_ID = 12345;
    private static final String P_ID = "test";
    private static final String GROUP_P_ID = "group_1";
    public static final long TIMEOUT = 1000L;


    @MockBean
    SourcePool sourcePool;
    @Autowired
    FraudResultRepository fraudResultRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @LocalServerPort
    int serverPort;

    private static String SERVICE_URL = "http://localhost:%s/fraud_inspector/v1";

    @ClassRule
    public static ClickHouseContainer clickHouseContainer = new ClickHouseContainer("yandex/clickhouse-server:19.17");

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @SneakyThrows
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            log.info("clickhouse.db.url={}", clickHouseContainer.getJdbcUrl());
            TestPropertyValues.of("clickhouse.db.url=" + clickHouseContainer.getJdbcUrl(),
                    "clickhouse.db.user=" + clickHouseContainer.getUsername(),
                    "clickhouse.db.password=" + clickHouseContainer.getPassword())
                    .applyTo(configurableApplicationContext.getEnvironment());
            LocationInfo info = new LocationInfo();
            info.setCountryGeoId(COUNTRY_GEO_ID);

            ChInitializer.initAllScripts(clickHouseContainer);
        }
    }

    @Before
    public void init() throws ExecutionException, InterruptedException, SQLException, TException {
        Mockito.when(sourcePool.getActiveSource()).thenReturn(fraudResultRepository);

        ChInitializer.initAllScripts(clickHouseContainer);

        String globalRef = UUID.randomUUID().toString();
        produceTemplate(globalRef, TEMPLATE, templateTopic);
        produceReference(true, null, null, globalRef);

        String partyTemplate = UUID.randomUUID().toString();
        produceTemplate(partyTemplate, TEMPLATE_CONCRETE, templateTopic);
        produceReference(false, P_ID, null, partyTemplate);

        String shopRef = UUID.randomUUID().toString();
        produceTemplate(shopRef, TEMPLATE_CONCRETE_SHOP, templateTopic);
        produceReference(false, P_ID, BeanUtil.ID_VALUE_SHOP, shopRef);

        String groupTemplateDecline = UUID.randomUUID().toString();
        produceTemplate(groupTemplateDecline, GROUP_DECLINE, templateTopic);
        String groupTemplateNormal = UUID.randomUUID().toString();
        produceTemplate(groupTemplateNormal, GROUP_NORMAL, templateTopic);

        String groupId = UUID.randomUUID().toString();
        produceGroup(groupId, List.of(new PriorityId()
                .setId(groupTemplateDecline)
                .setPriority(2L), new PriorityId()
                .setId(groupTemplateNormal)
                .setPriority(1L)), groupTopic);
        produceGroupReference(GROUP_P_ID, null, groupId);

        try (Consumer<String, Object> consumer = createConsumer(CommandDeserializer.class)) {
            consumer.subscribe(List.of(groupTopic));
            Unreliables.retryUntilTrue(10, TimeUnit.SECONDS, () -> {
                ConsumerRecords<String, Object> records = consumer.poll(100);
                return !records.isEmpty();
            });
        }

        Mockito.when(geoIpServiceSrv.getLocationIsoCode(any())).thenReturn("RUS");
    }

    @Test
    public void test() throws URISyntaxException, TException, InterruptedException, ExecutionException, NoSuchFieldException, IllegalAccessException {
        THClientBuilder clientBuilder = new THClientBuilder()
                .withAddress(new URI(String.format(SERVICE_URL, serverPort)))
                .withNetworkTimeout(300000);
        InspectorProxySrv.Iface client = clientBuilder.build(InspectorProxySrv.Iface.class);

        Thread.sleep(TIMEOUT);

        Context context = BeanUtil.createContext();
        RiskScore riskScore = client.inspectPayment(context);
        Assert.assertEquals(RiskScore.high, riskScore);

        Thread.sleep(TIMEOUT);

        List<Map<String, Object>> maps = jdbcTemplate.queryForList("select * from fraud.events_unique");

        context = BeanUtil.createContext();
        riskScore = client.inspectPayment(context);
        Assert.assertEquals(RiskScore.fatal, riskScore);

        Thread.sleep(TIMEOUT);

        context = BeanUtil.createContext(P_ID);
        riskScore = client.inspectPayment(context);
        Assert.assertEquals(RiskScore.low, riskScore);

        Thread.sleep(TIMEOUT);

        //test groups templates
        context = BeanUtil.createContext(GROUP_P_ID);
        riskScore = client.inspectPayment(context);
        Assert.assertEquals(RiskScore.fatal, riskScore);

        produceMessageToEventSink(BeanUtil.createMessageCreateInvoice(BeanUtil.SOURCE_ID));
        produceMessageToEventSink(BeanUtil.createMessagePaymentStared(BeanUtil.SOURCE_ID));
        produceMessageToEventSink(BeanUtil.createMessageInvoiceCaptured(BeanUtil.SOURCE_ID));

        maps = jdbcTemplate.queryForList("select * from fraud.events_unique");
    }
}