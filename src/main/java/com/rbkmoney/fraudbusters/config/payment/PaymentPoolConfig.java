package com.rbkmoney.fraudbusters.config.payment;

import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.pool.Pool;
import com.rbkmoney.fraudbusters.pool.PoolImpl;
import com.rbkmoney.fraudbusters.stream.impl.RuleApplierImpl;
import com.rbkmoney.fraudbusters.util.CheckedResultFactory;
import com.rbkmoney.fraudo.payment.visitor.impl.FirstFindVisitorImpl;
import org.antlr.v4.runtime.ParserRuleContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class PaymentPoolConfig {

    @Bean
    public Pool<List<String>> groupPoolImpl() {
        return new PoolImpl<>("group");
    }

    @Bean
    public Pool<String> referencePoolImpl() {
        return new PoolImpl<>("reference");
    }

    @Bean
    public Pool<String> groupReferencePoolImpl() {
        return new PoolImpl<>("group-reference");
    }

    @Bean
    public Pool<ParserRuleContext> templatePoolImpl() {
        return new PoolImpl<>("template");
    }

    @Bean
    public RuleApplierImpl<PaymentModel> ruleApplier(
            FirstFindVisitorImpl<PaymentModel, PaymentCheckedField> paymentRuleVisitor,
            Pool<ParserRuleContext> templatePoolImpl,
            CheckedResultFactory checkedResultFactory) {
        return new RuleApplierImpl<>(paymentRuleVisitor, templatePoolImpl, checkedResultFactory);
    }

}
