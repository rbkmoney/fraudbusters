package com.rbkmoney.fraudbusters.service;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentReferenceModel {
    private String partyId;
    private String shopId;
    private Boolean isGlobal;
}
