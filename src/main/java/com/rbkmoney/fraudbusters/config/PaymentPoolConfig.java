package com.rbkmoney.fraudbusters.config;

import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.stream.RuleApplierImpl;
import com.rbkmoney.fraudbusters.template.pool.Pool;
import com.rbkmoney.fraudbusters.template.pool.PoolImpl;
import com.rbkmoney.fraudo.payment.visitor.impl.FirstFindVisitorImpl;
import org.antlr.v4.runtime.ParserRuleContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class PaymentPoolConfig {

    @Bean
    public Pool<List<String>> groupPoolImpl() {
        return new PoolImpl<>();
    }

    @Bean
    public Pool<String> referencePoolImpl() {
        return new PoolImpl<>();
    }

    @Bean
    public Pool<String> groupReferencePoolImpl() {
        return new PoolImpl<>();
    }

    @Bean
    public Pool<ParserRuleContext> templatePoolImpl() {
        return new PoolImpl<>();
    }

    @Bean
    public RuleApplierImpl<PaymentModel> ruleApplier(FirstFindVisitorImpl<PaymentModel, PaymentCheckedField> paymentRuleVisitor,
                                                     Pool<ParserRuleContext> templatePoolImpl) {
        return new RuleApplierImpl<>(paymentRuleVisitor, templatePoolImpl);
    }

}
