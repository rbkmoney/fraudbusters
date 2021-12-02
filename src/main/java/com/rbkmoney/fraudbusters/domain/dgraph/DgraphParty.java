package com.rbkmoney.fraudbusters.domain.dgraph;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class DgraphParty {

    public DgraphParty(String partyId, String lastActTime) {
        this.partyId = partyId;
        this.lastActTime = lastActTime;
    }

    @JsonProperty("dgraph.type")
    private final String type = "Party";

    private String uid;
    private String partyId;
    private List<DgraphShop> shops;
    private String lastActTime;
    private List<DgraphPayment> payments;
    private List<DgraphRefund> refunds;
    private List<DgraphChargeback> chargebacks;
    private List<DgraphEmail> emails;
    private List<DgraphFingerprint> fingerprints;
    private List<DgraphToken> tokens;

}
