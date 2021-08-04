package com.rbkmoney.fraudbusters.service.dto;

import com.rbkmoney.damsel.fraudbusters.Chargeback;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HistoricalChargebacksDto {

    private List<Chargeback> chargebacks;
    private String lastId;

}
