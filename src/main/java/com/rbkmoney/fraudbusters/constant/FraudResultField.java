package com.rbkmoney.fraudbusters.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum FraudResultField {

    RESULT_STATUS("resultStatus"),
    CHECKED_RULE("checkedRule"),
    CHECKED_TEMPLATE("checkedTemplate");

    @Getter
    private final String value;

}
