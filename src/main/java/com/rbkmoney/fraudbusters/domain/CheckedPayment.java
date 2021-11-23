package com.rbkmoney.fraudbusters.domain;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CheckedPayment {

    private LocalDate timestamp;
    private Long eventTime;
    private Long eventTimeHour;

    private String id;
    private String email;
    private String phone;
    private String ip;
    private String fingerprint;

    private String bin;
    private String maskedPan;
    private String cardToken;
    private String cardCategory;
    private String paymentSystem;
    private String paymentTool;
    private String terminal;
    private String providerId;
    private String bankCountry;
    private String payerType;
    private String tokenProvider;
    private boolean mobile;
    private boolean recurrent;

    private String partyId;
    private String shopId;

    private Long amount;
    private String currency;

    private String errorReason;
    private String errorCode;
    private String paymentCountry;

    private String paymentStatus;

    private String checkedTemplate;
    private String checkedRule;
    private String resultStatus;
    private String checkedResultsJson;

}


