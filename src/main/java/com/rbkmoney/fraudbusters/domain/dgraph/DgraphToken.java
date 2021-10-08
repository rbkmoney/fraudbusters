package com.rbkmoney.fraudbusters.domain.dgraph;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class DgraphToken {

    @JsonProperty("dgraph.type")
    private final String type = "Token";

    private String uid;
    private String tokenId;
    private String maskedPan;
    private String lastActTime;
    private List<DgraphPayment> payments;
    private List<DgraphEmail> emails;
    private List<DgraphFingerprint> fingerprints;

}