package com.rbkmoney.fraudbusters.converter;

import org.springframework.core.convert.converter.Converter;

import java.util.List;
import java.util.stream.Collectors;

public interface BatchConverter<T, U> extends Converter<T, U> {

    default List<U> convertBatch(List<T> fraudResults) {
        return fraudResults.stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

}
