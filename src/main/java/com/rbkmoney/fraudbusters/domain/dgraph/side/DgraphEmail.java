package com.rbkmoney.fraudbusters.domain.dgraph.side;

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
@NoArgsConstructor
@ToString(callSuper = true)
public class DgraphEmail extends DgraphSideObject {

    public DgraphEmail(String userEmail, String lastActTime) {
        super(lastActTime);
        this.userEmail = userEmail;
    }

    @JsonProperty("dgraph.type")
    private final String type = "Email";

    private String userEmail;
    private List<DgraphFingerprint> fingerprints;
    private List<DgraphToken> tokens;

}
