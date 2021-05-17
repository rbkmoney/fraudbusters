package com.rbkmoney.fraudbusters.repository.impl;

import com.rbkmoney.damsel.fraudbusters.Chargeback;
import com.rbkmoney.fraudbusters.constant.ChargebackStatus;
import com.rbkmoney.fraudbusters.constant.EventSource;
import com.rbkmoney.fraudbusters.fraud.model.FieldModel;
import com.rbkmoney.fraudbusters.repository.AggregationRepository;
import com.rbkmoney.fraudbusters.repository.Repository;
import com.rbkmoney.fraudbusters.repository.setter.ChargebackBatchPreparedStatementSetter;
import com.rbkmoney.fraudbusters.util.PaymentTypeByContextResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChargebackRepository implements Repository<Chargeback>, AggregationRepository {

    private static final String INSERT = String.format(
            "INSERT INTO %1s (%2s) VALUES (%3s)",
            EventSource.FRAUD_EVENTS_CHARGEBACK.getTable(),
            ChargebackBatchPreparedStatementSetter.FIELDS,
            ChargebackBatchPreparedStatementSetter.FIELDS_MARK
    );

    private final AggregationStatusGeneralRepositoryImpl aggregationStatusGeneralRepository;
    private final JdbcTemplate jdbcTemplate;
    private final PaymentTypeByContextResolver paymentTypeByContextResolver;


    @Override
    public void insert(Chargeback chargeback) {
        throw new UnsupportedOperationException("Method insertBatch is not support!");
    }

    @Override
    public void insertBatch(List<Chargeback> batch) {
        if (batch != null && !batch.isEmpty()) {
            log.debug("ChargebackRepository insertBatch batch: {}", batch);
            jdbcTemplate.batchUpdate(
                    INSERT,
                    new ChargebackBatchPreparedStatementSetter(batch, paymentTypeByContextResolver));
        }
    }

    @Override
    public Integer countOperationByField(String fieldName, Object value, Long from, Long to) {
        return aggregationStatusGeneralRepository.countOperationByField(
                EventSource.FRAUD_EVENTS_CHARGEBACK.getTable(),
                fieldName,
                value,
                from,
                to,
                ChargebackStatus.accepted.name()
        );
    }

    @Override
    public Integer countOperationByFieldWithGroupBy(
            String fieldName,
            Object value,
            Long from,
            Long to,
            List<FieldModel> fieldModels) {
        return aggregationStatusGeneralRepository.countOperationByFieldWithGroupBy(
                EventSource.FRAUD_EVENTS_CHARGEBACK.getTable(),
                fieldName,
                value,
                from,
                to,
                fieldModels,
                ChargebackStatus.accepted.name()
        );
    }

    @Override
    public Long sumOperationByFieldWithGroupBy(
            String fieldName,
            Object value,
            Long from,
            Long to,
            List<FieldModel> fieldModels) {
        return aggregationStatusGeneralRepository.sumOperationByFieldWithGroupBy(
                EventSource.FRAUD_EVENTS_CHARGEBACK.getTable(),
                fieldName,
                value,
                from,
                to,
                fieldModels,
                ChargebackStatus.accepted.name()
        );
    }

    @Override
    public Integer uniqCountOperation(String fieldNameBy, Object value, String fieldNameCount, Long from, Long to) {
        return aggregationStatusGeneralRepository.uniqCountOperation(
                EventSource.FRAUD_EVENTS_CHARGEBACK.getTable(),
                fieldNameBy,
                value,
                fieldNameCount,
                from,
                to,
                ChargebackStatus.accepted.name()
        );
    }

    @Override
    public Integer uniqCountOperationWithGroupBy(
            String fieldNameBy,
            Object value,
            String fieldNameCount,
            Long from,
            Long to,
            List<FieldModel> fieldModels) {
        return aggregationStatusGeneralRepository.uniqCountOperationWithGroupBy(
                EventSource.FRAUD_EVENTS_CHARGEBACK.getTable(),
                fieldNameBy,
                value,
                fieldNameCount,
                from,
                to,
                fieldModels,
                ChargebackStatus.accepted.name()
        );
    }
}
