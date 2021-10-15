package com.rbkmoney.fraudbusters.factory.properties;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PaymentProperties {

    private String tokenId;
    private String email;
    private String fingerprint;
    private String partyId;
    private String shopId;
    private String country;
    private String bin;
    private String ip;

}
