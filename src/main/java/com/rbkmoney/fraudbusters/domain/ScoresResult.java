package com.rbkmoney.fraudbusters.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScoresResult {

    private FraudRequest fraudRequest;
    private Map<String, CheckedResultModel> scores;

}
