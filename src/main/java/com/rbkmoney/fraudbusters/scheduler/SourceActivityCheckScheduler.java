package com.rbkmoney.fraudbusters.scheduler;


import com.rbkmoney.fraudbusters.repository.source.SourcePool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SourceActivityCheckScheduler {

    private final SourcePool sourcePool;

    @Scheduled(fixedDelay = 60000)
    public void scheduleCheckActivity() {
        sourcePool.checkAllSource();
        log.info("SourceActivityCheckScheduler scheduleCheckActivity activeSource: {}", sourcePool.getActiveSource());
    }

}
