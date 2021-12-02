package com.rbkmoney.fraudbusters.domain.dgraph;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class DgraphCountry {

    public DgraphCountry(String countryName) {
        this.countryName = countryName;
    }

    @JsonProperty("dgraph.type")
    private final String type = "Country";

    private String uid;
    private String countryName;
    private List<DgraphPayment> payments;
    private List<DgraphToken> tokens;
    private List<DgraphEmail> emails;
    private List<DgraphFingerprint> fingerprints; //TODO: fill it
    private List<DgraphIp> ips;

}
