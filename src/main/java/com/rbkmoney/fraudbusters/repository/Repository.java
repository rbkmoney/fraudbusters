package com.rbkmoney.fraudbusters.repository;

import java.util.List;

public interface Repository<T> {

    void insert(T t);

    void insertBatch(List<T> batch);

}
