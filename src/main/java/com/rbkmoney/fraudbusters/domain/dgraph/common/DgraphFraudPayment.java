package com.rbkmoney.fraudbusters.domain.dgraph.common;

import lombok.Data;

@Data
public class DgraphFraudPayment {

    private final String type = "FraudPayment";
    private String paymentId;
    private String createdAt;
    private String fraudType;
    private String comment;
    private DgraphPayment sourcePayment;

}
