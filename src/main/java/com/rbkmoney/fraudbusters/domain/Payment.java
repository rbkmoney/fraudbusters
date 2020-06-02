package com.rbkmoney.fraudbusters.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Payment extends BaseRaw {

    private String bin;
    private String maskedPan;
    private String paymentTool;

}
