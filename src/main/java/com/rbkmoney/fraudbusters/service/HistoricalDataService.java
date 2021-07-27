package com.rbkmoney.fraudbusters.service;

import com.rbkmoney.fraudbusters.service.dto.*;

public interface HistoricalDataService {

    HistoricalPaymentsDto getPayments(FilterDto filter);

    HistoricalRefundsDto getRefunds(FilterDto filter);

    HistoricalChargebacksDto getChargebacks(FilterDto filter);

    HistoricalFraudResultsDto getFraudResults(FilterDto filter);

}
