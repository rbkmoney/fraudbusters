package com.rbkmoney.fraudbusters.domain;

import lombok.Data;

import java.sql.Date;

@Data
public class MgEventSinkRow {

    private Date timestamp;
    private String ip;
    private String email;
    private String bin;
    private String fingerprint;
    private String shopId;
    private String partyId;
    private String resultStatus;
    private String errorCode;
    private String errorMessage;
    private String country;
    private Long amount;
    private String bankCountry;
    private String currency;
    private String invoiceId;
    private String maskedPan;
    private String bankName;
    private String cardToken;
    private String paymentId;

}
