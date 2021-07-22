package com.rbkmoney.fraudbusters.service.dto;

import com.rbkmoney.fraudbusters.domain.CheckedPayment;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HistoricalPaymentsDto {

    private List<CheckedPayment> payments;
    private String lastId;

}
