package com.rbkmoney.fraudbusters.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScoresResult<T> {

    private T request;
    private Map<String, CheckedResultModel> scores;

}
