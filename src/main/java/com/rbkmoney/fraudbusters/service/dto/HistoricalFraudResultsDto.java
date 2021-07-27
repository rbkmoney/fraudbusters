package com.rbkmoney.fraudbusters.service.dto;

import com.rbkmoney.fraudbusters.domain.Event;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HistoricalFraudResultsDto {

    private List<Event> fraudResults;
    private String lastId;

}
