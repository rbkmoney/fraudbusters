package com.rbkmoney.fraudbusters.repository;

public interface DgraphAggregatesRepository {

    Integer getCount(String query);

    Double getSum(String query);

}
