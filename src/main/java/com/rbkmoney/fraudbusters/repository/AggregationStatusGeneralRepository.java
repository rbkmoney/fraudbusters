package com.rbkmoney.fraudbusters.repository;

import com.rbkmoney.fraudbusters.fraud.model.FieldModel;

import java.util.List;

public interface AggregationStatusGeneralRepository {

    Integer countOperationByField(String table, String fieldName, Object value, Long from, Long to, String status);

    Integer countOperationByFieldWithGroupBy(String table, String fieldName, Object value, Long from, Long to,
                                             List<FieldModel> fieldModels, String status);

    Long sumOperationByFieldWithGroupBy(String table, String fieldName, Object value, Long from, Long to,
                                        List<FieldModel> fieldModels, String status);

    Integer uniqCountOperation(String table, String fieldNameBy, Object value, String fieldNameCount, Long from, Long to, String status);

    Integer uniqCountOperationWithGroupBy(String table, String fieldNameBy, Object value, String fieldNameCount,
                                          Long from, Long to, List<FieldModel> fieldModels, String status);

}
