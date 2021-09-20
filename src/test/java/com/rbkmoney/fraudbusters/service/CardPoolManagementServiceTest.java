package com.rbkmoney.fraudbusters.service;

import com.rbkmoney.fraudbusters.config.ClickhouseConfig;
import com.rbkmoney.fraudbusters.fraud.pool.CardTokenPoolImpl;
import com.rbkmoney.fraudbusters.repository.impl.CommonQueryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ContextConfiguration(classes = {ClickhouseConfig.class})
@SpringBootTest(classes = {CardPoolManagementService.class, CardTokenPoolImpl.class,
        FileCardTokenManagementService.class})
class CardPoolManagementServiceTest {

    @Autowired
    CardPoolManagementService cardPoolManagementService;

    @Autowired
    CardTokenPoolImpl cardTokenPool;

    @Autowired
    FileCardTokenManagementService fileCardTokenManagementService;

    @MockBean
    CommonQueryRepository commonQueryRepository;

    @Test
    void updateTrustedTokens() {
        fileCardTokenManagementService.deleteOldFiles();

        //check empty select
        when(commonQueryRepository.selectFreshTrustedCardTokens(any())).thenReturn(new ArrayList<>());
        cardPoolManagementService.updateTrustedTokens();
        assertTrue(cardTokenPool.isEmpty());

        //check full select
        final String testToken = "test_token";
        when(commonQueryRepository.selectFreshTrustedCardTokens(any())).thenReturn(
                new ArrayList<>(List.of(testToken, "test_token_2")));
        cardPoolManagementService.updateTrustedTokens();
        assertTrue(cardTokenPool.isExist(testToken));

        //check read from old file
        cardTokenPool.clear();
        when(commonQueryRepository.selectFreshTrustedCardTokens(any()))
                .thenReturn(new ArrayList<>(List.of("fake_token")));
        cardPoolManagementService.updateTrustedTokens();
        assertTrue(cardTokenPool.isExist(testToken));

        //check not select
        cardPoolManagementService.updateTrustedTokens();
        assertTrue(cardTokenPool.isExist(testToken));

        fileCardTokenManagementService.deleteOldFiles();
    }
}
