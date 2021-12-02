package com.rbkmoney.fraudbusters.domain.dgraph;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class DgraphShop {

    public DgraphShop(String shopId, String lastActTime) {
        this.shopId = shopId;
        this.lastActTime = lastActTime;
    }

    @JsonProperty("dgraph.type")
    private final String type = "Shop";

    private String uid;
    private DgraphParty party;
    private String shopId;
    private String lastActTime;
    private List<DgraphPayment> payments;
    private List<DgraphRefund> refunds;
    private List<DgraphChargeback> chargebacks;
    private List<DgraphToken> tokens;
    private List<DgraphEmail> emails;
    private List<DgraphFingerprint> fingerprints;

}
