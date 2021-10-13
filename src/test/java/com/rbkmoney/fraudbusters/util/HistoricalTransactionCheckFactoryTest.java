package com.rbkmoney.fraudbusters.util;

import com.rbkmoney.damsel.fraudbusters.CheckResult;
import com.rbkmoney.damsel.fraudbusters.ConcreteCheckResult;
import com.rbkmoney.damsel.fraudbusters.Payment;
import com.rbkmoney.damsel.fraudbusters.PaymentStatus;
import com.rbkmoney.fraudbusters.converter.CheckedResultModelToCheckResultConverter;
import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import com.rbkmoney.fraudo.constant.ResultStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static com.rbkmoney.fraudbusters.factory.TestObjectsFactory.createCheckedResultModel;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(
        MockitoExtension.class
)
class HistoricalTransactionCheckFactoryTest {

    private HistoricalTransactionCheckFactory factory;

    @Mock
    private CheckedResultModelToCheckResultConverter checkResultConverter;

    private static final String TEMPLATE = UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        factory = new HistoricalTransactionCheckFactory(checkResultConverter);
    }

    @Test
    void createHistoricalTransactionCheck() {
        Payment payment = BeanUtil.createPayment(PaymentStatus.captured);
        CheckedResultModel resultModel = createCheckedResultModel(TEMPLATE, ResultStatus.ACCEPT);
        CheckResult checkResult = new CheckResult()
                .setCheckedTemplate(TEMPLATE)
                .setConcreteCheckResult(new ConcreteCheckResult());
        when(checkResultConverter.convert(resultModel)).thenReturn(checkResult);

        var actual = factory.createHistoricalTransactionCheck(payment, resultModel);

        verify(checkResultConverter, times(1)).convert(resultModel);
        assertEquals(actual.getCheckResult(), checkResult);
        assertEquals(actual.getTransaction(), payment);
    }

}
