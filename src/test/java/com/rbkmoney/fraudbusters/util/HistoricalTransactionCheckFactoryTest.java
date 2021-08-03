package com.rbkmoney.fraudbusters.util;

import com.rbkmoney.damsel.fraudbusters.CheckResult;
import com.rbkmoney.damsel.fraudbusters.ConcreteCheckResult;
import com.rbkmoney.damsel.fraudbusters.HistoricalTransactionCheck;
import com.rbkmoney.damsel.fraudbusters.Payment;
import com.rbkmoney.damsel.fraudbusters.PaymentStatus;
import com.rbkmoney.fraudo.constant.ResultStatus;
import com.rbkmoney.fraudo.model.ResultModel;
import com.rbkmoney.fraudo.model.RuleResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

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
    private CheckResultFactory checkResultFactory;

    @BeforeEach
    void setUp() {
        factory = new HistoricalTransactionCheckFactory(checkResultFactory);
    }

    @Test
    void createHistoricalTransactionCheck() {
        Payment payment = BeanUtil.createPayment(PaymentStatus.captured);
        String templateString = UUID.randomUUID().toString();
        ResultModel resultModel = createResultModel();
        CheckResult checkResult = new CheckResult()
                .setCheckedTemplate("template")
                .setConcreteCheckResult(new ConcreteCheckResult());
        when(checkResultFactory.createCheckResult(templateString, resultModel)).thenReturn(checkResult);

        var actual = factory.createHistoricalTransactionCheck(payment, templateString, resultModel);

        verify(checkResultFactory, times(1)).createCheckResult(templateString, resultModel);
        assertEquals(actual.getCheckResult(), checkResult);
        assertEquals(actual.getTransaction(), payment);
    }

    private ResultModel createResultModel() {
        RuleResult accepted = new RuleResult();
        accepted.setResultStatus(ResultStatus.ACCEPT);
        RuleResult declined = new RuleResult();
        declined.setResultStatus(ResultStatus.DECLINE);
        ResultModel resultModel = new ResultModel();
        resultModel.setRuleResults(List.of(accepted, declined));
        return resultModel;
    }
}
