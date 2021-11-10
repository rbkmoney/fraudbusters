package com.rbkmoney.fraudbusters.factory.properties;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OperationProperties {

    private String tokenId;
    private String paymentId;
    private String refundId;
    private String email;
    private String fingerprint;
    private String partyId;
    private String shopId;
    private String country;
    private String bin;
    private String ip;
    private boolean eventTimeDispersion;

}
