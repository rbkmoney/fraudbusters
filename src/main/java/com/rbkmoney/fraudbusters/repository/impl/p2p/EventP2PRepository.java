package com.rbkmoney.fraudbusters.repository.impl.p2p;

import com.google.common.collect.Lists;
import com.rbkmoney.fraudbusters.constant.ClickhouseSchemeNames;
import com.rbkmoney.fraudbusters.constant.EventSource;
import com.rbkmoney.fraudbusters.domain.EventP2P;
import com.rbkmoney.fraudbusters.fraud.model.FieldModel;
import com.rbkmoney.fraudbusters.repository.AggregationGeneralRepository;
import com.rbkmoney.fraudbusters.repository.AggregationRepository;
import com.rbkmoney.fraudbusters.repository.Repository;
import com.rbkmoney.fraudbusters.repository.setter.EventP2PBatchPreparedStatementSetter;
import com.rbkmoney.fraudbusters.repository.setter.EventP2PParametersGenerator;
import lombok.Getter;
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
public class EventP2PRepository implements Repository<EventP2P>, AggregationRepository {

    private final JdbcTemplate jdbcTemplate;
    private final AggregationGeneralRepository aggregationGeneralRepository;

    @Getter
    private final EventSource eventSource = EventSource.FRAUD_EVENTS_UNIQUE;

    public static final String FRAUD_EVENTS_P_TO_P = "fraud.events_p_to_p";

    private static final String INSERT = "INSERT INTO fraud.events_p_to_p " +
            "(timestamp, eventTime, eventTimeHour, identityId, transferId, ip, email, bin, fingerprint, amount, " +
            "currency, country, bankCountry, maskedPan, bankName, cardTokenFrom, cardTokenTo, resultStatus, checkedRule, " +
            "checkedTemplate) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    @Override
    public void insert(EventP2P value) {
        log.debug("EventP2PRepository insert value: {}", value);
        if (value != null) {
            Map<String, Object> parameters = EventP2PParametersGenerator.generateParamsByFraudModel(value);
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                    .withSchemaName(ClickhouseSchemeNames.FRAUD)
                    .withTableName(ClickhouseSchemeNames.EVENTS_P_TO_P);
            simpleJdbcInsert.setColumnNames(Lists.newArrayList(parameters.keySet()));
            simpleJdbcInsert
                    .execute(parameters);
        }
    }

    @Override
    public void insertBatch(List<EventP2P> events) {
        log.debug("EventP2PRepository insertBatch events: {}", events);
        if (events != null && !events.isEmpty()) {
            jdbcTemplate.batchUpdate(INSERT, new EventP2PBatchPreparedStatementSetter(events));
        }
    }

    public Integer countOperationByField(String fieldName, String value, Long from, Long to) {
        return aggregationGeneralRepository.countOperationByField(FRAUD_EVENTS_P_TO_P, fieldName, value, from, to);
    }

    public Integer countOperationByFieldWithGroupBy(String fieldName, String value, Long from, Long to, List<FieldModel> fieldModels) {
        return aggregationGeneralRepository.countOperationByFieldWithGroupBy(FRAUD_EVENTS_P_TO_P, fieldName, value, from, to, fieldModels);
    }

    public Long sumOperationByFieldWithGroupBy(String fieldName, String value, Long from, Long to, List<FieldModel> fieldModels) {
        return aggregationGeneralRepository.sumOperationByFieldWithGroupBy(FRAUD_EVENTS_P_TO_P, fieldName, value, from, to, fieldModels);
    }

    public Integer uniqCountOperation(String fieldNameBy, String value, String fieldNameCount, Long from, Long to) {
        return aggregationGeneralRepository.uniqCountOperation(FRAUD_EVENTS_P_TO_P, fieldNameBy, value, fieldNameCount, from, to);
    }

    public Integer uniqCountOperationWithGroupBy(String fieldNameBy, String value, String fieldNameCount, Long from, Long to, List<FieldModel> fieldModels) {
        return aggregationGeneralRepository.uniqCountOperationWithGroupBy(FRAUD_EVENTS_P_TO_P, fieldNameBy, value, fieldNameCount, from, to, fieldModels);
    }

    @Override
    public Integer countOperationSuccessWithGroupBy(String fieldName, String value, Long from, Long to, List<FieldModel> fieldModels) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer countOperationErrorWithGroupBy(String fieldName, String value, Long from, Long to,
                                                  List<FieldModel> fieldModels, String errorCode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long sumOperationSuccessWithGroupBy(String fieldName, String value, Long from, Long to, List<FieldModel> fieldModels) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long sumOperationErrorWithGroupBy(String fieldName, String value, Long from, Long to,
                                             List<FieldModel> fieldModels, String errorCode) {
        throw new UnsupportedOperationException();
    }

}
