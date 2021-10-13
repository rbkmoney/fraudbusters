package com.rbkmoney.fraudbusters.domain.dgraph;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class DgraphPayment {

    @JsonProperty("dgraph.type")
    private final String type = "Payment";

    private String uid;
    private String paymentId;
    private DgraphPartyShop partyShop;
    private String partyId;
    private String shopId;
    private String createdAt;
    private Long amount;
    private String currency;
    private String status;

    private String paymentTool;
    private String terminal;
    private String providerId;
    private String bankCountry;
    private String payerType;
    private String tokenProvider;
    private boolean mobile;
    private boolean recurrent;
    private String errorReason;
    private String errorCode;
    private String checkedTemplate;
    private String checkedRule;
    private String resultStatus;
    private String checkedResultsJson;

    private DgraphCountry country;
    private DgraphIp dgraphIp;
    private DgraphBin dgraphBin;
    private DgraphToken cardToken;
    private DgraphFingerprint fingerprint;
    private DgraphEmail contactEmail;

}
