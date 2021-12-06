package com.rbkmoney.fraudbusters.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.fraudbusters.exception.DgraphException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DgraphResponseConverterImpl implements DgraphResponseConverter {

    private final ObjectMapper dgraphMapper;

    @Override
    public <T> T convert(String json, Class<T> clazz) {
        try {
            return dgraphMapper.readValue(json, clazz);
        } catch (JsonProcessingException ex) {
            throw new DgraphException(String.format("Cannot covert json '%s' to class '%s", json, clazz), ex);
        }
    }

}
