package com.rbkmoney.fraudbusters;

import com.rbkmoney.damsel.domain.RiskScore;
import com.rbkmoney.damsel.p2p_insp.InspectResult;
import com.rbkmoney.fraudbusters.util.BeanUtil;
import com.rbkmoney.woody.thrift.impl.http.THClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Slf4j
@ActiveProfiles("full-prod")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT,
        classes = FraudBustersApplication.class,
        properties = "kafka.listen.result.concurrency=1")
public class P2PEndToEndIntegrationTest extends JUnit5IntegrationTest {

    public static final long TIMEOUT = 2000L;
    public static final String FRAUD = "fraud";
    public static final String IDENT_ID = "identId";

    private static final String TEMPLATE = """
            rule: count("email", 10, 0, "identity_id") > 1  AND count("email", 10) < 3
             AND sum("email", 10) >= 18000
             AND count("card_token_from", 10) > 1
             AND in(countryBy("country_bank"), "RUS")
             -> decline;
            """;
    private static final String SERVICE_P2P_URL = "http://localhost:%s/fraud_p2p_inspector/v1";

    @LocalServerPort
    int serverPort;

    @BeforeEach
    public void init() throws ExecutionException, InterruptedException, SQLException, TException {

        String globalRef = UUID.randomUUID().toString();
        produceTemplate(globalRef, TEMPLATE, kafkaTopics.getP2pTemplate());
        produceP2PReference(true, null, globalRef);

        waitingTopic(kafkaTopics.getP2pTemplate());
        Mockito.when(geoIpServiceSrv.getLocationIsoCode(any())).thenReturn("RUS");

        Thread.sleep(TIMEOUT * 3);
    }

    @Test
    public void testP2P()
            throws URISyntaxException, TException, InterruptedException {
        THClientBuilder clientBuilder = new THClientBuilder()
                .withAddress(new URI(String.format(SERVICE_P2P_URL, serverPort)))
                .withNetworkTimeout(300000);

        com.rbkmoney.damsel.p2p_insp.InspectorProxySrv.Iface client =
                clientBuilder.build(com.rbkmoney.damsel.p2p_insp.InspectorProxySrv.Iface.class);
        com.rbkmoney.damsel.p2p_insp.Context p2PContext = BeanUtil.createP2PContext(IDENT_ID, "transfer_1");

        InspectResult inspectResult = client.inspectTransfer(p2PContext, List.of(FRAUD));
        assertEquals(RiskScore.high, inspectResult.scores.get(FRAUD));

        Thread.sleep(TIMEOUT);

        p2PContext = BeanUtil.createP2PContext(IDENT_ID, "transfer_1");
        inspectResult = client.inspectTransfer(p2PContext, List.of(FRAUD));
        assertEquals(RiskScore.fatal, inspectResult.scores.get(FRAUD));
    }

}
