package com.rbkmoney.fraudbusters.fraud.model;

import com.rbkmoney.fraudo.model.BaseModel;
import lombok.Data;

@Data
public class P2PModel extends BaseModel {

    private Payer sender;
    private Payer receiver;
    private String identityId;

}
