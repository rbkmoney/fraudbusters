package com.rbkmoney.fraudbusters.config;

import com.rbkmoney.fraudbusters.fraud.constant.P2PCheckedField;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.P2PModel;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.stream.RuleApplierImpl;
import com.rbkmoney.fraudbusters.template.pool.*;
import com.rbkmoney.fraudo.p2p.visitor.impl.FirstFindP2PVisitorImpl;
import com.rbkmoney.fraudo.payment.visitor.impl.FirstFindVisitorImpl;
import org.antlr.v4.runtime.ParserRuleContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class P2PPoolConfig {

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
    public Pool<ParserRuleContext> templateP2PPoolImpl() {
        return new TemplatePoolImpl();
    }

    @Bean
    public RuleApplierImpl<P2PModel> ruleP2PApplier(FirstFindP2PVisitorImpl<P2PModel, P2PCheckedField> p2pRuleVisitor,
                                                    Pool<ParserRuleContext> templateP2PPoolImpl) {
        return new RuleApplierImpl<>(p2pRuleVisitor, templateP2PPoolImpl);
    }
}
