package com.rbkmoney.fraudbusters.repository;

import com.rbkmoney.damsel.geo_ip.GeoIpServiceSrv;
import com.rbkmoney.fraudbusters.config.ClickhouseConfig;
import com.rbkmoney.fraudbusters.constant.ClickhouseSchemeNames;
import com.rbkmoney.fraudbusters.constant.EventField;
import com.rbkmoney.fraudbusters.constant.MgEventSinkField;
import com.rbkmoney.fraudbusters.domain.Event;
import com.rbkmoney.fraudbusters.domain.FraudResult;
import com.rbkmoney.fraudbusters.domain.MgEventSinkRow;
import com.rbkmoney.fraudbusters.fraud.resolver.FieldResolver;
import com.rbkmoney.fraudbusters.util.BeanUtil;
import com.rbkmoney.fraudbusters.util.FileUtil;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import com.rbkmoney.fraudo.constant.CheckedField;
import com.rbkmoney.fraudo.constant.ResultStatus;
import com.rbkmoney.fraudo.model.FraudModel;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.ClickHouseContainer;
import ru.yandex.clickhouse.ClickHouseDataSource;
import ru.yandex.clickhouse.settings.ClickHouseProperties;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ContextConfiguration(classes = {MgEventSinkRepository.class, ClickhouseConfig.class, FieldResolver.class}, initializers = MgEventSinkRepositoryTest.Initializer.class)
public class MgEventSinkRepositoryTest {

    public static final String SELECT_COUNT_AS_CNT_FROM_FRAUD_EVENTS_UNIQUE = "SELECT count() as cnt from " +
            ClickhouseSchemeNames.FRAUD + "." + ClickhouseSchemeNames.EVENTS_SINK_MG;
    public static final String PARTY_ID = "partyId";
    public static final String SHOP_ID = "shopId";
    public static final String TEST_MAIL_RU = "test@mail.ru";

    @ClassRule
    public static ClickHouseContainer clickHouseContainer = new ClickHouseContainer();

    @Autowired
    private MgEventSinkRepository mgEventSinkRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    FieldResolver fieldResolver;

    @MockBean
    GeoIpServiceSrv.Iface iface;

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            log.info("clickhouse.db.url={}", clickHouseContainer.getJdbcUrl());
            TestPropertyValues
                    .of("clickhouse.db.url=" + clickHouseContainer.getJdbcUrl(),
                            "clickhouse.db.user=" + clickHouseContainer.getUsername(),
                            "clickhouse.db.password=" + clickHouseContainer.getPassword())
                    .applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @Before
    public void setUp() throws Exception {
        Connection connection = getSystemConn();
        execScript(connection, "sql/db_init.sql");
        execScript(connection, "sql/V2__create_event_sink.sql");
        connection.close();
    }

    private void execScript(Connection connection, String fileName) throws SQLException {
        String sql = FileUtil.getFile(fileName);
        String[] split = sql.split(";");
        for (String exec : split) {
            connection.createStatement().execute(exec);
        }
    }

    private Connection getSystemConn() throws SQLException {
        ClickHouseProperties properties = new ClickHouseProperties();
        ClickHouseDataSource dataSource = new ClickHouseDataSource(clickHouseContainer.getJdbcUrl(), properties);
        return dataSource.getConnection();
    }

    @Test
    public void insert() throws SQLException {
        MgEventSinkRow defaultMgEvent = createDefaultMgEvent("1");
        mgEventSinkRepository.insert(defaultMgEvent);

        Integer count = jdbcTemplate.queryForObject(SELECT_COUNT_AS_CNT_FROM_FRAUD_EVENTS_UNIQUE,
                (resultSet, i) -> resultSet.getInt("cnt"));
        Assert.assertEquals(1, count.intValue());
    }

    @Test
    public void insertWithEmptyFields() throws SQLException {
        MgEventSinkRow defaultMgEvent = createDefaultMgEvent("1");
        defaultMgEvent.setCurrency(null);
        defaultMgEvent.setAmount(null);
        defaultMgEvent.setBankCountry(null);
        mgEventSinkRepository.insert(defaultMgEvent);

        Integer count = jdbcTemplate.queryForObject(SELECT_COUNT_AS_CNT_FROM_FRAUD_EVENTS_UNIQUE,
                (resultSet, i) -> resultSet.getInt("cnt"));
        Assert.assertEquals(1, count.intValue());
    }

    @Test
    public void insertBatch() throws SQLException {
        List<MgEventSinkRow> batch = createBatch();
        mgEventSinkRepository.insertBatch(batch);

        Integer count = jdbcTemplate.queryForObject(SELECT_COUNT_AS_CNT_FROM_FRAUD_EVENTS_UNIQUE,
                (resultSet, i) -> resultSet.getInt("cnt"));

        Assert.assertEquals(2, count.intValue());
    }

    @NotNull
    private List<MgEventSinkRow> createBatch() {
        return List.of(createDefaultMgEvent("1"), createDefaultMgEvent("2"));
    }

    @Test
    public void countOperationByEmailTest() throws SQLException {
        mgEventSinkRepository.insertBatch(createBatch());

        MgEventSinkRow defaultMgEvent = createDefaultMgEvent("1");
        String email = "my@email.test";
        defaultMgEvent.setEmail(email);
        mgEventSinkRepository.insert(defaultMgEvent);

        Instant now = Instant.now();
        Long from = TimestampUtil.generateTimestampMinusMinutes(now, 10L);
        Long to = TimestampUtil.generateTimestampNow(now);

        int count = mgEventSinkRepository.countOperationByField(MgEventSinkField.email, TEST_MAIL_RU, from, to);
        Assert.assertEquals(2, count);
    }

    @Test
    public void countOperationByEmailTestWithGroupBy() throws SQLException {
        MgEventSinkRow defaultMgEvent = createDefaultMgEvent("1");
        defaultMgEvent.setPartyId(PARTY_ID + "_1");
        mgEventSinkRepository.insertBatch(List.of(defaultMgEvent));

        mgEventSinkRepository.insertBatch(createBatch());

        Instant now = Instant.now();
        Long from = TimestampUtil.generateTimestampMinusMinutes(now, 10L);
        Long to = TimestampUtil.generateTimestampNow(now);

        int count = mgEventSinkRepository.countOperationByFieldWithGroupBy(MgEventSinkField.email, defaultMgEvent.getEmail(), from, to, List.of());
        Assert.assertEquals(3, count);

        FraudModel fraudModelSecond = BeanUtil.createFraudModelSecond();
        fraudModelSecond.setEmail(TEST_MAIL_RU);
        fraudModelSecond.setPartyId(PARTY_ID + "_1");

        FieldResolver.FieldModel resolve = fieldResolver.resolve(CheckedField.PARTY_ID, fraudModelSecond);
        count = mgEventSinkRepository.countOperationByFieldWithGroupBy(MgEventSinkField.email, fraudModelSecond.getEmail(), from, to, List.of(resolve));
        Assert.assertEquals(1, count);
    }

    private MgEventSinkRow createDefaultMgEvent(String paymentId) {
        MgEventSinkRow mgEventSinkRow = new MgEventSinkRow();
        mgEventSinkRow.setAmount(1200L);
        mgEventSinkRow.setCurrency("RUB");
        mgEventSinkRow.setBankCountry("RUS");
        mgEventSinkRow.setCardToken("12341234qweffqewf234");
        mgEventSinkRow.setCountry("RUS");
        mgEventSinkRow.setEmail(TEST_MAIL_RU);
        mgEventSinkRow.setFingerprint("fwerfwerfw");
        mgEventSinkRow.setInvoiceId("invoice_1");
        mgEventSinkRow.setIp("123.123.123.123");
        mgEventSinkRow.setMaskedPan("***1324234");
        mgEventSinkRow.setPartyId(PARTY_ID);
        mgEventSinkRow.setShopId(SHOP_ID);
        mgEventSinkRow.setBankName("RBK");
        mgEventSinkRow.setPaymentId(paymentId);
        mgEventSinkRow.setResultStatus(ResultStatus.ACCEPT.name());
        Instant now = Instant.now();
        Long to = TimestampUtil.generateTimestampNow(now);
        mgEventSinkRow.setTimestamp(new Date(to));
        mgEventSinkRow.setEventTime(to);
        mgEventSinkRow.setBin(BeanUtil.BIN);
        return mgEventSinkRow;
    }

}