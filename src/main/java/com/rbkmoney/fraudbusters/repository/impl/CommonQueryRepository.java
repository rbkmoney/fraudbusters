package com.rbkmoney.fraudbusters.repository.impl;

import com.rbkmoney.fraudbusters.exception.ReadDataException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonQueryRepository {

    private static final String QUERY = "SELECT cardToken" +
                                        " FROM fraud.payment " +
                                        " WHERE toDateTime(?) - INTERVAL 1 YEAR < toDateTime(eventTime) and " +
                                        "toDateTime(?) > toDateTime(eventTime) and status='captured' " +
                                        " and providerId in ('108', '114', '118', '119', '121', '125', '126', '128', " +
                                        "'130', '136', '132', '137', '143', '139', '144', '149')" +
                                        " GROUP BY cardToken, currency " +
                                        " HAVING (uniq(id) > 2 and ((sum(amount) > 200000 and currency = 'RUB') " +
                                        " or (sum(amount) > 3000 and (currency = 'EUR' or currency = 'USD')))) " +
                                        " or (uniq(id) > 0 and (sum(amount) > 500000 and currency = 'KZT')) ";
    private static final String QUERY_WITHDRAWAL = "SELECT distinct cardToken" +
                                                   " FROM fraud.withdrawal " +
                                                   " WHERE toDateTime(?) - INTERVAL 1 YEAR < toDateTime(eventTime) " +
                                                   " and toDateTime(?) > toDateTime(eventTime)  " +
                                                   " and status='succeeded'";
    private final JdbcTemplate longQueryJdbcTemplate;

    public List<String> selectFreshTrustedCardTokens(Instant timeHour) {
        try {
            log.info("selectFreshTrustedCardTokens query: {} params: {}", QUERY, timeHour);
            List<String> data = longQueryJdbcTemplate.query(
                    QUERY,
                    List.of(timeHour.getEpochSecond(), timeHour.getEpochSecond()).toArray(),
                    (rs, rowNum) -> rs.getString(1)
            );
            log.info("selectFreshTrustedCardTokens result size: {}", data.size());

            log.info("select withdrawal card tokens query: {} params: {}", QUERY_WITHDRAWAL, timeHour);
            List<String> cardTokensWithdrawal = longQueryJdbcTemplate.query(
                    QUERY_WITHDRAWAL,
                    List.of(timeHour.getEpochSecond(), timeHour.getEpochSecond()).toArray(),
                    (rs, rowNum) -> rs.getString(1)
            );
            log.info("select withdrawal card tokens result size: {}", cardTokensWithdrawal.size());
            data.addAll(cardTokensWithdrawal);
            return data.stream()
                    .distinct()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("error when selectFreshTrustedCardTokens e: ", e);
            throw new ReadDataException("error when selectFreshTrustedCardTokens");
        }
    }
}
