package com.rbkmoney.fraudbusters.client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentReferenceModel {
    private String partyId;
    private String shopId;
    private Boolean isGlobal;
}
