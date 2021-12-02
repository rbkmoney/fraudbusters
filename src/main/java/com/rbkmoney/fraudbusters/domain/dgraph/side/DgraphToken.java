package com.rbkmoney.fraudbusters.domain.dgraph.side;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rbkmoney.fraudbusters.domain.dgraph.DgraphSideObject;
import com.rbkmoney.fraudbusters.domain.dgraph.common.DgraphChargeback;
import com.rbkmoney.fraudbusters.domain.dgraph.common.DgraphPayment;
import com.rbkmoney.fraudbusters.domain.dgraph.common.DgraphRefund;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@ToString(callSuper = true)
public class DgraphToken extends DgraphSideObject {

    public DgraphToken(String tokenId, String maskedPan, String lastActTime) {
        super(lastActTime);
        this.tokenId = tokenId;
        this.maskedPan = maskedPan;
    }

    @JsonProperty("dgraph.type")
    private final String type = "Token";

    private String tokenId;
    private String maskedPan;
    private String tokenizationMethod;
    private String paymentSystem;
    private String issuerCountry;
    private String bankName;
    private String cardholderName;
    private String category;

    private List<DgraphEmail> emails;
    private List<DgraphFingerprint> fingerprints;
    private DgraphBin bin;

}