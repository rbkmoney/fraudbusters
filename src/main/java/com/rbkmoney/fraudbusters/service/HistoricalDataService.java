package com.rbkmoney.fraudbusters.service;

import com.rbkmoney.fraudbusters.service.dto.FilterDto;
import com.rbkmoney.fraudbusters.service.dto.HistoricalPaymentsDto;

public interface HistoricalDataService {

    HistoricalPaymentsDto getPayments(FilterDto filter);
}
