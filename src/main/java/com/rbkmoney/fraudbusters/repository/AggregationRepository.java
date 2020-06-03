package com.rbkmoney.fraudbusters.repository;

import com.rbkmoney.fraudbusters.constant.EventSource;
import com.rbkmoney.fraudbusters.fraud.model.FieldModel;

import java.util.List;

public interface AggregationRepository {

    Integer countOperationByField(String fieldName, String value, Long from, Long to);

    Integer countOperationByFieldWithGroupBy(String fieldName, String value, Long from, Long to, List<FieldModel> fieldModels);

    Long sumOperationByFieldWithGroupBy(String fieldName, String value, Long from, Long to, List<FieldModel> fieldModels);

    Integer uniqCountOperation(String fieldNameBy, String value, String fieldNameCount, Long from, Long to);

    Integer uniqCountOperationWithGroupBy(String fieldNameBy, String value, String fieldNameCount, Long from, Long to, List<FieldModel> fieldModels);

}
