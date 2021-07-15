package com.rbkmoney.fraudbusters.service;

import com.rbkmoney.fraudbusters.domain.CheckedPayment;
import com.rbkmoney.fraudbusters.repository.HistoricalDataRepository;
import com.rbkmoney.fraudbusters.service.dto.FilterDto;
import com.rbkmoney.fraudbusters.service.dto.HistoricalPaymentsDto;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoricalDataServiceImpl implements HistoricalDataService {

    private final HistoricalDataRepository historicalDataRepository;

    @Override
    public HistoricalPaymentsDto getPayments(FilterDto filter) {
        List<CheckedPayment> payments = historicalDataRepository.getPayments(filter);
        String lastId = getLastId(filter.getSize(), payments);
        return HistoricalPaymentsDto.builder()
                .payments(payments)
                .lastId(lastId)
                .build();
    }

    // TODO id надо брать составной
    @Nullable
    private String getLastId(Integer filterSize, List<CheckedPayment> payments) {
        if (payments.size() == filterSize) {
            return payments.get(payments.size() - 1).getId();
        }
        return null;
    }
}
