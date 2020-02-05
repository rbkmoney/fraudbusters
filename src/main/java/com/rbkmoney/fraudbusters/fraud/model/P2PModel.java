package com.rbkmoney.fraudbusters.fraud.model;

import com.rbkmoney.fraudo.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class P2PModel extends BaseModel {

    private Payer sender;
    private Payer receiver;
    private String identityId;
    private String transferId;
    private Long timestamp;

}
