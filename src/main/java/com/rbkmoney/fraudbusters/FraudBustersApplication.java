package com.rbkmoney.fraudbusters;

import com.rbkmoney.fraudbusters.fraud.constant.P2PCheckedField;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.P2PModel;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.listener.StartupListener;
import com.rbkmoney.fraudo.p2p.visitor.impl.FirstFindP2PVisitorImpl;
import com.rbkmoney.fraudo.payment.visitor.impl.FirstFindVisitorImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class FraudBustersApplication extends SpringApplication {

    @Autowired
    private KafkaListenerEndpointRegistry registry;

    @Autowired
    private FirstFindVisitorImpl<PaymentModel, PaymentCheckedField> paymentRuleVisitor;

    @Autowired
    private FirstFindP2PVisitorImpl<P2PModel, P2PCheckedField> p2pRuleVisitor;

    @Autowired
    private StartupListener startupListener;

    public static void main(String[] args) {
        SpringApplication.run(FraudBustersApplication.class, args);
    }

    @PreDestroy
    public void preDestroy() {
        log.info("FraudBustersApplication preDestroy!");
        registry.stop();
        paymentRuleVisitor.close();
        p2pRuleVisitor.close();
    }
}
