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

    public DgraphIp(String ip, String lastActTime) {
        this.ip = ip;
        this.lastActTime = lastActTime;
    }

    @JsonProperty("dgraph.type")
    private final String type = "Ip";

    private String uid;
    private String ip;
    private String lastActTime;
    private List<DgraphEmail> emails;
    private List<DgraphToken> tokens;
    private List<DgraphPayment> payments;
    private List<DgraphCountry> countries;

}
