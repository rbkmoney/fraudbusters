package com.rbkmoney.fraudbusters.repository;

import com.rbkmoney.fraudbusters.config.ClickhouseConfig;
import com.rbkmoney.fraudbusters.domain.FraudResult;
import com.rbkmoney.fraudbusters.util.BeanUtil;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.List;
import java.util.TimeZone;

@Slf4j
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ContextConfiguration(classes = {FraudResultRepository.class, ClickhouseConfig.class}, initializers = FraudResultRepositoryTest.Initializer.class)
public class FraudResultRepositoryTest {

    @ClassRule
    public static ClickHouseContainer clickHouseContainer = new ClickHouseContainer();

    private DateFormat dateFormat;

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
        connection.createStatement().execute("CREATE DATABASE IF NOT EXISTS fraud");
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getDefault());

        connection.createStatement().execute("DROP TABLE IF EXISTS fraud.events_unique");
        connection.createStatement().execute(
                "create table fraud.events_unique (" +
                        "timestamp Date," +
                        "shopId String," +
                        "partyId String," +
                        "ip String," +
                        "email String," +
                        "bin String," +
                        "fingerprint String," +
                        "resultStatus String," +
                        "eventTime UInt64" +
                        ") ENGINE = MergeTree(timestamp, (shopId, partyId, ip, email, bin, fingerprint, resultStatus), 8192);"
        );
        connection.close();

    }

    private Connection getSystemConn() throws SQLException {
        ClickHouseProperties properties = new ClickHouseProperties();
        ClickHouseDataSource dataSource = new ClickHouseDataSource(clickHouseContainer.getJdbcUrl(), properties);
        return dataSource.getConnection();
    }

    @Test
    public void insert() throws SQLException {
        FraudResult value = new FraudResult();
        value.setResultStatus(ResultStatus.ACCEPT);
        FraudModel fraudModel = BeanUtil.createFraudModel();
        value.setFraudModel(fraudModel);
        fraudResultRepository.insert(value);

        int count = jdbcTemplate.queryForObject("SELECT count() as cnt from fraud.events_unique",
                (resultSet, i) -> resultSet.getInt("cnt"));
        Assert.assertEquals(1, count);
    }


    @Test
    public void insertBatch() throws SQLException {
        List<FraudResult> batch = createBatch();
        fraudResultRepository.insertBatch(batch);

        int count = jdbcTemplate.queryForObject("SELECT count() as cnt from fraud.events_unique",
                (resultSet, i) -> resultSet.getInt("cnt"));

        Assert.assertEquals(2, count);
    }

    @NotNull
    private List<FraudResult> createBatch() {
        FraudResult value = new FraudResult();
        value.setResultStatus(ResultStatus.ACCEPT);
        FraudModel fraudModel = BeanUtil.createFraudModel();
        value.setFraudModel(fraudModel);
        FraudResult value2 = new FraudResult();
        value2.setResultStatus(ResultStatus.DECLINE);
        FraudModel fraudModel2 = BeanUtil.createFraudModelSecond();
        value2.setFraudModel(fraudModel2);
        return List.of(value, value2);
    }

    @Test
    public void countOperationByEmail() throws SQLException {
        long from = Instant.now().toEpochMilli();
        List<FraudResult> batch = createBatch();
        fraudResultRepository.insertBatch(batch);
        long to = Instant.now().toEpochMilli();
        int count = fraudResultRepository.countOperationByEmail(BeanUtil.EMAIL, from, to);
        Assert.assertEquals(1, count);
    }
}