package com.rbkmoney.fraudbusters.config;

import com.rbkmoney.fraudbusters.fraud.model.P2PModel;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.stream.RuleApplierImpl;
import com.rbkmoney.fraudbusters.template.pool.*;
import com.rbkmoney.fraudo.FraudoParser;
import com.rbkmoney.fraudo.visitor.impl.FirstFindVisitorImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class PoolConfig {

    @Bean
    public Pool<List<String>> groupPoolImpl() {
        return new GroupPoolImpl();
    }

    @Bean
    public Pool<String> referencePoolImpl() {
        return new ReferencePoolImpl();
    }

    @Bean
    public Pool<String> groupReferencePoolImpl() {
        return new GroupReferencePoolImpl();
    }

    @Bean
    public Pool<FraudoParser.ParseContext> templatePoolImpl() {
        return new TemplatePoolImpl();
    }

    @Bean
    public Pool<List<String>> groupP2PPoolImpl() {
        return new GroupPoolImpl();
    }

    @Bean
    public Pool<String> referenceP2PPoolImpl() {
        return new ReferencePoolImpl();
    }

    @Bean
    public Pool<String> groupReferenceP2PPoolImpl() {
        return new GroupReferencePoolImpl();
    }

    @Bean
    public Pool<FraudoParser.ParseContext> templateP2PPoolImpl() {
        return new TemplatePoolImpl();
    }

    @Bean
    public RuleApplierImpl<PaymentModel> ruleApplier(FirstFindVisitorImpl paymentRuleVisitor, Pool<FraudoParser.ParseContext> templatePoolImpl) {
        return new RuleApplierImpl<>(paymentRuleVisitor, templatePoolImpl);
    }

    @Bean
    public RuleApplierImpl<P2PModel> ruleP2PApplier(FirstFindVisitorImpl paymentRuleVisitor, Pool<FraudoParser.ParseContext> templateP2PPoolImpl) {
        return new RuleApplierImpl<>(paymentRuleVisitor, templateP2PPoolImpl);
    }
}
