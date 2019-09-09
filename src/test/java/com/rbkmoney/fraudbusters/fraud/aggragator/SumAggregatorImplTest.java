package com.rbkmoney.fraudbusters.fraud.aggragator;

import com.rbkmoney.fraudbusters.fraud.resolver.FieldResolver;
import com.rbkmoney.fraudbusters.repository.EventRepository;
import com.rbkmoney.fraudo.constant.CheckedField;
import com.rbkmoney.fraudo.model.FraudModel;
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
    private FieldResolver fieldResolver;
    @Mock
    private FieldResolver.FieldModel modelMock;

    SumAggregatorImpl sumAggregator;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        Mockito.when(fieldResolver.resolve(any(), any())).thenReturn(modelMock);
        Mockito.when(eventRepository.sumOperationByField(any(), any(), any(), any())).thenReturn(1050100L);

        sumAggregator = new SumAggregatorImpl(eventRepository, fieldResolver);
    }

    @Test
    public void sum() {
        FraudModel fraudModel = new FraudModel();
        fraudModel.setAmount(1L);
        Double some = sumAggregator.sum(CheckedField.BIN, fraudModel, 1444L);

        Assert.assertEquals(Double.valueOf(1050101), some);
    }

    @Test
    public void sumTimeWindow() {
        FraudModel fraudModel = new FraudModel();
        fraudModel.setAmount(1L);
        TimeWindow.TimeWindowBuilder timeWindowBuilder = TimeWindow.builder().startWindowTime(1444L)
                .endWindowTime(400L);
        Mockito.when(eventRepository.sumOperationByFieldWithGroupBy(any(), any(), any(), any(), any())).thenReturn(1050100L);

        Double some = sumAggregator.sum(CheckedField.BIN, fraudModel, timeWindowBuilder.build(), null);

        Assert.assertEquals(Double.valueOf(1050101), some);

        timeWindowBuilder = TimeWindow.builder().startWindowTime(1444L)
                .endWindowTime(null);
        some = sumAggregator.sum(CheckedField.BIN, fraudModel, timeWindowBuilder.build(), null);

        Assert.assertEquals(Double.valueOf(1050101), some);
    }

    @Test
    public void sumSuccess() {
        Double some = sumAggregator.sum(CheckedField.BIN, new FraudModel(), 1444L);

        Assert.assertEquals(Double.valueOf(1050100), some);
    }

    @Test
    public void sumError() {
        Double some = sumAggregator.sum(CheckedField.BIN, new FraudModel(), 1444L);

        Assert.assertEquals(some, Double.valueOf(1050100));
    }
}