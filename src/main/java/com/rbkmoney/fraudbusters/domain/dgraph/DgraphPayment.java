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

    public DgraphPayment(String paymentId) {
        this.paymentId = paymentId;
    }

    @JsonProperty("dgraph.type")
    private final String type = "Payment";

    private String uid;
    private String paymentId;
    private String createdAt;
    private Long amount;
    private DgraphCurrency currency;
    private String status;

    private String paymentTool;
    private String terminal;
    private String providerId;
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

    private DgraphParty party;
    private DgraphShop shop;
    private DgraphCountry country;
    private DgraphIp operationIp;
    private DgraphBin bin;
    private DgraphToken cardToken;
    private DgraphFingerprint fingerprint;
    private DgraphEmail contactEmail;
    private DgraphFraudPayment fraudPayment;

}
