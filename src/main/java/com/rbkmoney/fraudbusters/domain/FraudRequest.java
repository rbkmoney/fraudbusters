package com.rbkmoney.fraudbusters.domain;

import com.rbkmoney.fraudo.model.FraudModel;
import lombok.Data;

@Data
public class FraudRequest {

    private FraudModel fraudModel;
    private Metadata metadata;

}
