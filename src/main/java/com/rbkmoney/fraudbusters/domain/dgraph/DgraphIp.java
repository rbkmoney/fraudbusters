package com.rbkmoney.fraudbusters.domain.dgraph;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class DgraphIp {

    public DgraphIp(String ipAddress, String lastActTime) {
        this.ipAddress = ipAddress;
        this.lastActTime = lastActTime;
    }

    @JsonProperty("dgraph.type")
    private final String type = "Ip";

    private String uid;
    private String ipAddress;
    private String lastActTime;
    private List<DgraphPayment> payments;
    private List<DgraphRefund> refunds;
    private List<DgraphChargeback> chargebacks;
    private List<DgraphToken> tokens;
    private List<DgraphEmail> emails;
    private List<DgraphFingerprint> fingerprints;
    private List<DgraphCountry> countries;

}
