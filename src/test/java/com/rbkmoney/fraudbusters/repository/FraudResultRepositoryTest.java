package com.rbkmoney.fraudbusters.repository;

import com.rbkmoney.fraudbusters.config.ClickhouseConfig;
import com.rbkmoney.fraudbusters.domain.FraudResult;
import com.rbkmoney.fraudbusters.util.BeanUtil;
import com.rbkmoney.fraudbusters.util.FileUtil;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import com.rbkmoney.fraudo.constant.ResultStatus;
import com.rbkmoney.fraudo.model.FraudModel;
import com.rbkmoney.fraudo.model.ResultModel;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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

@Slf4j
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ContextConfiguration(classes = {FraudResultRepository.class, ClickhouseConfig.class}, initializers = FraudResultRepositoryTest.Initializer.class)
public class FraudResultRepositoryTest {

    public static final String SELECT_COUNT_AS_CNT_FROM_FRAUD_EVENTS_UNIQUE = "SELECT count() as cnt from fraud.events_unique";
    @ClassRule
    public static ClickHouseContainer clickHouseContainer = new ClickHouseContainer();

    @Autowired
    private FraudResultRepository fraudResultRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues
                    .of("clickhouse.db.url=" + clickHouseContainer.getJdbcUrl())
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
        fraudResultRepository.insert(value);

        Integer count = jdbcTemplate.queryForObject(SELECT_COUNT_AS_CNT_FROM_FRAUD_EVENTS_UNIQUE,
                (resultSet, i) -> resultSet.getInt("cnt"));
        Assert.assertEquals(1, count.intValue());
    }


    @Test
    public void insertBatch() throws SQLException {
        List<FraudResult> batch = createBatch();
        fraudResultRepository.insertBatch(batch);

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
    private FraudResult createFraudResult(ResultStatus decline, FraudModel fraudModelSecond) {
        FraudResult value2 = new FraudResult();
        value2.setResultModel(new ResultModel(decline, null));
        FraudModel fraudModel2 = fraudModelSecond;
        value2.setFraudModel(fraudModel2);
        return value2;
    }

    @Test
    public void countOperationByEmail() throws SQLException {
        Instant now = Instant.now();
        Long to = TimestampUtil.generateTimestampNow(now);
        Long from = TimestampUtil.generateTimestampMinusMinutes(now, 10L);
        List<FraudResult> batch = createBatch();
        fraudResultRepository.insertBatch(batch);

        int count = fraudResultRepository.countOperationByEmail(BeanUtil.EMAIL, from, to);
        Assert.assertEquals(1, count);
    }

    @Test
    public void countOperationByEmailSuccess() throws SQLException {
        Instant now = Instant.now();
        Long to = TimestampUtil.generateTimestampNow(now);
        Long from = TimestampUtil.generateTimestampMinusMinutes(now, 10L);
        List<FraudResult> batch = createBatch();
        fraudResultRepository.insertBatch(batch);

        int count = fraudResultRepository.countOperationByEmailSuccess(BeanUtil.EMAIL, from, to);
        Assert.assertEquals(1, count);
    }

    @Test
    public void countOperationByEmailError() throws SQLException {
        Instant now = Instant.now();
        Long to = TimestampUtil.generateTimestampNow(now);
        Long from = TimestampUtil.generateTimestampMinusMinutes(now, 10L);
        FraudResult value = createFraudResult(ResultStatus.ACCEPT, BeanUtil.createFraudModel());
        FraudResult value2 = createFraudResult(ResultStatus.DECLINE, BeanUtil.createFraudModelSecond());
        FraudResult value3 = createFraudResult(ResultStatus.DECLINE, BeanUtil.createFraudModel());
        fraudResultRepository.insertBatch(List.of(value, value2, value3));

        int count = fraudResultRepository.countOperationByEmailError(BeanUtil.EMAIL, from, to);
        Assert.assertEquals(1, count);
    }

}