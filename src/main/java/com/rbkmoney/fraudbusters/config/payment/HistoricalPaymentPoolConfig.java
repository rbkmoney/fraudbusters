package com.rbkmoney.fraudbusters.config.payment;

import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.pool.HistoricalPool;
import com.rbkmoney.fraudbusters.pool.HistoricalPoolImpl;
import com.rbkmoney.fraudbusters.stream.impl.FullRuleApplierImpl;
import com.rbkmoney.fraudbusters.stream.impl.RuleCheckingApplierImpl;
import com.rbkmoney.fraudbusters.util.CheckedResultFactory;
import com.rbkmoney.fraudo.payment.visitor.impl.FirstFindVisitorImpl;
import org.antlr.v4.runtime.ParserRuleContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class HistoricalPaymentPoolConfig {

    @Bean
    public HistoricalPool<List<String>> timeGroupPoolImpl() {
        return new HistoricalPoolImpl<>("time-group-pool");
    }

    @Bean
    public HistoricalPool<String> timeReferencePoolImpl() {
        return new HistoricalPoolImpl<>("time-reference-pool");
    }

    @Bean
    public HistoricalPool<String> timeGroupReferencePoolImpl() {
        return new HistoricalPoolImpl<>("time-group-reference-pool");
    }

    @Bean
    public HistoricalPool<ParserRuleContext> timeTemplatePoolImpl() {
        return new HistoricalPoolImpl<>("time-template-pool");
    }

    @Bean
    public FullRuleApplierImpl fullRuleApplier(
            FirstFindVisitorImpl<PaymentModel, PaymentCheckedField> fullPaymentRuleVisitor,
            HistoricalPool<ParserRuleContext> templatePoolImpl,
            CheckedResultFactory checkedResultFactory) {
        return new FullRuleApplierImpl(fullPaymentRuleVisitor, templatePoolImpl, checkedResultFactory);
    }

    @Bean
    public RuleCheckingApplierImpl<PaymentModel> ruleCheckingApplier(
            FirstFindVisitorImpl<PaymentModel, PaymentCheckedField> paymentRuleVisitor,
            HistoricalPool<ParserRuleContext> timeTemplatePoolImpl,
            CheckedResultFactory checkedResultFactory) {
        return new RuleCheckingApplierImpl<>(paymentRuleVisitor, timeTemplatePoolImpl, checkedResultFactory);
    }

}
