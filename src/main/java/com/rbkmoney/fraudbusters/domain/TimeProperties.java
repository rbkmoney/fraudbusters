package com.rbkmoney.fraudbusters.domain;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TimeProperties {

    private LocalDate timestamp;
    private Long eventTime;
    private Long eventTimeHour;

}
