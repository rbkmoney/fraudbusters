package com.rbkmoney.fraudbusters.service.dto;

import com.rbkmoney.fraudbusters.constant.SortOrder;
import lombok.Data;

@Data
public class SortDto {

    private SortOrder order;
    private String field;

}
