package com.rbkmoney.fraudbusters.repository;

import com.rbkmoney.fraudbusters.domain.CheckedPayment;
import com.rbkmoney.fraudbusters.service.dto.FilterDto;

import java.util.List;

public interface HistoricalDataRepository {

    List<CheckedPayment> getPayments(FilterDto filter);

}
