package com.rbkmoney.fraudbusters.repository;

import java.util.List;

public interface CrudRepository<T> {

    void insert(T t);

    void insertBatch(List<T> batch);
}
