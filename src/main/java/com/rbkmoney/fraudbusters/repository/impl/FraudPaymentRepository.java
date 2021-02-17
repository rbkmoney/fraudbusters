package com.rbkmoney.fraudbusters.repository.impl;

import com.rbkmoney.fraudbusters.constant.EventSource;
import com.rbkmoney.fraudbusters.constant.RefundStatus;
import com.rbkmoney.fraudbusters.domain.FraudPaymentRow;
import com.rbkmoney.fraudbusters.fraud.model.FieldModel;
import com.rbkmoney.fraudbusters.repository.AggregationRepository;
import com.rbkmoney.fraudbusters.repository.Repository;
import com.rbkmoney.fraudbusters.repository.setter.FraudPaymentBatchPreparedStatementSetter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FraudPaymentRepository implements Repository<FraudPaymentRow>, AggregationRepository {

    private final AggregationStatusGeneralRepositoryImpl aggregationStatusGeneralRepository;
    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT = "INSERT INTO " + EventSource.FRAUD_EVENTS_FRAUD_PAYMENT.getTable() +
            " (timestamp," +
            "  eventTimeHour," +
            "  eventTime," +
            "  id," +
            "  fraudType ," +
            "  comment," +
            "  email," +
            "  ip," +
            "  fingerprint," +
            "  bin," +
            "  maskedPan," +
            "  cardToken," +
            "  paymentSystem," +
            "  paymentTool," +
            "  terminal," +
            "  providerId," +
            "  bankCountry," +
            "  partyId," +
            "  shopId," +
            "  amount," +
            "  currency," +
            "  status," +
            "  errorReason," +
            "  errorCode," +
            "  paymentCountry)" +
            " VALUES (?, ?, ?, ?, ?,?, ?, ?, ?, ?,?, ?, ?, ?, ?,?, ?, ?, ?, ?,?, ?, ?, ?, ?)";

    @Override
    public void insert(FraudPaymentRow value) {
        throw new UnsupportedOperationException("Method insertBatch is not support!");
    }

    @Override
    public void insertBatch(List<FraudPaymentRow> payments) {
        log.debug("FraudPaymentRepository insertBatch payments: {}", payments);
        if (!CollectionUtils.isEmpty(payments)) {
            jdbcTemplate.batchUpdate(INSERT, new FraudPaymentBatchPreparedStatementSetter(payments));
        }
    }

    @Override
    public Integer countOperationByField(String fieldName, Object value, Long from, Long to) {
        return aggregationStatusGeneralRepository.countOperationByField(EventSource.FRAUD_EVENTS_FRAUD_PAYMENT.getTable(),
                fieldName, value, from, to, RefundStatus.succeeded.name());
    }

    @Override
    public Integer countOperationByFieldWithGroupBy(String fieldName, Object value, Long from, Long to, List<FieldModel> fieldModels) {
        return aggregationStatusGeneralRepository.countOperationByFieldWithGroupBy(EventSource.FRAUD_EVENTS_FRAUD_PAYMENT.getTable(),
                fieldName, value, from, to, fieldModels, RefundStatus.succeeded.name());
    }

    @Override
    public Long sumOperationByFieldWithGroupBy(String fieldName, Object value, Long from, Long to, List<FieldModel> fieldModels) {
        return aggregationStatusGeneralRepository.sumOperationByFieldWithGroupBy(EventSource.FRAUD_EVENTS_FRAUD_PAYMENT.getTable(),
                fieldName, value, from, to, fieldModels, RefundStatus.succeeded.name());
    }

    @Override
    public Integer uniqCountOperation(String fieldNameBy, Object value, String fieldNameCount, Long from, Long to) {
        return aggregationStatusGeneralRepository.uniqCountOperation(EventSource.FRAUD_EVENTS_FRAUD_PAYMENT.getTable(),
                fieldNameBy, value, fieldNameCount, from, to, RefundStatus.succeeded.name());
    }

    @Override
    public Integer uniqCountOperationWithGroupBy(String fieldNameBy, Object value, String fieldNameCount, Long from,
                                                 Long to, List<FieldModel> fieldModels) {
        return aggregationStatusGeneralRepository.uniqCountOperationWithGroupBy(EventSource.FRAUD_EVENTS_FRAUD_PAYMENT.getTable(),
                fieldNameBy, value, fieldNameCount, from, to, fieldModels, RefundStatus.succeeded.name());
    }

}
