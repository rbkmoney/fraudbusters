package com.rbkmoney.fraudbusters.domain.dgraph;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class DgraphBin {

    public DgraphBin(String cardBin) {
        this.cardBin = cardBin;
    }

    @JsonProperty("dgraph.type")
    private final String type = "Bin";

    private String uid;
    private String cardBin;
    private List<DgraphPayment> payments;
    private List<DgraphRefund> refunds;
    private List<DgraphChargeback> chargebacks;
    private List<DgraphEmail> emails;
    private List<DgraphFingerprint> fingerprints; //TODO: добавить заполнение fingerprint'ов
    private List<DgraphToken> tokens;

}
