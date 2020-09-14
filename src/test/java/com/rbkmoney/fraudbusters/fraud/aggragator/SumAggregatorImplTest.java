package com.rbkmoney.fraudbusters.fraud.aggragator;

import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.FieldModel;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.fraud.payment.aggregator.SumAggregatorImpl;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.DBPaymentFieldResolver;
import com.rbkmoney.fraudbusters.repository.AggregationRepository;
import com.rbkmoney.fraudbusters.repository.PaymentRepository;
import com.rbkmoney.fraudo.model.TimeWindow;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class SumAggregatorImplTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private AggregationRepository analyticsRefundRepository;
    @Mock
    private AggregationRepository analyticsChargebackRepository;
    @Mock
    private DBPaymentFieldResolver DBPaymentFieldResolver;

    private FieldModel modelMock = new FieldModel("name", "value");

    SumAggregatorImpl sumAggregator;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        when(DBPaymentFieldResolver.resolve(any(), any())).thenReturn(modelMock);
        when(paymentRepository.sumOperationByFieldWithGroupBy(any(), any(), any(), any(), any())).thenReturn(1050100L);
        when(paymentRepository.sumOperationSuccessWithGroupBy(any(), any(), any(), any(), any())).thenReturn(1050100L);
        when(paymentRepository.sumOperationErrorWithGroupBy(any(), any(), any(), any(), any(), any())).thenReturn(1050100L);

        sumAggregator = new SumAggregatorImpl(DBPaymentFieldResolver, paymentRepository, analyticsRefundRepository, analyticsChargebackRepository);
    }

    @Test
    public void sum() {
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setAmount(1L);
        Double some = sumAggregator.sum(PaymentCheckedField.BIN, paymentModel,
                TimeWindow.builder().startWindowTime(1444L).build(), null);

        Assert.assertEquals(Double.valueOf(1050101), some);
    }

    @Test
    public void sumTimeWindow() {
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setAmount(1L);
        TimeWindow.TimeWindowBuilder timeWindowBuilder = TimeWindow.builder().startWindowTime(1444L)
                .endWindowTime(400L);
        when(paymentRepository.sumOperationByFieldWithGroupBy(any(), any(), any(), any(), any())).thenReturn(1050100L);

        Double sum = sumAggregator.sum(PaymentCheckedField.BIN, paymentModel, timeWindowBuilder.build(), null);

        Assert.assertEquals(Double.valueOf(1050101), sum);

        timeWindowBuilder = TimeWindow.builder().startWindowTime(1444L)
                .endWindowTime(null);
        sum = sumAggregator.sum(PaymentCheckedField.BIN, paymentModel, timeWindowBuilder.build(), null);

        Assert.assertEquals(Double.valueOf(1050101), sum);
    }

    @Test
    public void sumSuccess() {
        Double some = sumAggregator.sumSuccess(PaymentCheckedField.BIN, new PaymentModel(),
                TimeWindow.builder().startWindowTime(1444L).build(), null);

        Assert.assertEquals(Double.valueOf(1050100), some);
    }

    @Test
    public void sumError() {
        Double some = sumAggregator.sumError(PaymentCheckedField.BIN, new PaymentModel(),
                TimeWindow.builder().startWindowTime(1444L).build(), null, null);

        Assert.assertEquals(Double.valueOf(1050100), some);
    }
}