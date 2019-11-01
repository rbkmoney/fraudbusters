package com.rbkmoney.fraudbusters.fraud.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Payer {

    private String bin;
    private String pan;
    private String country;
    private String cardToken;

}
