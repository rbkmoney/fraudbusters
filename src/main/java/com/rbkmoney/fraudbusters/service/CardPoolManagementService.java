package com.rbkmoney.fraudbusters.service;

import com.rbkmoney.fraudbusters.exception.ReadDataException;
import com.rbkmoney.fraudbusters.fraud.pool.CardTokenPool;
import com.rbkmoney.fraudbusters.repository.impl.CommonQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardPoolManagementService {

    private final CardTokenPool cardTokenPool;
    private final CommonQueryRepository commonQueryRepository;
    private final FileCardTokenManagementService fileCardTokenManagementService;

    @Retryable(value = ReadDataException.class, maxAttemptsExpression = "${card-token-pool.retryDelayMs}",
            backoff = @Backoff(delayExpression = "${card-token-pool.maxAttempts}"))
    public void updateTrustedTokens() {
        log.debug("updateTrustedTokens started");
        Instant instant = Instant.now().truncatedTo(ChronoUnit.HOURS);
        String currentScheduleTime = instant.toString();
        if (!fileCardTokenManagementService.isFileExist(currentScheduleTime)) {
            final List<String> cardTokens = commonQueryRepository.selectFreshTrustedCardTokens(instant);
            if (!CollectionUtils.isEmpty(cardTokens)) {
                fileCardTokenManagementService.deleteOldFiles();
                fileCardTokenManagementService.writeToFile(currentScheduleTime, cardTokens);
                cardTokenPool.reinit(cardTokens);
                log.debug("init new file: {} rows: {}", currentScheduleTime, cardTokens.size());
            }
        } else if (isFirstInitPool(currentScheduleTime)) {
            final List<String> cardTokens = fileCardTokenManagementService.readCardTokensFromFile(currentScheduleTime);
            if (!CollectionUtils.isEmpty(cardTokens)) {
                cardTokenPool.reinit(cardTokens);
                log.debug("init data from exist file: {} rows: {}", currentScheduleTime, cardTokens.size());
            }
        }
        log.debug("updateTrustedTokens finish success");
    }

    private boolean isFirstInitPool(String currentScheduleTime) {
        return cardTokenPool.isEmpty() && fileCardTokenManagementService.isFileExist(currentScheduleTime);
    }
}
