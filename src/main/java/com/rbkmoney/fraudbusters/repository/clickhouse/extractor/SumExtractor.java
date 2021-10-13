package com.rbkmoney.fraudbusters.repository.clickhouse.extractor;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SumExtractor implements ResultSetExtractor<Long> {
    @Override
    public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
        return rs.next() ? rs.getLong("sum") : 0L;
    }
}
