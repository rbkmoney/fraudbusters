package com.rbkmoney.fraudbusters.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FraudResult {

    private FraudRequest fraudRequest;
    private CheckedResultModel resultModel;

}
