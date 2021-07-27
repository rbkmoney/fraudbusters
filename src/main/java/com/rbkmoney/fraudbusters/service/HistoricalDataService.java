package com.rbkmoney.fraudbusters.service;

import com.rbkmoney.damsel.fraudbusters.HistoricalTransactionCheck;
import com.rbkmoney.damsel.fraudbusters.Payment;
import com.rbkmoney.damsel.fraudbusters.Template;
import com.rbkmoney.fraudbusters.service.dto.FilterDto;
import com.rbkmoney.fraudbusters.service.dto.HistoricalPaymentsDto;

import java.util.Set;

public interface HistoricalDataService {

    HistoricalPaymentsDto getPayments(FilterDto filter);

    Set<HistoricalTransactionCheck> applySingleRule(Template template, Set<Payment> transactions);
}
