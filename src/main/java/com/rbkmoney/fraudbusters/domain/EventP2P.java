package com.rbkmoney.fraudbusters.domain;

import lombok.Data;

import java.sql.Date;

@Data
public class EventP2P {

    private Date timestamp;
    private Long eventTime;
    private Long eventTimeHour;

    private String identityId;
    private String transferId;

    private String ip;
    private String email;
    private String bin;
    private String fingerprint;

    private Long amount;
    private String currency;

    private String country;
    private String bankCountry;
    private String maskedPan;
    private String bankName;
    private String cardTokenFrom;
    private String cardTokenTo;

    private String resultStatus;
    private String checkedRule;
    private String checkedTemplate;

}
