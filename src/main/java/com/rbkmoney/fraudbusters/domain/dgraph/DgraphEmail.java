package com.rbkmoney.fraudbusters.domain.dgraph;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class DgraphEmail {

    public DgraphEmail(String userEmail, String lastActTime) {
        this.userEmail = userEmail;
        this.lastActTime = lastActTime;
    }

    @JsonProperty("dgraph.type")
    private final String type = "Email";

    private String uid;
    private String emailId;
    private String userEmail;
    private String lastActTime;
    private List<DgraphFingerprint> fingerprints;
    private List<DgraphToken> tokens;
    private List<DgraphPayment> payments;

}
