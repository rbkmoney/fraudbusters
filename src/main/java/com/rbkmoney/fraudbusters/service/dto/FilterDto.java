package com.rbkmoney.fraudbusters.service.dto;

import com.rbkmoney.fraudbusters.constant.PaymentField;
import lombok.Data;

import java.util.Map;

@Data
public class FilterDto {

    private String lastId;
    private Integer size;
    private String timeFrom;
    private String timeTo;
    private Map<PaymentField, String> searchPatterns;

}
