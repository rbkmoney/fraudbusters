package com.rbkmoney.fraudbusters.resource.handler;

import com.rbkmoney.damsel.base.InvalidRequest;
import com.rbkmoney.damsel.domain.RiskScore;
import com.rbkmoney.damsel.p2p_insp.InspectResult;
import com.rbkmoney.damsel.p2p_insp.InspectorProxySrv;
import com.rbkmoney.fraudbusters.converter.CheckedResultToRiskScoreConverter;
import com.rbkmoney.fraudbusters.converter.ContextToFraudRequestConverter;
import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import com.rbkmoney.fraudbusters.domain.FraudRequest;
import com.rbkmoney.fraudbusters.domain.FraudResult;
import com.rbkmoney.fraudbusters.domain.ScoresResult;
import com.rbkmoney.fraudbusters.stream.TemplateListVisitor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class FraudP2PInspectorHandler implements InspectorProxySrv.Iface {

    private final String resultTopic;
    private final CheckedResultToRiskScoreConverter resultConverter;
    private final ContextToFraudRequestConverter requestConverter;
    private final TemplateListVisitor templateVisitor;
    private final KafkaTemplate<String, FraudResult> kafkaFraudResultTemplate;

    @Override
    public InspectResult inspectTransfer(com.rbkmoney.damsel.p2p_insp.Context context, List<String> list) throws InvalidRequest, TException {
        try {
            Map<String, RiskScore> scores = new HashMap<>();

            FraudRequest model = requestConverter.convert(context);
            if (model != null) {
                log.info("Check fraudRequest: {}", model);
                Map<String, CheckedResultModel> visit = templateVisitor.visit(model.getFraudModel());
                ScoresResult scoresResult = new ScoresResult(model, visit);
//                kafkaFraudResultTemplate.send(resultTopic, scoresResult);
                log.info("Checked scoresResult: {}", scoresResult);
//                return resultConverter.convert(scoresResult);
                scores = scoresResult.getScores().entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> resultConverter.convert(e.getValue()))
                        );
            }


            return new InspectResult(scores);
        } catch (Exception e) {
            log.error("Error when inspectPayment() e: ", e);
            throw new TException("Error when inspectPayment() e: ", e);
        }
    }
}
