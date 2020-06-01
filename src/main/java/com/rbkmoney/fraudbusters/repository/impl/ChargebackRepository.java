package com.rbkmoney.fraudbusters.repository.impl;

import com.google.common.collect.Lists;
import com.rbkmoney.fraudbusters.constant.EventSource;
import com.rbkmoney.fraudbusters.domain.Chargeback;
import com.rbkmoney.fraudbusters.fraud.model.FieldModel;
import com.rbkmoney.fraudbusters.repository.AggregationGeneralRepository;
import com.rbkmoney.fraudbusters.repository.AggregationRepository;
import com.rbkmoney.fraudbusters.repository.Repository;
import com.rbkmoney.fraudbusters.repository.setter.BaseRawParametersGenerator;
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
public class ChargebackRepository implements Repository<Chargeback>, AggregationRepository {

    private final AggregationGeneralRepository aggregationGeneralRepository;
    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT = String.format(
            "INSERT INTO %1S (%2S) VALUES (%3S)",
            EventSource.ANALYTIC_EVENTS_SINK_CHARGEBACK.getTable(),
            BaseRawParametersGenerator.BASE_RAW_PARAMETERS,
            BaseRawParametersGenerator.BASE_RAW_PARAMETERS_MARK);

    @Override
    public void insert(Chargeback chargeback) {
        log.debug("ChargebackRepository insert chargeback: {}", chargeback);
        if (chargeback != null) {
            Map<String, Object> parameters = BaseRawParametersGenerator.generateParamsByFraudModel(chargeback);
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                    .withTableName(EventSource.ANALYTIC_EVENTS_SINK_CHARGEBACK.getTable());
            simpleJdbcInsert.setColumnNames(Lists.newArrayList(parameters.keySet()));
            simpleJdbcInsert.execute(parameters);
        }
    }

    @Override
    public void insertBatch(List<Chargeback> batch) {
        throw new UnsupportedOperationException("ChargebackRepository is not support now!");
    }

    @Override
    public Integer countOperationByField(String fieldName, String value, Long from, Long to) {
        return aggregationGeneralRepository.countOperationByField(EventSource.ANALYTIC_EVENTS_SINK_CHARGEBACK.getTable(),
                fieldName, value, from, to);
    }

    @Override
    public Integer countOperationByFieldWithGroupBy(String fieldName, String value, Long from, Long to, List<FieldModel> fieldModels) {
        return aggregationGeneralRepository.countOperationByFieldWithGroupBy(EventSource.ANALYTIC_EVENTS_SINK_CHARGEBACK.getTable(),
                fieldName, value, from, to, fieldModels);
    }

    @Override
    public Long sumOperationByFieldWithGroupBy(String fieldName, String value, Long from, Long to, List<FieldModel> fieldModels) {
        return aggregationGeneralRepository.sumOperationByFieldWithGroupBy(EventSource.ANALYTIC_EVENTS_SINK_CHARGEBACK.getTable(),
                fieldName, value, from, to, fieldModels);
    }

    @Override
    public Integer uniqCountOperation(String fieldNameBy, String value, String fieldNameCount, Long from, Long to) {
        return aggregationGeneralRepository.uniqCountOperation(EventSource.ANALYTIC_EVENTS_SINK_CHARGEBACK.getTable(),
                fieldNameBy, value, fieldNameCount, from, to);
    }

    @Override
    public Integer uniqCountOperationWithGroupBy(String fieldNameBy, String value, String fieldNameCount, Long from, Long to, List<FieldModel> fieldModels) {
        return aggregationGeneralRepository.uniqCountOperationWithGroupBy(EventSource.ANALYTIC_EVENTS_SINK_CHARGEBACK.getTable(),
                fieldNameBy, value, fieldNameCount, from, to, fieldModels);
    }

    @Override
    public Integer countOperationSuccessWithGroupBy(String fieldName, String value, Long from, Long to,
                                                    List<FieldModel> fieldModels) {
        throw new UnsupportedOperationException("ChargebackRepository is not support now!");
    }

    @Override
    public Integer countOperationErrorWithGroupBy(String fieldName, String value, Long from, Long to,
                                                  List<FieldModel> fieldModels, String errorCode) {
        throw new UnsupportedOperationException("ChargebackRepository is not support now!");
    }

    @Override
    public Long sumOperationSuccessWithGroupBy(String fieldName, String value, Long from, Long to,
                                               List<FieldModel> fieldModels) {
        throw new UnsupportedOperationException("ChargebackRepository is not support now!");
    }

    @Override
    public Long sumOperationErrorWithGroupBy(String fieldName, String value, Long from, Long to,
                                             List<FieldModel> fieldModels, String errorCode) {
        throw new UnsupportedOperationException("ChargebackRepository is not support now!");
    }
}
