package com.rbkmoney.fraudbusters.domain.dgraph.side;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rbkmoney.fraudbusters.domain.dgraph.DgraphExtendedSideObject;
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
public class DgraphParty extends DgraphExtendedSideObject {

    public DgraphParty(String partyId, String lastActTime) {
        super(lastActTime);
        this.partyId = partyId;
    }

    @JsonProperty("dgraph.type")
    private final String type = "Party";

    private String partyId;
    private List<DgraphShop> shops;

}
