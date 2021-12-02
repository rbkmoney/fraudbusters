package com.rbkmoney.fraudbusters.domain.dgraph;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class DgraphFingerprint {

    public DgraphFingerprint(String fingerprintData, String lastActTime) {
        this.fingerprintData = fingerprintData;
        this.lastActTime = lastActTime;
    }

    @JsonProperty("dgraph.type")
    private final String type = "Fingerprint";

    private String uid;
    private String fingerprintData;
    private String lastActTime;
    private List<DgraphPayment> payments;
    private List<DgraphRefund> refunds;
    private List<DgraphChargeback> chargebacks;
    private List<DgraphEmail> emails;
    private List<DgraphToken> tokens;

}
