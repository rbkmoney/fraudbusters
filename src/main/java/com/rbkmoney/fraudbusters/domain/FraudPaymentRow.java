package com.rbkmoney.fraudbusters.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FraudPaymentRow extends CheckedPayment {

    private String type;
    private String comment;

}
