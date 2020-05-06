package com.rbkmoney.fraudbusters.repository.impl;

import com.google.common.collect.Lists;
import com.rbkmoney.fraudbusters.constant.ClickhouseSchemeNames;
import com.rbkmoney.fraudbusters.domain.Event;
import com.rbkmoney.fraudbusters.repository.CrudRepository;
import com.rbkmoney.fraudbusters.repository.setter.EventBatchPreparedStatementSetter;
import com.rbkmoney.fraudbusters.repository.setter.EventParametersGenerator;
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
public class FraudResultRepository implements CrudRepository<Event> {

    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT = "INSERT INTO fraud.events_unique " +
            " (timestamp, eventTimeHour, eventTime, ip, email, bin, fingerprint, shopId, partyId, resultStatus, amount, " +
            "country, checkedRule, bankCountry, currency, invoiceId, maskedPan, bankName, cardToken, paymentId, checkedTemplate)" +
            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    @Override
    public void insert(Event value) {
        log.debug("EventRepository insertBatch value: {}", value);
        if (value != null) {
            Map<String, Object> parameters = EventParametersGenerator.generateParamsByFraudModel(value);
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                    .withSchemaName(ClickhouseSchemeNames.FRAUD)
                    .withTableName(ClickhouseSchemeNames.EVENTS_UNIQUE);
            simpleJdbcInsert.setColumnNames(Lists.newArrayList(parameters.keySet()));
            simpleJdbcInsert
                    .execute(parameters);
        }
    }

    @Override
    public void insertBatch(List<Event> events) {
        log.debug("EventRepository insertBatch events: {}", events);
        if (events != null && !events.isEmpty()) {
            jdbcTemplate.batchUpdate(INSERT, new EventBatchPreparedStatementSetter(events));
        }
    }

}
