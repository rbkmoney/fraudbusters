package com.rbkmoney.fraudbusters.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.rbkmoney.fraudo.model.FraudModel;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FraudRequest {

    private FraudModel fraudModel;
    private Metadata metadata;

}
