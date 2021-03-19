package com.rbkmoney.fraudbusters.resource;

import com.rbkmoney.fraudbusters.converter.CheckedResultToSwagRiskScoreConverter;
import com.rbkmoney.fraudbusters.converter.PaymentInspectRequestToFraudRequestConverter;
import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import com.rbkmoney.fraudbusters.domain.FraudRequest;
import com.rbkmoney.fraudbusters.domain.FraudResult;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.stream.TemplateVisitor;
import com.rbkmoney.swag.fraudbusters.api.FraudbustersApi;
import com.rbkmoney.swag.fraudbusters.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FraudbustersApiImpl implements FraudbustersApi {

    @Value("${kafka.topic.result}")
    private String resultTopic;

    private final CheckedResultToSwagRiskScoreConverter checkedResultToSwagRiskScoreConverter;
    private final PaymentInspectRequestToFraudRequestConverter requestConverter;
    private final TemplateVisitor<PaymentModel, CheckedResultModel> templateVisitor;
    private final KafkaTemplate<String, FraudResult> kafkaFraudResultTemplate;

    @Override
    public ResponseEntity<RiskScoreListResult> inspectP2p(@Valid P2pInspectRequest p2pInspectRequest) {
        //TODO add handle
        RiskScoreListResult riskScoreListResult = new RiskScoreListResult();
        riskScoreListResult.addResultItem(RiskScore.FATAL);
        return ResponseEntity.ok(riskScoreListResult);
    }

    @Override
    public ResponseEntity<RiskScoreResult> inspectPayment(@Valid PaymentInspectRequest paymentInspectRequest) {
        FraudRequest model = requestConverter.convert(paymentInspectRequest);
        RiskScoreResult riskScoreResult = new RiskScoreResult();
        riskScoreResult.setResult(com.rbkmoney.swag.fraudbusters.model.RiskScore.HIGH);
        if (model != null) {
            log.info("Check fraudRequest: {}", model);
            FraudResult fraudResult = new FraudResult(model, templateVisitor.visit(model.getFraudModel()));
            kafkaFraudResultTemplate.send(resultTopic, fraudResult);
            log.info("Checked fraudResult: {}", fraudResult);
            RiskScore riskScore = checkedResultToSwagRiskScoreConverter.convert(fraudResult.getResultModel());
            riskScoreResult.setResult(riskScore);
        }
        return ResponseEntity.ok(riskScoreResult);
    }

}
