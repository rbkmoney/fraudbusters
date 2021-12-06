package com.rbkmoney.fraudbusters.domain.dgraph.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rbkmoney.fraudbusters.domain.dgraph.side.*;
import lombok.Data;

@Data
public class DgraphChargeback {

    @JsonProperty("dgraph.type")
    private final String type = "Chargeback";

    private String chargebackId;
    private String paymentId;
    private String createdAt;
    private long amount;
    private DgraphCurrency currency;
    private String status;
    private String category;
    private String code;
    private String payerType;

    private DgraphParty party;
    private DgraphShop shop;
    private DgraphPayment payment;
    private DgraphToken cardToken;
    private DgraphFingerprint fingerprint;
    private DgraphIp operationIp;
    private DgraphEmail email;
    private DgraphBin bin;

}
