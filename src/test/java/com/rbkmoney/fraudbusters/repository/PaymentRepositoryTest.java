package com.rbkmoney.fraudbusters.repository;

import com.rbkmoney.clickhouse.initializer.ChInitializer;
import com.rbkmoney.damsel.geo_ip.GeoIpServiceSrv;
import com.rbkmoney.fraudbusters.config.ClickhouseConfig;
import com.rbkmoney.fraudbusters.constant.EventField;
import com.rbkmoney.fraudbusters.converter.FraudResultToEventConverter;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.FieldModel;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.DatabasePaymentFieldResolver;
import com.rbkmoney.fraudbusters.repository.clickhouse.impl.AggregationGeneralRepositoryImpl;
import com.rbkmoney.fraudbusters.repository.clickhouse.impl.PaymentRepositoryImpl;
import com.rbkmoney.fraudbusters.repository.clickhouse.mapper.CheckedPaymentMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.ClickHouseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.SQLException;
import java.util.List;

import static com.rbkmoney.fraudbusters.util.BeanUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@ActiveProfiles("full-prod")
@Testcontainers
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ContextConfiguration(classes = {PaymentRepositoryImpl.class, FraudResultToEventConverter.class, ClickhouseConfig.class,
        DatabasePaymentFieldResolver.class, AggregationGeneralRepositoryImpl.class, CheckedPaymentMapper.class},
        initializers = PaymentRepositoryTest.Initializer.class)
public class PaymentRepositoryTest {

    public static final long FROM = 1588761200000L;
    public static final long TO = 1588761209000L;

    @Container
    public static ClickHouseContainer clickHouseContainer =
            new ClickHouseContainer("yandex/clickhouse-server:19.17");
    @Autowired
    DatabasePaymentFieldResolver databasePaymentFieldResolver;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @MockBean
    GeoIpServiceSrv.Iface iface;
    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    public void countOperationByEmailTest() throws SQLException {
        int count = paymentRepository.countOperationByField(EventField.email.name(), EMAIL, FROM, TO);
        assertEquals(1, count);
    }

    @Test
    public void countOperationByEmailTestWithGroupBy() throws SQLException {
        PaymentModel paymentModel = createFraudModelSecond();

        FieldModel email = databasePaymentFieldResolver.resolve(PaymentCheckedField.EMAIL, paymentModel);
        int count = paymentRepository.countOperationByFieldWithGroupBy(EventField.email.name(), email.getValue(),
                1588761200000L, 1588761209000L, List.of()
        );
        assertEquals(2, count);

        FieldModel resolve = databasePaymentFieldResolver.resolve(PaymentCheckedField.PARTY_ID, paymentModel);
        count = paymentRepository.countOperationByFieldWithGroupBy(EventField.email.name(), email.getValue(),
                1588761200000L, 1588761209000L, List.of(resolve)
        );
        assertEquals(1, count);

        count = paymentRepository.countOperationSuccessWithGroupBy(EventField.email.name(), email.getValue(),
                1588761200000L, 1588761209000L, List.of(resolve)
        );
        assertEquals(1, count);

        count = paymentRepository.countOperationErrorWithGroupBy(EventField.email.name(), email.getValue(),
                1588761200000L, 1588761209000L, List.of(resolve), ""
        );
        assertEquals(0, count);
    }

    @Test
    public void sumOperationByEmailTest() throws SQLException {
        Long sum =
                paymentRepository.sumOperationByFieldWithGroupBy(EventField.email.name(), EMAIL, FROM, TO, List.of());
        assertEquals(AMOUNT_FIRST, sum);

        sum = paymentRepository.sumOperationSuccessWithGroupBy(EventField.email.name(), EMAIL, FROM, TO, List.of());
        assertEquals(AMOUNT_FIRST, sum);

        sum = paymentRepository.sumOperationErrorWithGroupBy(EventField.email.name(), EMAIL, FROM, TO, List.of(), "");
        assertEquals(0L, sum.longValue());
    }

    @Test
    public void countUniqOperationTest() {
        Integer sum = paymentRepository.uniqCountOperation(EventField.email.name(), EMAIL + SUFIX,
                EventField.fingerprint.name(), FROM, TO
        );
        assertEquals(Integer.valueOf(2), sum);
    }

    @Test
    public void countUniqOperationWithGroupByTest() {
        PaymentModel paymentModel = createFraudModelSecond();
        Integer sum = paymentRepository.uniqCountOperationWithGroupBy(EventField.email.name(), EMAIL + SUFIX,
                EventField.fingerprint.name(), FROM, TO, List.of()
        );
        assertEquals(Integer.valueOf(2), sum);

        FieldModel resolve = databasePaymentFieldResolver.resolve(PaymentCheckedField.PARTY_ID, paymentModel);
        sum = paymentRepository.uniqCountOperationWithGroupBy(EventField.email.name(), EMAIL + SUFIX,
                EventField.fingerprint.name(), FROM, TO, List.of(resolve)
        );
        assertEquals(Integer.valueOf(1), sum);
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @SneakyThrows
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            log.info("clickhouse.db.url={}", clickHouseContainer.getJdbcUrl());
            TestPropertyValues
                    .of(
                            "clickhouse.db.url=" + clickHouseContainer.getJdbcUrl(),
                            "clickhouse.db.user=" + clickHouseContainer.getUsername(),
                            "clickhouse.db.password=" + clickHouseContainer.getPassword()
                    )
                    .applyTo(configurableApplicationContext.getEnvironment());
            ChInitializer.initAllScripts(clickHouseContainer, List.of(
                    "sql/db_init.sql",
                    "sql/V2__create_events_p2p.sql",
                    "sql/V3__create_fraud_payments.sql",
                    "sql/V4__create_payment.sql",
                    "sql/V5__add_fields.sql",
                    "sql/V6__add_result_fields_payment.sql",
                    "sql/V7__add_fields.sql",
                    "sql/data/inserts_event_sink.sql"
            ));
        }
    }

}

