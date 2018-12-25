package com.rbkmoney.fraudbusters.domain;

import com.rbkmoney.fraudo.constant.ResultStatus;
import com.rbkmoney.fraudo.model.FraudModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FraudResult {

    private FraudModel fraudModel;
    private ResultStatus resultStatus;

}
