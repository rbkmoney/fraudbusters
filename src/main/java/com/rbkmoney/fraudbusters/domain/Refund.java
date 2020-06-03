package com.rbkmoney.fraudbusters.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Refund extends BaseRaw {

    private String reason;
    private String refundId;

}