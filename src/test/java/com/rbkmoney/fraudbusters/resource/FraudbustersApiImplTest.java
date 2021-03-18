package com.rbkmoney.fraudbusters.resource;

import com.rbkmoney.fraudbusters.converter.CheckedResultToRiskScoreConverter;
import com.rbkmoney.fraudbusters.converter.CheckedResultToSwagRiskScoreConverter;
import com.rbkmoney.fraudbusters.converter.PaymentInspectRequestToFraudRequestConverter;
import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import com.rbkmoney.fraudbusters.domain.ConcreteResultModel;
import com.rbkmoney.fraudbusters.domain.FraudRequest;
import com.rbkmoney.fraudbusters.domain.FraudResult;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.stream.TemplateVisitor;
import com.rbkmoney.fraudo.constant.ResultStatus;
import com.rbkmoney.swag.fraudbusters.model.PaymentInspectRequest;
import com.rbkmoney.swag.fraudbusters.model.RiskScore;
import com.rbkmoney.swag.fraudbusters.model.RiskScoreResult;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {FraudbustersApiImpl.class, CheckedResultToSwagRiskScoreConverter.class, ObjectMapper.class})
class FraudbustersApiImplTest {

    @MockBean
    TemplateVisitor<PaymentModel, CheckedResultModel> templateVisitor;
    @MockBean
    KafkaTemplate<String, FraudResult> kafkaTemplate;
    @MockBean
    PaymentInspectRequestToFraudRequestConverter paymentInspectRequestToFraudRequestConverter;

    @Autowired
    FraudbustersApiImpl fraudbustersApi;
@Autowired
ObjectMapper objectMapper;
    @Test
    void inspectPayment() throws JsonProcessingException {
        CheckedResultModel checkedResultModel = new CheckedResultModel();
        ConcreteResultModel concreteResultModel = new ConcreteResultModel();
        concreteResultModel.setResultStatus(ResultStatus.HIGH_RISK);
        checkedResultModel.setResultModel(concreteResultModel);
        when(templateVisitor.visit(any())).thenReturn(checkedResultModel);

        PaymentInspectRequest paymentInspectRequest = new PaymentInspectRequest();

        ResponseEntity<RiskScoreResult> riskScoreResultResponseEntity = fraudbustersApi.inspectPayment(paymentInspectRequest);
        assertEquals(200, riskScoreResultResponseEntity.getStatusCodeValue());
        assertEquals(RiskScore.HIGH, riskScoreResultResponseEntity.getBody().getResult());

        when(paymentInspectRequestToFraudRequestConverter.convert(any())).thenReturn(new FraudRequest());

        concreteResultModel.setResultStatus(ResultStatus.DECLINE);
        checkedResultModel.setResultModel(concreteResultModel);
        when(templateVisitor.visit(any())).thenReturn(checkedResultModel);

        riskScoreResultResponseEntity = fraudbustersApi.inspectPayment(paymentInspectRequest);
        assertEquals(200, riskScoreResultResponseEntity.getStatusCodeValue());
        assertEquals(RiskScore.FATAL, riskScoreResultResponseEntity.getBody().getResult());

    }
}
