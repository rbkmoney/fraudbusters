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

    @JsonProperty("dgraph.type")
    private final String type = "Party";

    private String uid;
    private String partyId;
    private String lastActTime;
    private List<DgraphEmail> emails;
    private List<DgraphToken> tokens;
    private List<DgraphPayment> payments;
    private List<DgraphShop> shops;

}
