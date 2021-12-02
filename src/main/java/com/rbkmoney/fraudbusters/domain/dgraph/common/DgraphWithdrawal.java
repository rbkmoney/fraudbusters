package com.rbkmoney.fraudbusters.domain.dgraph.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rbkmoney.fraudbusters.domain.dgraph.side.DgraphBin;
import com.rbkmoney.fraudbusters.domain.dgraph.side.DgraphCountry;
import com.rbkmoney.fraudbusters.domain.dgraph.side.DgraphCurrency;
import com.rbkmoney.fraudbusters.domain.dgraph.side.DgraphToken;
import lombok.Data;

@Data
public class DgraphWithdrawal {

    @JsonProperty("dgraph.type")
    private final String type = "Withdrawal";

    private String withdrawalId;
    private String createdAt;
    private long amount;
    private DgraphCurrency currency;
    private String status;
    private String providerId;
    private String terminalId;
    private String accountId;
    private String accountIdentity;
    private DgraphCurrency accountCurrency;
    private String errorCode;
    private String errorReason;

    private String destinationResource;
    private String digitalWalletId;
    private String digitalWalletDataProvider;
    private String cryptoWalletId;
    private DgraphCurrency cryptoWalletCurrency;

    private DgraphCountry country;
    private DgraphBin bin;
    private DgraphToken cardToken;

}
