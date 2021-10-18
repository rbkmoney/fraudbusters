package com.rbkmoney.fraudbusters.domain.dgraph;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DgraphRefund {

    @JsonProperty("dgraph.type")
    private final String type = "Refund";

    private String refundId;
    private String paymentId;
    private String partyId;
    private String shopId;
    private String createdAt;
    private long amount;
    private String currency;
    private String status;
    private String payerType;
    private String errorCode;
    private String errorReason;
    private DgraphPartyShop partyShop;
    private DgraphPayment payment;
    private DgraphToken cardToken;
    private DgraphFingerprint fingerprint;
    private DgraphIp refundIp;
    private DgraphEmail email;
    private DgraphBin bin;
}
