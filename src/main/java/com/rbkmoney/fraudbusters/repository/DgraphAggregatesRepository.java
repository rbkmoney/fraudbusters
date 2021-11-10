package com.rbkmoney.fraudbusters.repository;

public interface DgraphAggregatesRepository {

    Integer getCount(String query);

    Long getSum(String query);

}
