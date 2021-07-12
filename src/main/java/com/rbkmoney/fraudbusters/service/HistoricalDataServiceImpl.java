package com.rbkmoney.fraudbusters.service;

import com.rbkmoney.fraudbusters.service.dto.FilterDto;
import com.rbkmoney.fraudbusters.service.dto.HistoricalPaymentsDto;
import org.springframework.stereotype.Service;

@Service
public class HistoricalDataServiceImpl implements HistoricalDataService {

    @Override
    public HistoricalPaymentsDto getPayments(FilterDto filter) {

        return null;
    }
}
