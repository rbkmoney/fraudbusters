package com.rbkmoney.fraudbusters.resource.handler;

import com.rbkmoney.damsel.domain.RiskScore;
import com.rbkmoney.damsel.p2p_insp.InspectResult;
import com.rbkmoney.damsel.p2p_insp.InspectorProxySrv;
import com.rbkmoney.fraudbusters.constant.ScoresType;
import com.rbkmoney.fraudbusters.converter.CheckedResultToRiskScoreConverter;
import com.rbkmoney.fraudbusters.converter.P2PContextToP2PModelConverter;
import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import com.rbkmoney.fraudbusters.domain.ScoresResult;
import com.rbkmoney.fraudbusters.fraud.model.P2PModel;
import com.rbkmoney.fraudbusters.stream.TemplateVisitor;
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
    private final P2PContextToP2PModelConverter requestConverter;
    private final TemplateVisitor<P2PModel, CheckedResultModel> templateVisitor;
    private final KafkaTemplate<String, ScoresResult<P2PModel>> kafkaFraudResultTemplate;

    @Override
    public InspectResult inspectTransfer(com.rbkmoney.damsel.p2p_insp.Context context, List<String> list) throws TException {
        try {
            Map<String, RiskScore> scores = new HashMap<>();

            P2PModel model = requestConverter.convert(context);
            if (model != null) {
                log.info("Check p2p model: {}", model);

                CheckedResultModel visit = templateVisitor.visit(model);

                HashMap<String, CheckedResultModel> scoresCheck = new HashMap<>();
                scoresCheck.put(ScoresType.FRAUD, visit);

                ScoresResult<P2PModel> scoresResult = new ScoresResult<>(model, scoresCheck);

                kafkaFraudResultTemplate.send(resultTopic, scoresResult);
                log.info("Checked p2p scoresResult: {}", scoresResult);

                scores = scoresResult.getScores().entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> resultConverter.convert(e.getValue()))
                        );
            }

            return new InspectResult(scores);
        } catch (Exception e) {
            log.error("Error when inspectPayment() p2p e: ", e);
            throw new TException("Error when inspectPayment() p2p e: ", e);
        }
    }
}
