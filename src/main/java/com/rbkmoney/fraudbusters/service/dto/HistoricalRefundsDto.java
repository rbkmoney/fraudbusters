package com.rbkmoney.fraudbusters.service.dto;

import com.rbkmoney.damsel.fraudbusters.Refund;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HistoricalRefundsDto {

    private List<Refund> refunds;
    private String lastId;

}
