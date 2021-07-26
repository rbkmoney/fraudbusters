package com.rbkmoney.fraudbusters.service;

import com.rbkmoney.fraudbusters.service.dto.FilterDto;
import com.rbkmoney.fraudbusters.service.dto.HistoricalChargebacksDto;
import com.rbkmoney.fraudbusters.service.dto.HistoricalPaymentsDto;
import com.rbkmoney.fraudbusters.service.dto.HistoricalRefundsDto;

public interface HistoricalDataService {

    HistoricalPaymentsDto getPayments(FilterDto filter);

    HistoricalRefundsDto getRefunds(FilterDto filter);

    HistoricalChargebacksDto getChargebacks(FilterDto filter);

}
