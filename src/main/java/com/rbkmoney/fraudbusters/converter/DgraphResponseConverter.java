package com.rbkmoney.fraudbusters.converter;

public interface DgraphResponseConverter {

    <T> T convert(String json, Class<T> clazz);

}
