package com.rbkmoney.fraudbusters.repository.impl;

import com.rbkmoney.fraudbusters.constant.PaymentField;
import com.rbkmoney.fraudbusters.constant.QueryParamName;
import com.rbkmoney.fraudbusters.constant.SortOrder;
import com.rbkmoney.fraudbusters.domain.CheckedPayment;
import com.rbkmoney.fraudbusters.repository.HistoricalDataRepository;
import com.rbkmoney.fraudbusters.repository.mapper.CheckedPaymentMapper;
import com.rbkmoney.fraudbusters.repository.query.PaymentQuery;
import com.rbkmoney.fraudbusters.service.dto.FilterDto;
import com.rbkmoney.fraudbusters.util.CompositeIdUtil;
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

    public static final String PAGE_CONTENT_FILTER = " and (id %s :id or (status != :status and id = :id)) ";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final CheckedPaymentMapper checkedPaymentMapper;

    @Override
    public List<CheckedPayment> getPayments(FilterDto filter) {
        StringBuilder filters = new StringBuilder();
        Map<PaymentField, String> filterFields = filter.getSearchPatterns();
        if (!CollectionUtils.isEmpty(filterFields)) {
            filterFields.forEach((key, value) ->
                    filters.append(" and like(").append(key.getValue()).append(",'").append(value).append("')"));
        }
        if (Objects.nonNull(filter.getLastId())) {
            if (SortOrder.DESC.equals(filter.getSort().getOrder())) {
                filters.append(String.format(PAGE_CONTENT_FILTER, "<"));
            } else {
                filters.append(String.format(PAGE_CONTENT_FILTER, ">"));
            }
        }
        String sorting = String.format("ORDER BY (eventTime, id) %s ", filter.getSort().getOrder().name());
        String limit = " LIMIT :size ";
        String query = PaymentQuery.SELECT_HISTORY_PAYMENT +
                filters.toString() +
                sorting +
                limit;
        MapSqlParameterSource params = buildParams(filter);
        return namedParameterJdbcTemplate.query(query, params, checkedPaymentMapper);
    }

    private MapSqlParameterSource buildParams(FilterDto filter) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (Objects.nonNull(filter.getLastId())) {
            List<String> compositeId = CompositeIdUtil.extract(filter.getLastId());
            if (compositeId.size() == 2) {
                params.addValue(QueryParamName.ID, compositeId.get(0))
                        .addValue(QueryParamName.STATUS, compositeId.get(1));
            }
        }
        params.addValue(QueryParamName.FROM, filter.getTimeFrom())
                .addValue(QueryParamName.TO, filter.getTimeTo())
                .addValue(QueryParamName.SIZE, filter.getSize());
        return params;
    }
}
