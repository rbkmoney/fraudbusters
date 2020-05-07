package com.rbkmoney.fraudbusters.repository;

import com.rbkmoney.fraudbusters.fraud.model.FieldModel;

import java.util.List;

public interface AggregationGeneralRepository {

    Integer countOperationByField(String table, String fieldName, String value, Long from, Long to);

    Integer countOperationByFieldWithGroupBy(String table, String fieldName, String value, Long from, Long to,
                                             List<FieldModel> fieldModels);

    Long sumOperationByFieldWithGroupBy(String table, String fieldName, String value, Long from, Long to,
                                        List<FieldModel> fieldModels);

    Integer uniqCountOperation(String table, String fieldNameBy, String value, String fieldNameCount, Long from, Long to);

    Integer uniqCountOperationWithGroupBy(String table, String fieldNameBy, String value, String fieldNameCount,
                                          Long from, Long to, List<FieldModel> fieldModels);

}
