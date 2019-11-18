package com.rbkmoney.fraudbusters.fraud.aggragator;

import com.rbkmoney.fraudbusters.fraud.payment.aggregator.SumAggregatorImpl;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.DBPaymentFieldResolver;
import com.rbkmoney.fraudbusters.fraud.model.FieldModel;
import com.rbkmoney.fraudbusters.repository.EventRepository;
import com.rbkmoney.fraudbusters.repository.MgEventSinkRepository;
import com.rbkmoney.fraudo.model.TimeWindow;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;

public class SumAggregatorImplTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private MgEventSinkRepository mgEventSinkRepository;
    @Mock
    private DBPaymentFieldResolver DBPaymentFieldResolver;
    @Mock
    private FieldModel modelMock;

    SumAggregatorImpl sumAggregator;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        Mockito.when(DBPaymentFieldResolver.resolve(any(), any())).thenReturn(modelMock);
        Mockito.when(eventRepository.sumOperationByFieldWithGroupBy(any(), any(), any(), any(), any())).thenReturn(1050100L);

        sumAggregator = new SumAggregatorImpl(eventRepository, mgEventSinkRepository, DBPaymentFieldResolver);
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
        Mockito.when(eventRepository.sumOperationByFieldWithGroupBy(any(), any(), any(), any(), any())).thenReturn(1050100L);

        Double some = sumAggregator.sum(PaymentCheckedField.BIN, paymentModel, timeWindowBuilder.build(), null);

        Assert.assertEquals(Double.valueOf(1050101), some);

        timeWindowBuilder = TimeWindow.builder().startWindowTime(1444L)
                .endWindowTime(null);
        some = sumAggregator.sum(PaymentCheckedField.BIN, paymentModel, timeWindowBuilder.build(), null);

        Assert.assertEquals(Double.valueOf(1050101), some);
    }

    @Test
    public void sumSuccess() {
        Double some = sumAggregator.sum(PaymentCheckedField.BIN, new PaymentModel(),
                TimeWindow.builder().startWindowTime(1444L).build(), null);

        Assert.assertEquals(Double.valueOf(1050100), some);
    }

    @Test
    public void sumError() {
        Double some = sumAggregator.sum(PaymentCheckedField.BIN, new PaymentModel(),
                TimeWindow.builder().startWindowTime(1444L).build(), null);

        Assert.assertEquals(some, Double.valueOf(1050100));
    }
}