package com.rbkmoney.fraudbusters.repository;

import com.rbkmoney.damsel.geo_ip.GeoIpServiceSrv;
import com.rbkmoney.fraudbusters.config.ClickhouseConfig;
import com.rbkmoney.fraudbusters.constant.ClickhouseSchemeNames;
import com.rbkmoney.fraudbusters.constant.EventField;
import com.rbkmoney.fraudbusters.constant.MgEventSinkField;
import com.rbkmoney.fraudbusters.constant.ResultStatus;
import com.rbkmoney.fraudbusters.domain.MgEventSinkRow;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.fraud.resolver.DBPaymentFieldResolver;
import com.rbkmoney.fraudbusters.util.BeanUtil;
import com.rbkmoney.fraudbusters.util.FileUtil;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
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

@Slf4j
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ContextConfiguration(classes = {MgEventSinkRepository.class, ClickhouseConfig.class, DBPaymentFieldResolver.class}, initializers = MgEventSinkRepositoryTest.Initializer.class)
public class MgEventSinkRepositoryTest {

    private static final String SELECT_COUNT_AS_CNT_FROM_FRAUD_EVENTS_UNIQUE = "SELECT count() as cnt from " +
            ClickhouseSchemeNames.FRAUD + "." + ClickhouseSchemeNames.EVENTS_SINK_MG;

    private static final String PARTY_ID = "partyId";
    private static final String SHOP_ID = "shopId";
    private static final String TEST_MAIL_RU = "test@mail.ru";
    private static final String MY_EMAIL_TEST = "my@email.test";
    private static final long AMOUNT = 1200L;

    @ClassRule
    public static ClickHouseContainer clickHouseContainer = new ClickHouseContainer();

    @Autowired
    private MgEventSinkRepository mgEventSinkRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    DBPaymentFieldResolver DBPaymentFieldResolver;

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
        MgEventSinkRow defaultMgEvent = createDefaultMgEvent("1", com.rbkmoney.fraudbusters.constant.ResultStatus.CAPTURED.name());
        mgEventSinkRepository.insert(defaultMgEvent);

        Integer count = jdbcTemplate.queryForObject(SELECT_COUNT_AS_CNT_FROM_FRAUD_EVENTS_UNIQUE,
                (resultSet, i) -> resultSet.getInt("cnt"));
        Assert.assertEquals(1, count.intValue());
    }

    @Test
    public void insertWithEmptyFields() throws SQLException {
        MgEventSinkRow defaultMgEvent = createDefaultMgEvent("1", com.rbkmoney.fraudbusters.constant.ResultStatus.CAPTURED.name());
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
        return List.of(
                createDefaultMgEvent("1", ResultStatus.CAPTURED.name()),
                createDefaultMgEvent("2", ResultStatus.FAILED.name()));
    }

    @Test
    public void countOperationByEmailTest() throws SQLException {
        mgEventSinkRepository.insertBatch(createBatch());

        MgEventSinkRow defaultMgEvent = createDefaultMgEvent("1", com.rbkmoney.fraudbusters.constant.ResultStatus.CAPTURED.name());
        defaultMgEvent.setEmail(MY_EMAIL_TEST);
        mgEventSinkRepository.insert(defaultMgEvent);

        Instant now = Instant.now();
        Long from = TimestampUtil.generateTimestampMinusMinutes(now, 10L);
        Long to = TimestampUtil.generateTimestampNow(now);

        int count = mgEventSinkRepository.countOperationByField(MgEventSinkField.email.name(), TEST_MAIL_RU, from, to);
        Assert.assertEquals(2, count);
    }

    @Test
    public void countOperationByEmailTestWithGroupBy() throws SQLException {
        MgEventSinkRow defaultMgEvent = createDefaultMgEvent("1", com.rbkmoney.fraudbusters.constant.ResultStatus.CAPTURED.name());
        defaultMgEvent.setPartyId(PARTY_ID + "_1");
        mgEventSinkRepository.insertBatch(List.of(defaultMgEvent));

        mgEventSinkRepository.insertBatch(createBatch());

        Instant now = Instant.now();
        Long from = TimestampUtil.generateTimestampMinusMinutes(now, 10L);
        Long to = TimestampUtil.generateTimestampNow(now);

        int count = mgEventSinkRepository.countOperationByFieldWithGroupBy(MgEventSinkField.email.name(), defaultMgEvent.getEmail(), from, to, List.of());
        Assert.assertEquals(3, count);

        PaymentModel fraudModelSecond = BeanUtil.createFraudModelSecond();
        fraudModelSecond.setEmail(TEST_MAIL_RU);
        fraudModelSecond.setPartyId(PARTY_ID + "_1");

        DBPaymentFieldResolver.FieldModel resolve = DBPaymentFieldResolver.resolve(PaymentCheckedField.PARTY_ID, fraudModelSecond);
        count = mgEventSinkRepository.countOperationByFieldWithGroupBy(MgEventSinkField.email.name(), fraudModelSecond.getEmail(), from, to, List.of(resolve));
        Assert.assertEquals(1, count);
    }

    @Test
    public void countOperationByEmailSuccessTest() {
        Instant now = Instant.now();
        Long to = TimestampUtil.generateTimestampNow(now);
        Long from = TimestampUtil.generateTimestampMinusMinutes(now, 10L);
        mgEventSinkRepository.insertBatch(createBatch());

        int count = mgEventSinkRepository.countOperationSuccess(EventField.email.name(), TEST_MAIL_RU, from, to);
        Assert.assertEquals(1, count);

        count = mgEventSinkRepository.countOperationError(EventField.email.name(), TEST_MAIL_RU, from, to);
        Assert.assertEquals(1, count);
    }

    @Test
    public void sumOperationByEmailSuccessTest() {
        Instant now = Instant.now();
        Long to = TimestampUtil.generateTimestampNow(now);
        Long from = TimestampUtil.generateTimestampMinusMinutes(now, 10L);
        List<MgEventSinkRow> batch = createBatch();
        long amount = 123L;
        batch.get(1).setAmount(amount);
        mgEventSinkRepository.insertBatch(batch);

        Long sum = mgEventSinkRepository.sumOperationSuccess(EventField.email.name(), TEST_MAIL_RU, from, to);
        Assert.assertEquals(AMOUNT, sum.longValue());

        sum = mgEventSinkRepository.sumOperationError(EventField.email.name(), TEST_MAIL_RU, from, to);
        Assert.assertEquals(amount, sum.longValue());
    }

    @Test
    public void sumOperationByEmailGroupByPartyIdErrorTest() throws SQLException {
        Instant now = Instant.now();
        Long to = TimestampUtil.generateTimestampNow(now);
        Long from = TimestampUtil.generateTimestampMinusMinutes(now, 10L);
        PaymentModel fraudModel = BeanUtil.createPaymentModel();
        MgEventSinkRow defaultMgEvent = createDefaultMgEvent("1", ResultStatus.CAPTURED.name());
        defaultMgEvent.setAmount(BeanUtil.AMOUNT_FIRST);
        MgEventSinkRow defaultMgEvent1 = createDefaultMgEvent("2", ResultStatus.FAILED.name());
        defaultMgEvent1.setAmount(BeanUtil.AMOUNT_FIRST);
        MgEventSinkRow defaultMgEvent2 = createDefaultMgEvent("3", ResultStatus.FAILED.name());
        defaultMgEvent2.setAmount(BeanUtil.AMOUNT_FIRST + 10);
        MgEventSinkRow defaultMgEvent3 = createDefaultMgEvent("4", ResultStatus.FAILED.name());
        defaultMgEvent3.setAmount(BeanUtil.AMOUNT_FIRST + 10);
        defaultMgEvent3.setShopId("test");

        mgEventSinkRepository.insertBatch(List.of(defaultMgEvent, defaultMgEvent1, defaultMgEvent2, defaultMgEvent3));

        DBPaymentFieldResolver.FieldModel partyId = DBPaymentFieldResolver.resolve(PaymentCheckedField.PARTY_ID, fraudModel);
        Long sum = mgEventSinkRepository.sumOperationErrorWithGroupBy(EventField.email.name(), defaultMgEvent.getEmail(), from, to, List.of(partyId));
        Assert.assertEquals(BeanUtil.AMOUNT_FIRST + BeanUtil.AMOUNT_FIRST + 10 + BeanUtil.AMOUNT_FIRST + 10, sum.longValue());

        DBPaymentFieldResolver.FieldModel shopId = DBPaymentFieldResolver.resolve(PaymentCheckedField.SHOP_ID, fraudModel);
        sum = mgEventSinkRepository.sumOperationErrorWithGroupBy(EventField.email.name(), defaultMgEvent.getEmail(), from, to, List.of(partyId, shopId));
        Assert.assertEquals(BeanUtil.AMOUNT_FIRST + BeanUtil.AMOUNT_FIRST + 10, sum.longValue());

        fraudModel.setShopId("test_2");
        shopId = DBPaymentFieldResolver.resolve(PaymentCheckedField.SHOP_ID, fraudModel);
        sum = mgEventSinkRepository.sumOperationErrorWithGroupBy(EventField.email.name(), defaultMgEvent.getEmail(), from, to, List.of(partyId, shopId));
        Assert.assertEquals(0L, sum.longValue());
    }

    private MgEventSinkRow createDefaultMgEvent(String paymentId, String status) {
        MgEventSinkRow mgEventSinkRow = new MgEventSinkRow();
        mgEventSinkRow.setAmount(AMOUNT);
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
        mgEventSinkRow.setResultStatus(status);
        Instant now = Instant.now();
        Long to = TimestampUtil.generateTimestampNow(now);
        mgEventSinkRow.setTimestamp(new Date(to));
        mgEventSinkRow.setEventTime(to);
        mgEventSinkRow.setBin(BeanUtil.BIN);
        return mgEventSinkRow;
    }

}