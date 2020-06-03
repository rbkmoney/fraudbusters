package com.rbkmoney.fraudbusters.domain;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BaseRaw {

    private LocalDate timestamp;
    private Long eventTimeHour;
    private Long eventTime;

    private String shopId;
    private String partyId;

    private String email;
    private String providerId;

    private Long amount;
    private String currency;

    private String status;
    private String errorReason;
    private String errorCode;

    private String invoiceId;
    private String paymentId;

    private String ip;
    private String fingerprint;
    private String cardToken;
    private String paymentSystem;

    private String paymentCountry;
    private String bankCountry;

    private String terminal;

}
