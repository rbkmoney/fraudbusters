package com.rbkmoney.fraudbusters.fraud.model;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class DgraphAggregationQueryModel {

    private String rootType;
    private String rootFilter;
    private String targetType;
    private String targetFaset;
    private String targetFilter;
    private Set<String> innerTypesFilters;
    private boolean isRootModel;

}
