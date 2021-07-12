package com.rbkmoney.fraudbusters.service.dto;

import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import lombok.Data;

import java.util.Map;

@Data
public class FilterDto {

    private String lastId;
    private Integer size;
    private Map<PaymentCheckedField, String> searchPatterns;

}
