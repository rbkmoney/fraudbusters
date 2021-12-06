package com.rbkmoney.fraudbusters.domain.dgraph.side;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rbkmoney.fraudbusters.domain.dgraph.DgraphSideObject;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@ToString(callSuper = true)
public class DgraphBin extends DgraphSideObject { //TODO: добавить заполнение fingerprint'ов

    public DgraphBin(String cardBin) {
        this.cardBin = cardBin;
    }

    @JsonProperty("dgraph.type")
    private final String type = "Bin";

    private String cardBin;

}
