package com.rbkmoney.fraudbusters.repository;

import com.rbkmoney.fraudbusters.fraud.model.FieldModel;

import java.util.List;

public interface PaymentRepository extends AggregationRepository {

    Integer countOperationSuccessWithGroupBy(String fieldName, String value, Long from, Long to, List<FieldModel> fieldModels);

    Integer countOperationErrorWithGroupBy(String fieldName, String value, Long from, Long to, List<FieldModel> fieldModels, String errorCode);

    Long sumOperationSuccessWithGroupBy(String fieldName, String value, Long from, Long to, List<FieldModel> fieldModels);

    Long sumOperationErrorWithGroupBy(String fieldName, String value, Long from, Long to, List<FieldModel> fieldModels, String errorCode);

}
