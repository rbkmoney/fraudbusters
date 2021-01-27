package com.rbkmoney.fraudbusters.repository.impl;

import com.rbkmoney.fraudbusters.exception.ReadDataException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonQueryRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String QUERY = "SELECT cardToken" +
            " FROM fraud.payment " +
            " WHERE toDateTime(?) - INTERVAL 1 YEAR < toDateTime(eventTime) and toDateTime(?) > toDateTime(eventTime) and status='captured' " +
            " GROUP BY cardToken, currency " +
            " HAVING uniq(id) > 2 and ((sum(amount) > 200000 and currency = 'RUB') or(sum(amount) > 3000 and currency != 'RUB'))";


    public List<String> selectFreshTrustedCardTokens(Instant timeHour) {
        try {
            log.info("selectFreshTrustedCardTokens query: {} params: {}", QUERY, timeHour);
            List<String> data = jdbcTemplate.query(QUERY, List.of(timeHour.getEpochSecond(), timeHour.getEpochSecond()).toArray(), (rs, rowNum) -> rs.getString(1));
            log.info("selectFreshTrustedCardTokens result size: {}", data.size());
            return data;
        } catch (Exception e) {
            log.error("error when selectFreshTrustedCardTokens e: ", e);
            throw new ReadDataException("error when selectFreshTrustedCardTokens");
        }
    }
}
