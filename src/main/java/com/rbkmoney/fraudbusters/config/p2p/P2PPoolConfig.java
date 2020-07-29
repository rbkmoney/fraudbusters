package com.rbkmoney.fraudbusters.config.p2p;

import com.rbkmoney.fraudbusters.fraud.constant.P2PCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.P2PModel;
import com.rbkmoney.fraudbusters.pool.Pool;
import com.rbkmoney.fraudbusters.pool.PoolImpl;
import com.rbkmoney.fraudbusters.stream.impl.RuleApplierImpl;
import com.rbkmoney.fraudbusters.util.CheckedResultFactory;
import com.rbkmoney.fraudo.p2p.visitor.impl.FirstFindP2PVisitorImpl;
import org.antlr.v4.runtime.ParserRuleContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class P2PPoolConfig {

    @Bean
    public Pool<List<String>> groupP2PPoolImpl() {
        return new PoolImpl<>("p2p-group");
    }

    @Bean
    public Pool<String> referenceP2PPoolImpl() {
        return new PoolImpl<>("p2p-reference");
    }

    @Bean
    public Pool<String> groupReferenceP2PPoolImpl() {
        return new PoolImpl<>("p2p-group-reference");
    }

    @Bean
    public Pool<ParserRuleContext> templateP2PPoolImpl() {
        return new PoolImpl<>("p2p-template");
    }

    @Bean
    public RuleApplierImpl<P2PModel> ruleP2PApplier(FirstFindP2PVisitorImpl<P2PModel, P2PCheckedField> p2pRuleVisitor,
                                                    Pool<ParserRuleContext> templateP2PPoolImpl,
                                                    CheckedResultFactory checkedResultFactory) {
        return new RuleApplierImpl<>(p2pRuleVisitor, templateP2PPoolImpl, checkedResultFactory);
    }
}
