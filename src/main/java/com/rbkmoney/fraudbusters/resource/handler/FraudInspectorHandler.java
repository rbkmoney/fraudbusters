package com.rbkmoney.fraudbusters.resource.handler;

import com.rbkmoney.damsel.domain.RiskScore;
import com.rbkmoney.damsel.proxy_inspector.Context;
import com.rbkmoney.damsel.proxy_inspector.InspectorProxySrv;
import com.rbkmoney.fraudbusters.converter.ContextToFraudModelConverter;
import com.rbkmoney.fraudbusters.converter.FraudResultRiskScoreConverter;
import com.rbkmoney.fraudbusters.domain.FraudResult;
import com.rbkmoney.fraudo.model.FraudModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.thrift.TException;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class FraudInspectorHandler implements InspectorProxySrv.Iface {

    private final ReplyingKafkaTemplate<String, FraudModel, FraudResult> kafkaTemplate;
    private final String requestTopic;
    private final String requestReplyTopic;
    private final FraudResultRiskScoreConverter resultConverter;
    private final ContextToFraudModelConverter requestConverter;

    @Override
    public RiskScore inspectPayment(Context context) throws TException {
        try {
            FraudModel model = requestConverter.convert(context);
            ProducerRecord<String, FraudModel> record = new ProducerRecord<>(requestTopic, model);
            record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, requestReplyTopic.getBytes()));
            RequestReplyFuture<String, FraudModel, FraudResult> sendAndReceive = kafkaTemplate.sendAndReceive(record);
            ConsumerRecord<String, FraudResult> consumerRecord = sendAndReceive.get(60L, TimeUnit.SECONDS);
            return resultConverter.convert(consumerRecord.value());
        } catch (Exception e) {
            log.error("Error when inspectPayment() e: ", e);
            throw new TException("Error when inspectPayment() e: ", e);
        }
    }

}
