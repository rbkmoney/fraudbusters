package com.rbkmoney.fraudbusters.repository;

import com.rbkmoney.damsel.geo_ip.GeoIpServiceSrv;
import com.rbkmoney.fraudbusters.config.ClickhouseConfig;
import com.rbkmoney.fraudbusters.constant.EventField;
import com.rbkmoney.fraudbusters.converter.FraudResultToEventConverter;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.FieldModel;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.DBPaymentFieldResolver;
import com.rbkmoney.fraudbusters.repository.impl.AggregationGeneralRepositoryImpl;
import com.rbkmoney.fraudbusters.repository.impl.AnalyticRepository;
import com.rbkmoney.fraudbusters.repository.impl.FraudResultRepository;
import com.rbkmoney.fraudbusters.repository.source.SourcePool;
import com.rbkmoney.fraudbusters.util.BeanUtil;
import com.rbkmoney.fraudbusters.util.ChInitializer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.ClickHouseContainer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static com.rbkmoney.fraudbusters.util.ChInitializer.execAllInFile;
import static org.junit.Assert.assertEquals;

@Slf4j
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ContextConfiguration(classes = {AnalyticRepository.class, FraudResultToEventConverter.class, ClickhouseConfig.class,
        DBPaymentFieldResolver.class, AggregationGeneralRepositoryImpl.class, FraudResultRepository.class, SourcePool.class},
        initializers = AnalyticRepositoryTest.Initializer.class)
public class AnalyticRepositoryTest {

    private static final String SELECT_COUNT_AS_CNT_FROM_FRAUD_EVENTS_UNIQUE = "SELECT count() as cnt from fraud.events_unique";
    public static final long FROM = 1588761200000L;
    public static final long TO = 1588761209000L;

    @ClassRule
    public static ClickHouseContainer clickHouseContainer = new ClickHouseContainer("yandex/clickhouse-server:19.17");

    @Autowired
    private AnalyticRepository analyticRepository;

    @Autowired
    DBPaymentFieldResolver DBPaymentFieldResolver;

    @MockBean
    GeoIpServiceSrv.Iface iface;

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @SneakyThrows
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            log.info("clickhouse.db.url={}", clickHouseContainer.getJdbcUrl());
            TestPropertyValues
                    .of("clickhouse.db.url=" + clickHouseContainer.getJdbcUrl(),
                            "clickhouse.db.user=" + clickHouseContainer.getUsername(),
                            "clickhouse.db.password=" + clickHouseContainer.getPassword())
                    .applyTo(configurableApplicationContext.getEnvironment());

            initDb();
        }
    }

    private static void initDb() throws SQLException {
        try (Connection connection = ChInitializer.getSystemConn(clickHouseContainer)) {
            execAllInFile(connection, "sql/db_init.sql");
            execAllInFile(connection, "sql/TEST_analytics_data.sql");
            execAllInFile(connection, "sql/data/inserts_event_sink.sql");
        }
    }

    @Test
    public void countOperationByEmailTest() throws SQLException {
        int count = analyticRepository.countOperationByField(EventField.email.name(), BeanUtil.EMAIL, FROM, TO);
        assertEquals(1, count);
    }

    @Test
    public void countOperationByEmailTestWithGroupBy() throws SQLException {
        PaymentModel paymentModel = BeanUtil.createFraudModelSecond();

        FieldModel email = DBPaymentFieldResolver.resolve(PaymentCheckedField.EMAIL, paymentModel);
        int count = analyticRepository.countOperationByFieldWithGroupBy(EventField.email.name(), email.getValue(),
                1588761200000L, 1588761209000L, List.of());
        assertEquals(2, count);

        FieldModel resolve = DBPaymentFieldResolver.resolve(PaymentCheckedField.PARTY_ID, paymentModel);
        count = analyticRepository.countOperationByFieldWithGroupBy(EventField.email.name(), email.getValue(),
                1588761200000L, 1588761209000L, List.of(resolve));
        assertEquals(1, count);

        count = analyticRepository.countOperationSuccessWithGroupBy(EventField.email.name(), email.getValue(),
                1588761200000L, 1588761209000L, List.of(resolve));
        assertEquals(1, count);

        count = analyticRepository.countOperationErrorWithGroupBy(EventField.email.name(), email.getValue(),
                1588761200000L, 1588761209000L, List.of(resolve), "");
        assertEquals(0, count);
    }

    @Test
    public void sumOperationByEmailTest() throws SQLException {
        Long sum = analyticRepository.sumOperationByFieldWithGroupBy(EventField.email.name(), BeanUtil.EMAIL, FROM, TO, List.of());
        assertEquals(BeanUtil.AMOUNT_FIRST, sum);

        sum = analyticRepository.sumOperationSuccessWithGroupBy(EventField.email.name(), BeanUtil.EMAIL, FROM, TO, List.of());
        assertEquals(BeanUtil.AMOUNT_FIRST, sum);

        sum = analyticRepository.sumOperationErrorWithGroupBy(EventField.email.name(), BeanUtil.EMAIL, FROM, TO, List.of(), "");
        assertEquals(0L, sum.longValue());
    }

    @Test
    public void countUniqOperationTest() {
        Integer sum = analyticRepository.uniqCountOperation(EventField.email.name(), BeanUtil.EMAIL + BeanUtil.SUFIX,
                EventField.fingerprint.name(), FROM, TO);
        assertEquals(Integer.valueOf(2), sum);
    }

    @Test
    public void countUniqOperationWithGroupByTest() {
        PaymentModel paymentModel = BeanUtil.createFraudModelSecond();
        Integer sum = analyticRepository.uniqCountOperationWithGroupBy(EventField.email.name(), BeanUtil.EMAIL + BeanUtil.SUFIX,
                EventField.fingerprint.name(), FROM, TO, List.of());
        assertEquals(Integer.valueOf(2), sum);

        FieldModel resolve = DBPaymentFieldResolver.resolve(PaymentCheckedField.PARTY_ID, paymentModel);
        sum = analyticRepository.uniqCountOperationWithGroupBy(EventField.email.name(), BeanUtil.EMAIL + BeanUtil.SUFIX,
                EventField.fingerprint.name(), FROM, TO, List.of(resolve));
        assertEquals(Integer.valueOf(1), sum);
    }

}