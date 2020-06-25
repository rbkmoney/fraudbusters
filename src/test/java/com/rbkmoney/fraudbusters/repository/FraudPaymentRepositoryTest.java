package com.rbkmoney.fraudbusters.repository;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.fraudbusters.FraudInfo;
import com.rbkmoney.damsel.fraudbusters.FraudPayment;
import com.rbkmoney.damsel.fraudbusters.PaymentStatus;
import com.rbkmoney.damsel.geo_ip.GeoIpServiceSrv;
import com.rbkmoney.fraudbusters.config.ClickhouseConfig;
import com.rbkmoney.fraudbusters.constant.EventField;
import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import com.rbkmoney.fraudbusters.domain.FraudRequest;
import com.rbkmoney.fraudbusters.domain.Metadata;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.FieldModel;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.DBPaymentFieldResolver;
import com.rbkmoney.fraudbusters.repository.impl.AggregationGeneralRepositoryImpl;
import com.rbkmoney.fraudbusters.repository.impl.FraudPaymentRepository;
import com.rbkmoney.fraudbusters.util.BeanUtil;
import com.rbkmoney.fraudbusters.util.ChInitializer;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import com.rbkmoney.fraudo.constant.ResultStatus;
import com.rbkmoney.fraudo.model.ResultModel;
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

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static com.rbkmoney.fraudbusters.util.ChInitializer.execAllInFile;
import static org.junit.Assert.assertEquals;

@Slf4j
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ContextConfiguration(classes = {ClickhouseConfig.class,
        DBPaymentFieldResolver.class, AggregationGeneralRepositoryImpl.class, FraudPaymentRepository.class},
        initializers = FraudPaymentRepositoryTest.Initializer.class)
public class FraudPaymentRepositoryTest {

    private static final String SELECT_COUNT_AS_CNT_FROM_FRAUD_EVENTS_UNIQUE = "SELECT count() as cnt from fraud.fraud_payment";

    @ClassRule
    public static ClickHouseContainer clickHouseContainer = new ClickHouseContainer("yandex/clickhouse-server:19.17");

    @Autowired
    private FraudPaymentRepository FraudPaymentRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
            execAllInFile(connection, "sql/V3__create_fraud_payments.sql");
        }
    }

    @Before
    public void setUp() throws Exception {
        initDb();
    }


    @Test
    public void insertBatch() throws SQLException {
        FraudPaymentRepository.insertBatch(createBatch());

        Integer count = jdbcTemplate.queryForObject(SELECT_COUNT_AS_CNT_FROM_FRAUD_EVENTS_UNIQUE,
                (resultSet, i) -> resultSet.getInt("cnt"));

        assertEquals(2, count.intValue());
    }

    @NotNull
    private List<FraudPayment> createBatch() {
        FraudPayment value = createFraudPayment("inv1.1");
        FraudPayment value2 = createFraudPayment("inv2.1");
        return List.of(value, value2);
    }

    @NotNull
    private FraudPayment createFraudPayment(String id) {
        return new FraudPayment()
                .setId(id)
                .setLastChangeTime("2016-03-22T06:12:27Z")
                .setPartyId("party_id")
                .setShopId("shop_id")
                .setCost(new Cash()
                        .setAmount(124L)
                        .setCurrency(new CurrencyRef()
                                .setSymbolicCode("RUB")))
                .setPayer(Payer.payment_resource(
                        new PaymentResourcePayer()
                                .setResource(new DisposablePaymentResource()
                                        .setPaymentTool(PaymentTool.bank_card(new BankCard()
                                                .setPaymentSystem(BankCardPaymentSystem.amex)
                                                .setLastDigits("4242")
                                                .setIssuerCountry(Residence.ABH)
                                                .setBin("520045")
                                                .setToken("nsjfvbeirfi")))
                                        .setClientInfo(new ClientInfo().setIpAddress("127.1.1.1")
                                                .setFingerprint("12nj23njL23:4343nk:ff")))
                                .setContactInfo(new ContactInfo()
                                        .setEmail("kek@kek.ru"))))
                .setStatus(PaymentStatus.captured)
                .setRrn("rrrrrn")
                .setRoute(new PaymentRoute()
                        .setTerminal(new TerminalRef(123))
                        .setProvider(new ProviderRef(567)))
                .setFraudInfo(new FraudInfo()
                        .setTempalteId("template_id")
                        .setDescription("desc"));
    }



}