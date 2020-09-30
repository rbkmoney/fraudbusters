package com.rbkmoney.fraudbusters.repository;

import com.rbkmoney.clickhouse.initializer.ChInitializer;
import com.rbkmoney.damsel.geo_ip.GeoIpServiceSrv;
import com.rbkmoney.fraudbusters.config.ClickhouseConfig;
import com.rbkmoney.fraudbusters.constant.EventField;
import com.rbkmoney.fraudbusters.converter.FraudResultToEventConverter;
import com.rbkmoney.fraudbusters.domain.*;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.FieldModel;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.DBPaymentFieldResolver;
import com.rbkmoney.fraudbusters.repository.impl.AggregationGeneralRepositoryImpl;
import com.rbkmoney.fraudbusters.repository.impl.FraudResultRepository;
import com.rbkmoney.fraudbusters.util.BeanUtil;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import com.rbkmoney.fraudo.constant.ResultStatus;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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

import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

@Slf4j
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ContextConfiguration(classes = {FraudResultToEventConverter.class, ClickhouseConfig.class,
        DBPaymentFieldResolver.class, AggregationGeneralRepositoryImpl.class, FraudResultRepository.class},
        initializers = FraudResultRepositoryTest.Initializer.class)
public class FraudResultRepositoryTest {

    private static final String SELECT_COUNT_AS_CNT_FROM_FRAUD_EVENTS_UNIQUE = "SELECT count() as cnt from fraud.events_unique";

    @ClassRule
    public static ClickHouseContainer clickHouseContainer = new ClickHouseContainer("yandex/clickhouse-server:19.17");

    @Autowired
    private FraudResultRepository fraudResultRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    FraudResultToEventConverter fraudResultToEventConverter;

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
        }
    }

    @Before
    public void setUp() throws Exception {
        ChInitializer.initAllScripts(clickHouseContainer, List.of("sql/db_init.sql",
                "sql/V2__create_events_p2p.sql",
                "sql/V3__create_fraud_payments.sql",
                "sql/V4__create_payment.sql",
                "sql/V5__add_fields.sql",
                "sql/V6__add_result_fields_payment.sql",
                "sql/V7__add_fields.sql"));
    }

    @Test
    public void insertBatch() throws SQLException {
        fraudResultRepository.insertBatch(
                createBatch().stream()
                        .map(fraudResultToEventConverter::convert)
                        .collect(Collectors.toList())
        );

        Integer count = jdbcTemplate.queryForObject(SELECT_COUNT_AS_CNT_FROM_FRAUD_EVENTS_UNIQUE,
                (resultSet, i) -> resultSet.getInt("cnt"));

        assertEquals(2, count.intValue());
    }

    @NotNull
    private List<FraudResult> createBatch() {
        FraudResult value = createFraudResult(ResultStatus.ACCEPT, BeanUtil.createPaymentModel());
        FraudResult value2 = createFraudResult(ResultStatus.DECLINE, BeanUtil.createFraudModelSecond());
        return List.of(value, value2);
    }

    @NotNull
    private FraudResult createFraudResult(ResultStatus decline, PaymentModel paymentModel) {
        FraudResult value2 = new FraudResult();
        CheckedResultModel resultModel = new CheckedResultModel();
        resultModel.setResultModel(new ConcreteResultModel(decline, "test",null));
        resultModel.setCheckedTemplate("RULE");

        value2.setResultModel(resultModel);
        FraudRequest fraudRequest = new FraudRequest();
        fraudRequest.setFraudModel(paymentModel);
        Metadata metadata = new Metadata();
        fraudRequest.setMetadata(metadata);
        value2.setFraudRequest(fraudRequest);
        return value2;
    }

    @Test
    public void countOperationByEmailTest() throws SQLException {
        Instant now = Instant.now();
        Long to = TimestampUtil.generateTimestampNowMillis(now);
        Long from = TimestampUtil.generateTimestampMinusMinutesMillis(now, 10L);
        List<FraudResult> batch = createBatch();
        fraudResultRepository.insertBatch(fraudResultToEventConverter.convertBatch(batch));

        int count = fraudResultRepository.countOperationByField(EventField.email.name(), BeanUtil.EMAIL, from, to);
        assertEquals(1, count);
    }

    @Test
    public void countOperationByEmailTestWithGroupBy() throws SQLException {
        PaymentModel paymentModel = BeanUtil.createFraudModelSecond();
        paymentModel.setPartyId("test");
        fraudResultRepository.insertBatch(fraudResultToEventConverter
                .convertBatch(List.of(
                        createFraudResult(ResultStatus.ACCEPT, BeanUtil.createPaymentModel()),
                        createFraudResult(ResultStatus.DECLINE, BeanUtil.createFraudModelSecond()),
                        createFraudResult(ResultStatus.ACCEPT, paymentModel),
                        createFraudResult(ResultStatus.DECLINE, paymentModel))
                )
        );

        Instant now = Instant.now().plusSeconds(30L);
        Long to = TimestampUtil.generateTimestampNowMillis(now);
        Long from = TimestampUtil.generateTimestampMinusMinutesMillis(now, 10L);

        FieldModel email = DBPaymentFieldResolver.resolve(PaymentCheckedField.EMAIL, paymentModel);
        int count = fraudResultRepository.countOperationByFieldWithGroupBy(EventField.email.name(), email.getValue(), from, to, List.of());
        assertEquals(3, count);

        count = fraudResultRepository.countOperationSuccessWithGroupBy(EventField.email.name(), email.getValue(), from, to, List.of());
        assertEquals(1, count);

        FieldModel resolve = DBPaymentFieldResolver.resolve(PaymentCheckedField.PARTY_ID, paymentModel);
        count = fraudResultRepository.countOperationByFieldWithGroupBy(EventField.email.name(), email.getValue(), from, to, List.of(resolve));
        assertEquals(2, count);

        count = fraudResultRepository.countOperationSuccessWithGroupBy(EventField.email.name(), email.getValue(), from, to, List.of(resolve));
        assertEquals(1, count);
    }

    @Test
    public void sumOperationByEmailTest() throws SQLException {
        fraudResultRepository.insertBatch(fraudResultToEventConverter.convertBatch(createBatch()));

        Instant now = Instant.now();
        Long to = TimestampUtil.generateTimestampNowMillis(now);
        Long from = TimestampUtil.generateTimestampMinusMinutesMillis(now, 10L);
        Long sum = fraudResultRepository.sumOperationByFieldWithGroupBy(EventField.email.name(), BeanUtil.EMAIL, from, to, List.of());
        assertEquals(BeanUtil.AMOUNT_FIRST, sum);

        sum = fraudResultRepository.sumOperationSuccessWithGroupBy(EventField.email.name(), BeanUtil.EMAIL, from, to, List.of());
        assertEquals(BeanUtil.AMOUNT_FIRST, sum);
    }

    @Test
    public void countUniqOperationTest() {
        PaymentModel paymentModel = BeanUtil.createPaymentModel();
        paymentModel.setFingerprint("test");
        fraudResultRepository.insertBatch(fraudResultToEventConverter
                .convertBatch(List.of(
                        createFraudResult(ResultStatus.ACCEPT, BeanUtil.createPaymentModel()),
                        createFraudResult(ResultStatus.DECLINE, BeanUtil.createFraudModelSecond()),
                        createFraudResult(ResultStatus.DECLINE, BeanUtil.createPaymentModel()),
                        createFraudResult(ResultStatus.DECLINE, paymentModel))
                )
        );

        Instant now = Instant.now();
        Long to = TimestampUtil.generateTimestampNowMillis(now);
        Long from = TimestampUtil.generateTimestampMinusMinutesMillis(now, 10L);
        Integer sum = fraudResultRepository.uniqCountOperation(EventField.email.name(), BeanUtil.EMAIL, EventField.fingerprint.name(), from, to);
        assertEquals(Integer.valueOf(2), sum);
    }

    @Test
    public void countUniqOperationWithGroupByTest() {
        PaymentModel paymentModel = BeanUtil.createPaymentModel();
        paymentModel.setFingerprint("test");
        paymentModel.setPartyId("party");

        fraudResultRepository.insertBatch(fraudResultToEventConverter
                .convertBatch(List.of(
                        createFraudResult(ResultStatus.ACCEPT, BeanUtil.createPaymentModel()),
                        createFraudResult(ResultStatus.DECLINE, BeanUtil.createFraudModelSecond()),
                        createFraudResult(ResultStatus.DECLINE, BeanUtil.createPaymentModel()),
                        createFraudResult(ResultStatus.DECLINE, paymentModel))
                )
        );

        Instant now = Instant.now();
        Long to = TimestampUtil.generateTimestampNowMillis(now);
        Long from = TimestampUtil.generateTimestampMinusMinutesMillis(now, 10L);
        Integer sum = fraudResultRepository.uniqCountOperationWithGroupBy(EventField.email.name(), BeanUtil.EMAIL, EventField.fingerprint.name(), from, to, List.of());
        assertEquals(Integer.valueOf(2), sum);

        FieldModel resolve = DBPaymentFieldResolver.resolve(PaymentCheckedField.PARTY_ID, paymentModel);
        sum = fraudResultRepository.uniqCountOperationWithGroupBy(EventField.email.name(), BeanUtil.EMAIL, EventField.fingerprint.name(), from, to, List.of(resolve));
        assertEquals(Integer.valueOf(1), sum);
    }

}
