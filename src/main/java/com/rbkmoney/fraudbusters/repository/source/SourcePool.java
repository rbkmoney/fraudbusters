package com.rbkmoney.fraudbusters.repository.source;

import com.rbkmoney.fraudbusters.constant.EventSource;
import com.rbkmoney.fraudbusters.repository.AggregationRepository;
import com.rbkmoney.fraudbusters.repository.extractor.CountExtractor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SourcePool {

    private final JdbcTemplate jdbcTemplate;

    @Getter
    private AggregationRepository activeSource;

    private final List<AggregationRepository> aggregationRepositoryList;

    @PostConstruct
    public void initPool() {
        checkAllSource();
    }

    public void checkAllSource() {
        boolean findActive = false;
        for (AggregationRepository value : aggregationRepositoryList) {
            boolean isActive = isActiveSource(value.getEventSource());
            if (!findActive && isActive) {
                this.activeSource = value;
                findActive = true;
            }
        }
        if (!findActive) {
            activeSource = aggregationRepositoryList.get(0);
            log.warn("SourcePool not found active source and set default by priority: {}", activeSource);
        }
        log.info("SourcePool checkAllSource sourcePool: {}", aggregationRepositoryList);
    }

    private boolean isActiveSource(EventSource table) {
        try {
            LocalDateTime now = LocalDateTime.now();
            Object[] params = {now.minusMinutes(10L).toInstant(ZoneOffset.UTC).getEpochSecond(), now.toInstant(ZoneOffset.UTC).getEpochSecond()};
            String query = String.format("select count() as cnt from %s where eventTime >= ? and eventTime <= ?", table.getTable());
            Integer cnt = jdbcTemplate.query(query, params, new CountExtractor());
            return cnt != null && cnt > 0;
        } catch (Exception e) {
            log.warn("SourcePool error when check activity for source: {} e: ", table, e);
            return false;
        }
    }
}
