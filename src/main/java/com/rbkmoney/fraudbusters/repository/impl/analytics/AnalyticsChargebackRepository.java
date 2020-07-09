package com.rbkmoney.fraudbusters.repository.impl.analytics;

import com.google.common.collect.Lists;
import com.rbkmoney.fraudbusters.constant.ChargebackStatus;
import com.rbkmoney.fraudbusters.constant.EventSource;
import com.rbkmoney.fraudbusters.domain.Chargeback;
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
public class AnalyticsChargebackRepository implements Repository<Chargeback>, AggregationRepository {

    private final AggregationStatusGeneralRepositoryImpl aggregationStatusGeneralRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void insert(Chargeback chargeback) {
        log.debug("ChargebackRepository insert chargeback: {}", chargeback);
        if (chargeback != null) {
            Map<String, Object> parameters = ChargebackParametersGenerator.generateParamsByFraudModel(chargeback);
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                    .withTableName(EventSource.ANALYTIC_EVENTS_SINK_CHARGEBACK.getTable());
            simpleJdbcInsert.setColumnNames(Lists.newArrayList(parameters.keySet()));
            simpleJdbcInsert.execute(parameters);
        }
    }

    @Override
    public void insertBatch(List<Chargeback> batch) {
        throw new UnsupportedOperationException("AnalyticsChargebackRepository method insertBatch is not support!");
    }

    @Override
    public Integer countOperationByField(String fieldName, String value, Long from, Long to) {
        return aggregationStatusGeneralRepository.countOperationByField(EventSource.ANALYTIC_EVENTS_SINK_CHARGEBACK.getTable(),
                fieldName, value, from, to, ChargebackStatus.accepted.name());
    }

    @Override
    public Integer countOperationByFieldWithGroupBy(String fieldName, String value, Long from, Long to, List<FieldModel> fieldModels) {
        return aggregationStatusGeneralRepository.countOperationByFieldWithGroupBy(EventSource.ANALYTIC_EVENTS_SINK_CHARGEBACK.getTable(),
                fieldName, value, from, to, fieldModels, ChargebackStatus.accepted.name());
    }

    @Override
    public Long sumOperationByFieldWithGroupBy(String fieldName, String value, Long from, Long to, List<FieldModel> fieldModels) {
        return aggregationStatusGeneralRepository.sumOperationByFieldWithGroupBy(EventSource.ANALYTIC_EVENTS_SINK_CHARGEBACK.getTable(),
                fieldName, value, from, to, fieldModels, ChargebackStatus.accepted.name());
    }

    @Override
    public Integer uniqCountOperation(String fieldNameBy, String value, String fieldNameCount, Long from, Long to) {
        return aggregationStatusGeneralRepository.uniqCountOperation(EventSource.ANALYTIC_EVENTS_SINK_CHARGEBACK.getTable(),
                fieldNameBy, value, fieldNameCount, from, to, ChargebackStatus.accepted.name());
    }

    @Override
    public Integer uniqCountOperationWithGroupBy(String fieldNameBy, String value, String fieldNameCount, Long from, Long to, List<FieldModel> fieldModels) {
        return aggregationStatusGeneralRepository.uniqCountOperationWithGroupBy(EventSource.ANALYTIC_EVENTS_SINK_CHARGEBACK.getTable(),
                fieldNameBy, value, fieldNameCount, from, to, fieldModels, ChargebackStatus.accepted.name());
    }

}
