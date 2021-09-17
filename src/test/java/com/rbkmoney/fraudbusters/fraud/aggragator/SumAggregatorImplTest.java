package com.rbkmoney.fraudbusters.fraud.aggragator;

import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.FieldModel;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.fraud.payment.aggregator.SumAggregatorImpl;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.DatabasePaymentFieldResolver;
import com.rbkmoney.fraudbusters.repository.AggregationRepository;
import com.rbkmoney.fraudbusters.repository.PaymentRepository;
import com.rbkmoney.fraudo.model.TimeWindow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SumAggregatorImplTest {

    private SumAggregatorImpl sumAggregator;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private AggregationRepository analyticsRefundRepository;
    @Mock
    private AggregationRepository analyticsChargebackRepository;
    @Mock
    private DatabasePaymentFieldResolver databasePaymentFieldResolver;

    private final FieldModel modelMock = new FieldModel("name", "value");

    @BeforeEach
    public void init() {
        when(databasePaymentFieldResolver.resolve(any(), any())).thenReturn(modelMock);

        sumAggregator = new SumAggregatorImpl(
                databasePaymentFieldResolver,
                paymentRepository,
                analyticsRefundRepository,
                analyticsChargebackRepository
        );
    }

    @Test
    public void sum() {
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setAmount(1L);

        when(paymentRepository.sumOperationByFieldWithGroupBy(any(), any(), any(), any(), any())).thenReturn(1050100L);

        Double some = sumAggregator.sum(
                PaymentCheckedField.BIN,
                paymentModel,
                TimeWindow.builder().startWindowTime(1444L).build(),
                null
        );

        assertEquals(Double.valueOf(1050101), some);
    }

    @Test
    public void sumTimeWindow() {
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setAmount(1L);
        TimeWindow.TimeWindowBuilder timeWindowBuilder = TimeWindow.builder().startWindowTime(1444L)
                .endWindowTime(400L);
        when(paymentRepository.sumOperationByFieldWithGroupBy(any(), any(), any(), any(), any())).thenReturn(1050100L);

        Double sum = sumAggregator.sum(PaymentCheckedField.BIN, paymentModel, timeWindowBuilder.build(), null);

        assertEquals(Double.valueOf(1050101), sum);

        timeWindowBuilder = TimeWindow.builder().startWindowTime(1444L)
                .endWindowTime(null);
        sum = sumAggregator.sum(PaymentCheckedField.BIN, paymentModel, timeWindowBuilder.build(), null);

        assertEquals(Double.valueOf(1050101), sum);
    }

    @Test
    public void sumSuccess() {
        when(paymentRepository.sumOperationSuccessWithGroupBy(any(), any(), any(), any(), any())).thenReturn(1050100L);
        Double some = sumAggregator.sumSuccess(PaymentCheckedField.BIN, new PaymentModel(),
                TimeWindow.builder().startWindowTime(1444L).build(), null
        );

        assertEquals(Double.valueOf(1050100), some);
    }

    @Test
    public void sumError() {
        when(paymentRepository.sumOperationErrorWithGroupBy(any(), any(), any(), any(), any(), any()))
                .thenReturn(1050100L);
        Double some = sumAggregator.sumError(PaymentCheckedField.BIN, new PaymentModel(),
                TimeWindow.builder().startWindowTime(1444L).build(), null, null
        );

        assertEquals(Double.valueOf(1050100), some);
    }
}
