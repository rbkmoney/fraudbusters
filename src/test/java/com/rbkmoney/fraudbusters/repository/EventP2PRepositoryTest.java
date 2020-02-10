package com.rbkmoney.fraudbusters.repository;

import com.rbkmoney.damsel.geo_ip.GeoIpServiceSrv;
import com.rbkmoney.fraudbusters.config.ClickhouseConfig;
import com.rbkmoney.fraudbusters.constant.EventP2PField;
import com.rbkmoney.fraudbusters.constant.ScoresType;
import com.rbkmoney.fraudbusters.converter.ScoresResultToEventConverter;
import com.rbkmoney.fraudbusters.converter.ScoresResultToEventP2PConverter;
import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import com.rbkmoney.fraudbusters.domain.EventP2P;
import com.rbkmoney.fraudbusters.domain.ScoresResult;
import com.rbkmoney.fraudbusters.fraud.constant.P2PCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.P2PModel;
import com.rbkmoney.fraudbusters.fraud.p2p.resolver.DbP2pFieldResolver;
import com.rbkmoney.fraudbusters.fraud.model.FieldModel;
import com.rbkmoney.fraudbusters.util.BeanUtil;
import com.rbkmoney.fraudbusters.util.FileUtil;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import com.rbkmoney.fraudo.constant.ResultStatus;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ContextConfiguration(classes = {EventP2PRepository.class, ScoresResultToEventConverter.class, ScoresResultToEventP2PConverter.class, ClickhouseConfig.class, DbP2pFieldResolver.class}, initializers = EventP2PRepositoryTest.Initializer.class)
public class EventP2PRepositoryTest {

    private static final String SELECT_COUNT_AS_CNT_FROM_FRAUD_EVENTS_UNIQUE = "SELECT count() as cnt from fraud.events_p_to_p";

    @ClassRule
    public static ClickHouseContainer clickHouseContainer = new ClickHouseContainer("yandex/clickhouse-server:19.17");

    @Autowired
    private EventP2PRepository eventP2PRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    ScoresResultToEventConverter scoresResultToEventConverter;

    @Autowired
    DbP2pFieldResolver dbP2pFieldResolver;

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
        sql = FileUtil.getFile("sql/V3__create_events_p2p.sql");
        split = sql.split(";");
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
        ScoresResult<P2PModel> scoresResult = createScoresResult(ResultStatus.ACCEPT, BeanUtil.createP2PModel());

        eventP2PRepository.insert(scoresResultToEventConverter.convert(scoresResult));

        Integer count = jdbcTemplate.queryForObject(SELECT_COUNT_AS_CNT_FROM_FRAUD_EVENTS_UNIQUE,
                (resultSet, i) -> resultSet.getInt("cnt"));

        Assert.assertEquals(1, count.intValue());
    }


    @Test
    public void insertBatch() throws SQLException {
        List<ScoresResult<P2PModel>> batch = createBatch();

        List<EventP2P> collect = batch.stream()
                .map(scoresResultToEventConverter::convert)
                .collect(Collectors.toList());

        eventP2PRepository.insertBatch(collect);

        Integer count = jdbcTemplate.queryForObject(SELECT_COUNT_AS_CNT_FROM_FRAUD_EVENTS_UNIQUE,
                (resultSet, i) -> resultSet.getInt("cnt"));

        Assert.assertEquals(2, count.intValue());
    }

    @NotNull
    private List<ScoresResult<P2PModel>> createBatch() {
        ScoresResult<P2PModel> value = createScoresResult(ResultStatus.ACCEPT, BeanUtil.createP2PModel());
        ScoresResult<P2PModel> value2 = createScoresResult(ResultStatus.DECLINE, BeanUtil.createP2PModelSecond());
        return List.of(value, value2);
    }

    @NotNull
    private ScoresResult<P2PModel> createScoresResult(ResultStatus status, P2PModel p2PModel) {
        ScoresResult<P2PModel> value2 = new ScoresResult<>();
        CheckedResultModel resultModel = new CheckedResultModel();
        resultModel.setResultModel(new ResultModel(status, "test", null));
        resultModel.setCheckedTemplate("RULE");
        value2.setRequest(p2PModel);
        HashMap<String, CheckedResultModel> map = new HashMap<>();
        map.put(ScoresType.FRAUD, resultModel);
        value2.setScores(map);
        return value2;
    }

    @Test
    public void countOperationByEmailTest() throws SQLException {
        List<ScoresResult<P2PModel>> batch = createBatch();

        List<EventP2P> collect = batch.stream()
                .map(scoresResultToEventConverter::convert)
                .collect(Collectors.toList());

        eventP2PRepository.insertBatch(collect);

        Instant now = Instant.now();
        Long to = TimestampUtil.generateTimestampNowMillis(now);
        Long from = TimestampUtil.generateTimestampMinusMinutesMillis(now, 10L);

        List<Map<String, Object>> maps = jdbcTemplate.queryForList("SELECT * from fraud.events_p_to_p");
        maps.forEach(stringObjectMap -> System.out.println(stringObjectMap));

        int count = eventP2PRepository.countOperationByField(EventP2PField.email.name(), BeanUtil.EMAIL, from, to);
        Assert.assertEquals(1, count);
    }

    @Test
    public void countOperationByEmailTestWithGroupBy() throws SQLException {

        ScoresResult<P2PModel> value = createScoresResult(ResultStatus.ACCEPT, BeanUtil.createP2PModel());
        ScoresResult<P2PModel> value2 = createScoresResult(ResultStatus.DECLINE, BeanUtil.createP2PModelSecond());
        P2PModel p2PModelSecond = BeanUtil.createP2PModelSecond();
        p2PModelSecond.setIdentityId("test");

        ScoresResult<P2PModel> value3 = createScoresResult(ResultStatus.DECLINE, p2PModelSecond);

        eventP2PRepository.insertBatch(scoresResultToEventConverter.convertBatch(List.of(value, value2, value3)));

        Instant now = Instant.now();
        Long to = TimestampUtil.generateTimestampNowMillis(now);
        Long from = TimestampUtil.generateTimestampMinusMinutesMillis(now, 10L);

        FieldModel email = dbP2pFieldResolver.resolve(P2PCheckedField.EMAIL, p2PModelSecond);
        int count = eventP2PRepository.countOperationByFieldWithGroupBy(EventP2PField.email.name(), email.getValue(), from, to, List.of());
        Assert.assertEquals(2, count);

        FieldModel resolve = dbP2pFieldResolver.resolve(P2PCheckedField.IDENTITY_ID, p2PModelSecond);
        count = eventP2PRepository.countOperationByFieldWithGroupBy(EventP2PField.email.name(), email.getValue(), from, to, List.of(resolve));
        Assert.assertEquals(1, count);
    }

    @Test
    public void sumOperationByEmailTest() throws SQLException {
        List<ScoresResult<P2PModel>> batch = createBatch();

        eventP2PRepository.insertBatch(scoresResultToEventConverter.convertBatch(batch));

        Instant now = Instant.now();
        Long to = TimestampUtil.generateTimestampNowMillis(now);
        Long from = TimestampUtil.generateTimestampMinusMinutesMillis(now, 10L);

        Long sum = eventP2PRepository.sumOperationByFieldWithGroupBy(EventP2PField.email.name(), BeanUtil.EMAIL, from, to, List.of());
        Assert.assertEquals(BeanUtil.AMOUNT_FIRST, sum);
    }

    @Test
    public void countUniqOperationTest() {
        ScoresResult<P2PModel> value = createScoresResult(ResultStatus.ACCEPT, BeanUtil.createP2PModel());
        ScoresResult<P2PModel> value2 = createScoresResult(ResultStatus.DECLINE, BeanUtil.createP2PModelSecond());
        ScoresResult<P2PModel> value3 = createScoresResult(ResultStatus.DECLINE, BeanUtil.createP2PModel());
        P2PModel p2pModel = BeanUtil.createP2PModel();
        p2pModel.setFingerprint("test");
        ScoresResult<P2PModel> value4 = createScoresResult(ResultStatus.DECLINE, p2pModel);
        eventP2PRepository.insertBatch(scoresResultToEventConverter.convertBatch(List.of(value, value2, value3, value4)));

        Instant now = Instant.now();
        Long to = TimestampUtil.generateTimestampNowMillis(now);
        Long from = TimestampUtil.generateTimestampMinusMinutesMillis(now, 10L);

        Integer sum = eventP2PRepository.uniqCountOperation(EventP2PField.email.name(), BeanUtil.EMAIL, EventP2PField.fingerprint.name(), from, to);
        Assert.assertEquals(Integer.valueOf(2), sum);
    }

    @Test
    public void countUniqOperationWithGroupByTest() {
        ScoresResult<P2PModel> value = createScoresResult(ResultStatus.ACCEPT, BeanUtil.createP2PModel());
        ScoresResult<P2PModel> value2 = createScoresResult(ResultStatus.DECLINE, BeanUtil.createP2PModelSecond());
        ScoresResult<P2PModel> value3 = createScoresResult(ResultStatus.DECLINE, BeanUtil.createP2PModel());
        P2PModel p2pModel = BeanUtil.createP2PModel();
        p2pModel.setFingerprint("test");
        p2pModel.setIdentityId("identity");
        ScoresResult<P2PModel> value4 = createScoresResult(ResultStatus.DECLINE, p2pModel);

        eventP2PRepository.insertBatch(scoresResultToEventConverter.convertBatch(List.of(value, value2, value3, value4)));

        Instant now = Instant.now();
        Long to = TimestampUtil.generateTimestampNowMillis(now);
        Long from = TimestampUtil.generateTimestampMinusMinutesMillis(now, 10L);
        Integer sum = eventP2PRepository.uniqCountOperationWithGroupBy(EventP2PField.email.name(), BeanUtil.EMAIL, EventP2PField.fingerprint.name(), from, to, List.of());
        Assert.assertEquals(Integer.valueOf(2), sum);

        FieldModel resolve = dbP2pFieldResolver.resolve(P2PCheckedField.IDENTITY_ID, p2pModel);
        sum = eventP2PRepository.uniqCountOperationWithGroupBy(EventP2PField.email.name(), BeanUtil.EMAIL, EventP2PField.fingerprint.name(), from, to, List.of(resolve));
        Assert.assertEquals(Integer.valueOf(1), sum);
    }

}