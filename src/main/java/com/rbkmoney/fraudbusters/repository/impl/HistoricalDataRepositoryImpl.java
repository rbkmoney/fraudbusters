package com.rbkmoney.fraudbusters.repository.impl;

import com.rbkmoney.fraudbusters.constant.PaymentField;
import com.rbkmoney.fraudbusters.domain.CheckedPayment;
import com.rbkmoney.fraudbusters.repository.HistoricalDataRepository;
import com.rbkmoney.fraudbusters.repository.mapper.CheckedPaymentMapper;
import com.rbkmoney.fraudbusters.service.dto.FilterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class HistoricalDataRepositoryImpl implements HistoricalDataRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final CheckedPaymentMapper checkedPaymentMapper;

    @Override
    public List<CheckedPayment> getPayments(FilterDto filter) {
        String select = "SELECT " +
                "    toDateTime(eventTime) as eventTime, " +
                "    partyId, " +
                "    shopId, " +
                "    email, " +
                "    amount / 100 as amount, " +
                "    currency as cur, " +
                "    id, " +
                "    cardToken, " +
                "    bankCountry, " +
                "    fingerprint, " +
                "    ip, " +
                "    status, " +
                "    errorReason, " +
                "    errorCode, " +
                "    paymentSystem, " +
                "    paymentCountry, " +
                "    paymentTool, " +
                "    providerId, " +
                "    terminal " +
                "FROM payment " +
                "WHERE " +
                "    timestamp >= toDate(:from) " +
                "    and timestamp <= toDate(:to) " +
                "    and toDateTime(eventTime) >= :from " +
                "    and toDateTime(eventTime) <= :to ";
        StringBuilder filters = new StringBuilder();
        Map<PaymentField, String> filterFields = filter.getSearchPatterns();
        filterFields.forEach((key, value) ->
                filters.append(" and like(").append(key.getValue()).append(",'").append(value).append("')"));
        if (Objects.nonNull(filter.getLastId())) {
            filters.append(" and id <= :id ");
        }
        String pagination = "ORDER BY id DESC LIMIT :size";
        String query = select + filters.toString() + pagination;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("from", LocalDateTime.now()) // TODO поменять
                .addValue("to", LocalDateTime.now()) // TODO поменять
                .addValue("id", filter.getLastId())
                .addValue("size", filter.getSize())
        ;
        namedParameterJdbcTemplate.query(query, params, checkedPaymentMapper);
        return null;
    }

    public static void main(String[] args) {
        System.out.println(LocalDateTime.now());
    }
}
