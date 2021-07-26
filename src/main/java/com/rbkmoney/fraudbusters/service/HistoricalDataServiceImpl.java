package com.rbkmoney.fraudbusters.service;

import com.rbkmoney.damsel.fraudbusters.Refund;
import com.rbkmoney.fraudbusters.domain.CheckedPayment;
import com.rbkmoney.fraudbusters.repository.Repository;
import com.rbkmoney.fraudbusters.service.dto.FilterDto;
import com.rbkmoney.fraudbusters.service.dto.HistoricalPaymentsDto;
import com.rbkmoney.fraudbusters.service.dto.HistoricalRefundsDto;
import com.rbkmoney.fraudbusters.util.CompositeIdUtil;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoricalDataServiceImpl implements HistoricalDataService {

    private final Repository<CheckedPayment> paymentRepository;
    private final Repository<Refund> refundRepository;

    @Override
    public HistoricalPaymentsDto getPayments(FilterDto filter) {
        List<CheckedPayment> payments = paymentRepository.getByFilter(filter);
        String lastId = buildLastPaymentId(filter.getSize(), payments);
        return HistoricalPaymentsDto.builder()
                .payments(payments)
                .lastId(lastId)
                .build();
    }

    @Override
    public HistoricalRefundsDto getRefunds(FilterDto filter) {
        List<Refund> refunds = refundRepository.getByFilter(filter);
        String lastId = buildLastRefundId(filter.getSize(), refunds);
        return HistoricalRefundsDto.builder()
                .refunds(refunds)
                .lastId(lastId)
                .build();
    }

    @Nullable
    private String buildLastPaymentId(Long filterSize, List<CheckedPayment> payments) {
        if (payments.size() == filterSize) {
            CheckedPayment lastPayment = payments.get(payments.size() - 1);
            return CompositeIdUtil.create(lastPayment.getId(), lastPayment.getPaymentStatus());
        }
        return null;
    }

    @Nullable
    private String buildLastRefundId(Long filterSize, List<Refund> refunds) { // TODO перейти на внутреннюю модель
        if (refunds.size() == filterSize) {
            Refund lastRefund = refunds.get(refunds.size() - 1);
            return CompositeIdUtil.create(lastRefund.getId(), lastRefund.getStatus().name());
        }
        return null;
    }
}
