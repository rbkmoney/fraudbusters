package com.rbkmoney.fraudbusters.schedule;

import com.rbkmoney.fraudbusters.service.CardPoolManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "load.isTrusted.enabled")
public class CardTokenPoolManagedJob {

    private final CardPoolManagementService cardPoolManagementService;

    @Scheduled(fixedRateString = "${card-token-pool.scheduleRateMs}")
    public void updateTokensTask() {
        log.info("updateTokensTask started");
        cardPoolManagementService.updateTrustedTokens();
        log.info("updateTokensTask finished");
    }

}
