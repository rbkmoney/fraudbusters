package com.rbkmoney.fraudbusters.service.dto;

import com.rbkmoney.fraudbusters.constant.PaymentField;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class FilterDto {

    private String lastId;
    private Integer size;
    private Map<PaymentField, String> searchPatterns;

}
