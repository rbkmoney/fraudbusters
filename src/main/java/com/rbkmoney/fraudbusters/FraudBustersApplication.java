package com.rbkmoney.fraudbusters;

import com.rbkmoney.fraudbusters.fraud.constant.P2PCheckedField;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.P2PModel;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.stream.StreamManager;
import com.rbkmoney.fraudo.p2p.visitor.impl.FirstFindP2PVisitorImpl;
import com.rbkmoney.fraudo.payment.visitor.impl.FirstFindVisitorImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PreDestroy;

@Slf4j
@EnableScheduling
@ServletComponentScan
@SpringBootApplication
@RequiredArgsConstructor
public class FraudBustersApplication extends SpringApplication {

    private final KafkaListenerEndpointRegistry registry;
    private final FirstFindVisitorImpl<PaymentModel, PaymentCheckedField> paymentRuleVisitor;
    private final FirstFindVisitorImpl<PaymentModel, PaymentCheckedField> fullPaymentRuleVisitor;
    private final FirstFindP2PVisitorImpl<P2PModel, P2PCheckedField> p2pRuleVisitor;
    private final StreamManager streamManager;

    public static void main(String[] args) {
        SpringApplication.run(FraudBustersApplication.class, args);
    }

    @PreDestroy
    public void preDestroy() {
        log.info("FraudBustersApplication preDestroy!");
        streamManager.stop();
        registry.stop();
        paymentRuleVisitor.close();
        fullPaymentRuleVisitor.close();
        p2pRuleVisitor.close();
        log.info("FraudBustersApplication preDestroy finish!");
    }
}
