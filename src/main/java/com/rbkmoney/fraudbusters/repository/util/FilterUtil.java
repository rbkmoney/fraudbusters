package com.rbkmoney.fraudbusters.repository.util;


import com.rbkmoney.fraudbusters.constant.PaymentField;
import com.rbkmoney.fraudbusters.constant.QueryParamName;
import com.rbkmoney.fraudbusters.constant.SortOrder;
import com.rbkmoney.fraudbusters.service.dto.FilterDto;
import com.rbkmoney.fraudbusters.util.CompositeIdUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilterUtil {

    public static final String PAGE_CONTENT_FILTER = " and (id %s :id or (status != :status and id = :id)) ";

    public static String appendFilters(FilterDto filter) {
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
        return filters.toString() +
                sorting +
                limit;
    }

    public static MapSqlParameterSource initParams(FilterDto filter) {
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
