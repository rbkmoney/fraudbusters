package com.rbkmoney.fraudbusters.fraud.model;

import lombok.Data;

@Data
public class Payer {

    private String bin;
    private String pan;
    private String cardToken;
    private String binCountryCode;
    private String bankName;

}
