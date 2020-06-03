package com.rbkmoney.fraudbusters.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Chargeback extends BaseRaw {

    private String chargebackCode;
    private String stage;
    private String category;
    private String chargebackId;

}