package com.rbkmoney.fraudbusters.domain;

import lombok.Data;

import java.sql.Date;

@Data
public class Event {

    private Date timestamp;
    private String ip;
    private String email;
    private String bin;
    private String fingerprint;
    private String shopId;
    private String partyId;
    private String resultStatus;
    private String country;
    private Long eventTime;
    private Long amount;
    private String checkedRule;
    private String bankCountry;
    private String currency;
    private String invoiceId;
    private String maskedPan;
    private String bankName;

}
