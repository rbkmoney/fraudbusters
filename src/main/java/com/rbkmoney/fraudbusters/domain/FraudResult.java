package com.rbkmoney.fraudbusters.domain;

import com.rbkmoney.fraudo.constant.ResultStatus;
import com.rbkmoney.fraudo.model.FraudModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@AllArgsConstructor
public class FraudResult {

    private FraudModel fraudModel;
    private ResultStatus resultStatus;

}
