package com.rbkmoney.fraudbusters.domain.dgraph;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DgraphRefund {

    @JsonProperty("dgraph.type")
    private final String type = "Refund";

    private String refundId;
    private String paymentId;
    private String createdAt;
    private long amount;
    private DgraphCurrency currency;
    private String status;
    private String payerType;
    private String errorCode;
    private String errorReason;

    private DgraphParty party;
    private DgraphShop shop;
    private DgraphPayment sourcePayment;
    private DgraphToken cardToken;
    private DgraphFingerprint fingerprint;
    private DgraphIp operationIp;
    private DgraphEmail contactEmail;
    private DgraphBin bin;
}
