package com.rbkmoney.fraudbusters.repository.impl;

import com.google.common.base.Splitter;
import com.rbkmoney.fraudbusters.constant.EventSource;
import com.rbkmoney.fraudbusters.constant.PaymentField;
import com.rbkmoney.fraudbusters.domain.CheckedPayment;
import com.rbkmoney.fraudbusters.repository.HistoricalDataRepository;
import com.rbkmoney.fraudbusters.repository.mapper.CheckedPaymentMapper;
import com.rbkmoney.fraudbusters.service.dto.FilterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

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
        String select = "" +
                "SELECT " +
                "     eventTime, " +
                "    partyId, " +
                "    shopId, " +
                "    email, " +
                "    amount / 100 as amount, " +
                "    currency, " +
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
                " FROM " +
                EventSource.FRAUD_EVENTS_PAYMENT.getTable() +
                " WHERE " +
                "    timestamp >= toDate(:from) " +
                "    and timestamp <= toDate(:to) " +
                "    and toDateTime(eventTime) >= toDateTime(:from) " +
                "    and toDateTime(eventTime) <= toDateTime(:to) ";
        StringBuilder filters = new StringBuilder();
        Map<PaymentField, String> filterFields = filter.getSearchPatterns();
        if (!CollectionUtils.isEmpty(filterFields)) {
            filterFields.forEach((key, value) ->
                    filters.append(" and like(").append(key.getValue()).append(",'").append(value).append("')"));
        }
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (Objects.nonNull(filter.getLastId())) {
            List<String> compositeId = Splitter.on("-")
                    .splitToList(filter.getLastId());
            if (compositeId.size() == 2) {
                params.addValue("id", compositeId.get(0))
                        .addValue("status", compositeId.get(1));
            }
            filters.append(" and (id < :id or (status != :status and id = :id)) ");
        }
        String pagination = "ORDER BY id DESC LIMIT :size";
        String query = select + filters.toString() + pagination;

        params.addValue("from", filter.getTimeFrom())
                .addValue("to", filter.getTimeTo())
                .addValue("size", filter.getSize())
        ;
        return namedParameterJdbcTemplate.query(query, params, checkedPaymentMapper);
    }
}
