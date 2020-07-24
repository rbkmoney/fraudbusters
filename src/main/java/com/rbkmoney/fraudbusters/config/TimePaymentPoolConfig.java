package com.rbkmoney.fraudbusters.config;

import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.stream.impl.FullRuleApplierImpl;
import com.rbkmoney.fraudbusters.stream.RuleApplier;
import com.rbkmoney.fraudbusters.template.pool.TimePool;
import com.rbkmoney.fraudbusters.template.pool.TimePoolImpl;
import com.rbkmoney.fraudbusters.util.CheckedResultFactory;
import com.rbkmoney.fraudo.payment.visitor.impl.FirstFindVisitorImpl;
import org.antlr.v4.runtime.ParserRuleContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class TimePaymentPoolConfig {

    @Bean
    public TimePool<List<String>> timeGroupPoolImpl() {
        return new TimePoolImpl<>();
    }

    @Bean
    public TimePool<String> timeReferencePoolImpl() {
        return new TimePoolImpl<>();
    }

    @Bean
    public TimePool<String> timeGroupReferencePoolImpl() {
        return new TimePoolImpl<>();
    }

    @Bean
    public TimePool<ParserRuleContext> timeTemplatePoolImpl() {
        return new TimePoolImpl<>();
    }

    @Bean
    public RuleApplier<PaymentModel> fullRuleApplier(FirstFindVisitorImpl<PaymentModel, PaymentCheckedField> paymentRuleVisitor,
                                                     TimePool<ParserRuleContext> templatePoolImpl,
                                                     CheckedResultFactory checkedResultFactory) {
        return new FullRuleApplierImpl(paymentRuleVisitor, templatePoolImpl, checkedResultFactory);
    }

}
