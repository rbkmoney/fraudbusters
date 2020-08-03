package com.rbkmoney.fraudbusters.repository.impl.analytics;

import com.google.common.collect.Lists;
import com.rbkmoney.fraudbusters.constant.EventSource;
import com.rbkmoney.fraudbusters.constant.RefundStatus;
import com.rbkmoney.fraudbusters.domain.Refund;
import com.rbkmoney.fraudbusters.fraud.model.FieldModel;
import com.rbkmoney.fraudbusters.repository.AggregationRepository;
import com.rbkmoney.fraudbusters.repository.Repository;
import com.rbkmoney.fraudbusters.repository.impl.AggregationStatusGeneralRepositoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsRefundRepository implements Repository<Refund>, AggregationRepository {

    private final AggregationStatusGeneralRepositoryImpl aggregationStatusGeneralRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void insert(Refund refund) {
        log.debug("RefundRepository insert payment: {}", refund);
        if (refund != null) {
            Map<String, Object> parameters = RefundParametersGenerator.generateParamsByFraudModel(refund);
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                    .withTableName(EventSource.ANALYTIC_EVENTS_SINK_REFUND.getTable());
            simpleJdbcInsert.setColumnNames(Lists.newArrayList(parameters.keySet()));
            simpleJdbcInsert.execute(parameters);
        }
    }

    @Override
    public void insertBatch(List<Refund> batch) {
        throw new UnsupportedOperationException("Method insertBatch is not support!");
    }

    @Override
    public Integer countOperationByField(String fieldName, String value, Long from, Long to) {
        return aggregationStatusGeneralRepository.countOperationByField(EventSource.ANALYTIC_EVENTS_SINK_REFUND.getTable(),
                fieldName, value, from, to, RefundStatus.succeeded.name());
    }

    @Override
    public Integer countOperationByFieldWithGroupBy(String fieldName, String value, Long from, Long to, List<FieldModel> fieldModels) {
        return aggregationStatusGeneralRepository.countOperationByFieldWithGroupBy(EventSource.ANALYTIC_EVENTS_SINK_REFUND.getTable(),
                fieldName, value, from, to, fieldModels, RefundStatus.succeeded.name());
    }

    @Override
    public Long sumOperationByFieldWithGroupBy(String fieldName, String value, Long from, Long to, List<FieldModel> fieldModels) {
        return aggregationStatusGeneralRepository.sumOperationByFieldWithGroupBy(EventSource.ANALYTIC_EVENTS_SINK_REFUND.getTable(),
                fieldName, value, from, to, fieldModels, RefundStatus.succeeded.name());
    }

    @Override
    public Integer uniqCountOperation(String fieldNameBy, String value, String fieldNameCount, Long from, Long to) {
        return aggregationStatusGeneralRepository.uniqCountOperation(EventSource.ANALYTIC_EVENTS_SINK_REFUND.getTable(),
                fieldNameBy, value, fieldNameCount, from, to, RefundStatus.succeeded.name());
    }

    @Override
    public Integer uniqCountOperationWithGroupBy(String fieldNameBy, String value, String fieldNameCount, Long from,
                                                 Long to, List<FieldModel> fieldModels) {
        return aggregationStatusGeneralRepository.uniqCountOperationWithGroupBy(EventSource.ANALYTIC_EVENTS_SINK_REFUND.getTable(),
                fieldNameBy, value, fieldNameCount, from, to, fieldModels, RefundStatus.succeeded.name());
    }

}
