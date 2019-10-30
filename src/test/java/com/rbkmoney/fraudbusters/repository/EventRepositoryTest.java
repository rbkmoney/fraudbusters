package com.rbkmoney.fraudbusters.repository;

import com.rbkmoney.damsel.geo_ip.GeoIpServiceSrv;
import com.rbkmoney.fraudbusters.config.ClickhouseConfig;
import com.rbkmoney.fraudbusters.constant.EventField;
import com.rbkmoney.fraudbusters.converter.FraudResultToEventConverter;
import com.rbkmoney.fraudbusters.domain.*;
import com.rbkmoney.fraudbusters.fraud.resolver.DBPaymentFieldResolver;
import com.rbkmoney.fraudbusters.util.BeanUtil;
import com.rbkmoney.fraudbusters.util.FileUtil;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import com.rbkmoney.fraudo.constant.PaymentCheckedField;
import com.rbkmoney.fraudo.constant.ResultStatus;
import com.rbkmoney.fraudo.model.PaymentModel;
import com.rbkmoney.fraudo.model.ResultModel;
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
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ContextConfiguration(classes = {EventRepository.class, FraudResultToEventConverter.class, ClickhouseConfig.class, DBPaymentFieldResolver.class}, initializers = EventRepositoryTest.Initializer.class)
public class EventRepositoryTest {

    private static final String SELECT_COUNT_AS_CNT_FROM_FRAUD_EVENTS_UNIQUE = "SELECT count() as cnt from fraud.events_unique";

    @ClassRule
    public static ClickHouseContainer clickHouseContainer = new ClickHouseContainer();

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    FraudResultToEventConverter fraudResultToEventConverter;

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
        String sql = FileUtil.getFile("sql/db_init.sql");
        String[] split = sql.split(";");
        for (String exec : split) {
            connection.createStatement().execute(exec);
        }
        connection.close();
    }

    private Connection getSystemConn() throws SQLException {
        ClickHouseProperties properties = new ClickHouseProperties();
        ClickHouseDataSource dataSource = new ClickHouseDataSource(clickHouseContainer.getJdbcUrl(), properties);
        return dataSource.getConnection();
    }

    @Test
    public void insert() throws SQLException {
        FraudResult value = createFraudResult(ResultStatus.ACCEPT, BeanUtil.createFraudModel());
        eventRepository.insert(fraudResultToEventConverter.convert(value));

        Integer count = jdbcTemplate.queryForObject(SELECT_COUNT_AS_CNT_FROM_FRAUD_EVENTS_UNIQUE,
                (resultSet, i) -> resultSet.getInt("cnt"));
        Assert.assertEquals(1, count.intValue());
    }


    @Test
    public void insertBatch() throws SQLException {
        List<FraudResult> batch = createBatch();
        List<Event> events = batch.stream()
                .map(fraudResultToEventConverter::convert)
                .collect(Collectors.toList());
        eventRepository.insertBatch(events);

        Integer count = jdbcTemplate.queryForObject(SELECT_COUNT_AS_CNT_FROM_FRAUD_EVENTS_UNIQUE,
                (resultSet, i) -> resultSet.getInt("cnt"));

        Assert.assertEquals(2, count.intValue());
    }

    @NotNull
    private List<FraudResult> createBatch() {
        FraudResult value = createFraudResult(ResultStatus.ACCEPT, BeanUtil.createFraudModel());
        FraudResult value2 = createFraudResult(ResultStatus.DECLINE, BeanUtil.createFraudModelSecond());
        return List.of(value, value2);
    }

    @NotNull
    private FraudResult createFraudResult(ResultStatus decline, PaymentModel fraudModelSecond) {
        FraudResult value2 = new FraudResult();
        CheckedResultModel resultModel = new CheckedResultModel();
        resultModel.setResultModel(new ResultModel(decline, "test",null));
        resultModel.setCheckedTemplate("RULE");

        value2.setResultModel(resultModel);
        PaymentModel fraudModel2 = fraudModelSecond;
        FraudRequest fraudRequest = new FraudRequest();
        fraudRequest.setPaymentModel(fraudModel2);
        Metadata metadata = new Metadata();
        metadata.setTimestamp(TimestampUtil.generateTimestampNow(Instant.now()));
        fraudRequest.setMetadata(metadata);
        value2.setFraudRequest(fraudRequest);
        return value2;
    }

    @Test
    public void countOperationByEmailTest() throws SQLException {
        Instant now = Instant.now();
        Long to = TimestampUtil.generateTimestampNow(now);
        Long from = TimestampUtil.generateTimestampMinusMinutes(now, 10L);
        List<FraudResult> batch = createBatch();
        eventRepository.insertBatch(fraudResultToEventConverter.convertBatch(batch));

        int count = eventRepository.countOperationByField(EventField.email.name(), BeanUtil.EMAIL, from, to);
        Assert.assertEquals(1, count);
    }

    @Test
    public void countOperationByEmailTestWithGroupBy() throws SQLException {
        Instant now = Instant.now();
        Long to = TimestampUtil.generateTimestampNow(now);
        Long from = TimestampUtil.generateTimestampMinusMinutes(now, 10L);
        FraudResult value = createFraudResult(ResultStatus.ACCEPT, BeanUtil.createFraudModel());
        FraudResult value2 = createFraudResult(ResultStatus.DECLINE, BeanUtil.createFraudModelSecond());
        PaymentModel fraudModelSecond = BeanUtil.createFraudModelSecond();
        fraudModelSecond.setPartyId("test");
        FraudResult value3 = createFraudResult(ResultStatus.DECLINE, fraudModelSecond);

        eventRepository.insertBatch(fraudResultToEventConverter.convertBatch(List.of(value, value2, value3)));

        DBPaymentFieldResolver.FieldModel email = DBPaymentFieldResolver.resolve(PaymentCheckedField.EMAIL, fraudModelSecond);
        int count = eventRepository.countOperationByFieldWithGroupBy(EventField.email.name(), email.getValue(), from, to, List.of());
        Assert.assertEquals(2, count);

        DBPaymentFieldResolver.FieldModel resolve = DBPaymentFieldResolver.resolve(PaymentCheckedField.PARTY_ID, fraudModelSecond);
        count = eventRepository.countOperationByFieldWithGroupBy(EventField.email.name(), email.getValue(), from, to, List.of(resolve));
        Assert.assertEquals(1, count);
    }

    @Test
    public void sumOperationByEmailTest() throws SQLException {
        Instant now = Instant.now();
        Long to = TimestampUtil.generateTimestampNow(now);
        Long from = TimestampUtil.generateTimestampMinusMinutes(now, 10L);
        List<FraudResult> batch = createBatch();
        eventRepository.insertBatch(fraudResultToEventConverter.convertBatch(batch));

        Long sum = eventRepository.sumOperationByFieldWithGroupBy(EventField.email.name(), BeanUtil.EMAIL, from, to, List.of());
        Assert.assertEquals(BeanUtil.AMOUNT_FIRST, sum);
    }

    @Test
    public void countUniqOperationTest() {
        FraudResult value = createFraudResult(ResultStatus.ACCEPT, BeanUtil.createFraudModel());
        FraudResult value2 = createFraudResult(ResultStatus.DECLINE, BeanUtil.createFraudModelSecond());
        FraudResult value3 = createFraudResult(ResultStatus.DECLINE, BeanUtil.createFraudModel());
        PaymentModel fraudModel = BeanUtil.createFraudModel();
        fraudModel.setFingerprint("test");
        FraudResult value4 = createFraudResult(ResultStatus.DECLINE, fraudModel);
        eventRepository.insertBatch(fraudResultToEventConverter.convertBatch(List.of(value, value2, value3, value4)));

        Instant now = Instant.now();
        Long to = TimestampUtil.generateTimestampNow(now);
        Long from = TimestampUtil.generateTimestampMinusMinutes(now, 10L);
        Integer sum = eventRepository.uniqCountOperation(EventField.email.name(), BeanUtil.EMAIL, EventField.fingerprint.name(), from, to);
        Assert.assertEquals(Integer.valueOf(2), sum);
    }

    @Test
    public void countUniqOperationWithGroupByTest() {
        FraudResult value = createFraudResult(ResultStatus.ACCEPT, BeanUtil.createFraudModel());
        FraudResult value2 = createFraudResult(ResultStatus.DECLINE, BeanUtil.createFraudModelSecond());
        FraudResult value3 = createFraudResult(ResultStatus.DECLINE, BeanUtil.createFraudModel());
        PaymentModel fraudModel = BeanUtil.createFraudModel();
        fraudModel.setFingerprint("test");
        fraudModel.setPartyId("party");
        FraudResult value4 = createFraudResult(ResultStatus.DECLINE, fraudModel);

        eventRepository.insertBatch(fraudResultToEventConverter.convertBatch(List.of(value, value2, value3, value4)));

        Instant now = Instant.now();
        Long to = TimestampUtil.generateTimestampNow(now);
        Long from = TimestampUtil.generateTimestampMinusMinutes(now, 10L);
        Integer sum = eventRepository.uniqCountOperationWithGroupBy(EventField.email.name(), BeanUtil.EMAIL, EventField.fingerprint.name(), from, to, List.of());
        Assert.assertEquals(Integer.valueOf(2), sum);

        DBPaymentFieldResolver.FieldModel resolve = DBPaymentFieldResolver.resolve(PaymentCheckedField.PARTY_ID, fraudModel);
        sum = eventRepository.uniqCountOperationWithGroupBy(EventField.email.name(), BeanUtil.EMAIL, EventField.fingerprint.name(), from, to, List.of(resolve));
        Assert.assertEquals(Integer.valueOf(1), sum);
    }

}