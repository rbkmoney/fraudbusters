package com.rbkmoney.fraudbusters.domain.dgraph.side;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rbkmoney.fraudbusters.domain.dgraph.DgraphSideObject;
import com.rbkmoney.fraudbusters.domain.dgraph.common.DgraphWithdrawal;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@ToString(callSuper = true)
public class DgraphCurrency extends DgraphSideObject {

    public DgraphCurrency(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @JsonProperty("dgraph.type")
    private final String type = "Currency";

    private String currencyCode;
    private List<DgraphWithdrawal> withdrawals;

}
