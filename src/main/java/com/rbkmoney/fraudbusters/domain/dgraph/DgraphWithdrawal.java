package com.rbkmoney.fraudbusters.domain.dgraph;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DgraphWithdrawal {

    @JsonProperty("dgraph.type")
    private final String type = "Withdrawal";

    private String withdrawalId;
    private String createdAt;
    private long amount;
    private String currency;
    private String status;
    private String providerId;
    private String terminalId;
    private String accountId;
    private String accountIdentity;
    private String accountCurrency;
    private String errorCode;
    private String errorReason;

    private String destinationResource;
    private String digitalWalletId;
    private String digitalWalletDataProvider;
    private String cryptoWalletId;
    private String cryptoWalletCurrency;

    private DgraphCountry country;
    private DgraphBin bin;
    private DgraphToken cardToken;

}
