package com.rbkmoney.fraudbusters.service;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.rbkmoney.fraudbusters.client.FraudManagementClientImpl;
import com.rbkmoney.fraudbusters.config.RestTemplateConfig;
import com.rbkmoney.fraudbusters.config.properties.DefaultTemplateProperties;
import com.rbkmoney.fraudbusters.repository.impl.AggregationGeneralRepositoryImpl;
import com.rbkmoney.fraudbusters.repository.impl.FraudResultRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(SpringRunner.class)
@TestPropertySource(properties = {"fraud.management.url=http://127.0.0.1:8089"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ContextConfiguration(classes = {FraudManagementService.class,
        AggregationGeneralRepositoryImpl.class,
        FraudResultRepository.class,
        FraudManagementClientImpl.class,
        RestTemplateConfig.class,
        DefaultTemplateProperties.class})
public class FraudManagementServiceTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);

    @MockBean
    private FraudResultRepository fraudResultRepository;

    @MockBean
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FraudManagementService fraudManagementService;

    @Before
    public void setUp() {

        stubFor(post(urlEqualTo("/template/default"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json; charset=utf-8")
                        .withBody("[\"id\"]")));

        stubFor(get(anyUrl())
                .withQueryParam("shopId", equalTo("shopId_exists"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json; charset=utf-8")
                        .withBody("[{\"partyId\": \"p1\", \"shopId\": \"s1\", \"isGlobal\": \"false\"}]")));

        stubFor(get(anyUrl())
                .withQueryParam("shopId", equalTo("shopId_non_exists"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json; charset=utf-8")
                        .withBody("[]")));

    }

    @Test
    public void testCreateDefaultReference() {
       fraudManagementService.createDefaultReference("partyId", "shopId_exists");
        verify(1, getRequestedFor(anyUrl()));
        verify(0, postRequestedFor(urlEqualTo("/template/default")));

        fraudManagementService.createDefaultReference("partyId", "shopId_non_exists");
        verify(2, getRequestedFor(anyUrl()));
        verify(1, postRequestedFor(urlEqualTo("/template/default")));
    }

    @Test
    public void testIsNewShop(){
        MockitoAnnotations.initMocks(this);
        Mockito.when(fraudResultRepository.countOperationByField(anyString(), anyString(), anyLong(), anyLong())).thenReturn(0);
        fraudManagementService.isNewShop("s1");
        Mockito.verify(fraudResultRepository).countOperationByField(anyString(), anyString(), anyLong(), anyLong());
    }

}
