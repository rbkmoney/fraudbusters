package com.rbkmoney.fraudbusters.repository.clickhouse.extractor;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CountExtractor implements ResultSetExtractor<Integer> {
    @Override
    public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
        return rs.next() ? rs.getInt("cnt") : 0;
    }
}
