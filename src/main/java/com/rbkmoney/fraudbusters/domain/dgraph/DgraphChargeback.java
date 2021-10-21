package com.rbkmoney.fraudbusters.domain.dgraph;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DgraphChargeback {

    @JsonProperty("dgraph.type")
    private final String type = "Chargeback";

    private String chargebackId;
    private String paymentId;
    private String partyId;
    private String shopId;
    private String createdAt;
    private long amount;
    private String currency;
    private String status;
    private String category;
    private String code;
    private String payerType;
    private DgraphPartyShop partyShop;
    private DgraphPayment payment;
    private DgraphToken cardToken;
    private DgraphFingerprint fingerprint;
    private DgraphIp chargebackIp;
    private DgraphEmail email;
    private DgraphBin bin;

}
