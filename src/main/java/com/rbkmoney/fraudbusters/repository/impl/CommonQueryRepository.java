package com.rbkmoney.fraudbusters.repository.impl;

import com.rbkmoney.fraudbusters.exception.ReadDataException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonQueryRepository {

    private static final String QUERY = """
            SELECT cardToken
             FROM fraud.payment
             WHERE toDateTime(?) - INTERVAL ? YEAR < toDateTime(eventTime)
             and toDateTime(?) > toDateTime(eventTime) and status='captured'
             and providerId in (%s)
             GROUP BY cardToken, currency
             HAVING (uniq(id) > 2 and ((sum(amount) > 200000 and currency = 'RUB')
             or (sum(amount) > 3000 and (currency = 'EUR' or currency = 'USD'))))
             or (uniq(id) > 0 and (sum(amount) > 500000 and currency = 'KZT'))
            """;

    private static final String QUERY_WITHDRAWAL = """
            SELECT distinct cardToken
            FROM fraud.withdrawal
            WHERE toDateTime(?) - INTERVAL ? YEAR < toDateTime(eventTime)
            and toDateTime(?) > toDateTime(eventTime)
            and status='succeeded'
            """;

    private final JdbcTemplate longQueryJdbcTemplate;

    @Value("${trusted.providers.interval-time-year}")
    public Double timeIntervalYear;

    @Value("#{'${trusted.providers.list}'}")
    public String[] trustedProvidersList;

    public List<String> selectFreshTrustedCardTokens(Instant timeHour) {
        try {
            String formatQuery = addProviderParameters();
            log.info("selectFreshTrustedCardTokens query: {} params: {}", formatQuery, timeHour);

            Object[] args = {timeHour.getEpochSecond(),
                    timeIntervalYear,
                    timeHour.getEpochSecond()
            };

            List<String> data = longQueryJdbcTemplate.query(
                    formatQuery,
                    ArrayUtils.addAll(args, trustedProvidersList),
                    (rs, rowNum) -> rs.getString(1)
            );
            log.info("selectFreshTrustedCardTokens result size: {}", data.size());

            log.info("select withdrawal card tokens query: {} params: {}", QUERY_WITHDRAWAL, timeHour);
            List<String> cardTokensWithdrawal = longQueryJdbcTemplate.query(
                    QUERY_WITHDRAWAL,
                    List.of(timeHour.getEpochSecond(), timeIntervalYear, timeHour.getEpochSecond()).toArray(),
                    (rs, rowNum) -> rs.getString(1)
            );
            log.info("select withdrawal card tokens result size: {}",
                    CollectionUtils.isEmpty(cardTokensWithdrawal) ? 0 : cardTokensWithdrawal.size());
            data.addAll(cardTokensWithdrawal);
            return data.stream()
                    .distinct()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("error when selectFreshTrustedCardTokens e: ", e);
            throw new ReadDataException("error when selectFreshTrustedCardTokens");
        }
    }

    private String addProviderParameters() {
        String inSql = String.join(",", Collections.nCopies(trustedProvidersList.length, "?"));
        return String.format(QUERY, inSql);
    }
}
