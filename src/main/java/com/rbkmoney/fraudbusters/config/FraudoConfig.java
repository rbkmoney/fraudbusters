package com.rbkmoney.fraudbusters.config;

import com.rbkmoney.damsel.wb_list.WbListServiceSrv;
import com.rbkmoney.fraudbusters.fraud.constant.P2PCheckedField;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.P2PModel;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.fraud.p2p.aggregator.CountP2PAggregatorImpl;
import com.rbkmoney.fraudbusters.fraud.p2p.aggregator.P2PUniqueValueAggregatorImpl;
import com.rbkmoney.fraudbusters.fraud.p2p.aggregator.SumP2PAggregatorImpl;
import com.rbkmoney.fraudbusters.fraud.p2p.finder.P2pInListFinderImpl;
import com.rbkmoney.fraudbusters.fraud.p2p.resolver.CountryP2PResolverImpl;
import com.rbkmoney.fraudbusters.fraud.p2p.resolver.DbP2pFieldResolver;
import com.rbkmoney.fraudbusters.fraud.p2p.resolver.P2PModelFieldResolver;
import com.rbkmoney.fraudbusters.fraud.payment.CountryByIpResolver;
import com.rbkmoney.fraudbusters.fraud.payment.aggregator.CountAggregatorImpl;
import com.rbkmoney.fraudbusters.fraud.payment.aggregator.SumAggregatorImpl;
import com.rbkmoney.fraudbusters.fraud.payment.aggregator.UniqueValueAggregatorImpl;
import com.rbkmoney.fraudbusters.fraud.payment.finder.PaymentInListFinderImpl;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.CountryResolverImpl;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.DBPaymentFieldResolver;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.PaymentModelFieldResolver;
import com.rbkmoney.fraudbusters.repository.AggregationRepository;
import com.rbkmoney.fraudbusters.repository.impl.p2p.EventP2PRepository;
import com.rbkmoney.fraudbusters.repository.source.SourcePool;
import com.rbkmoney.fraudo.aggregator.CountAggregator;
import com.rbkmoney.fraudo.aggregator.SumAggregator;
import com.rbkmoney.fraudo.aggregator.UniqueValueAggregator;
import com.rbkmoney.fraudo.factory.FirstFraudVisitorFactory;
import com.rbkmoney.fraudo.factory.FraudVisitorFactory;
import com.rbkmoney.fraudo.finder.InListFinder;
import com.rbkmoney.fraudo.resolver.CountryResolver;
import com.rbkmoney.fraudo.resolver.FieldResolver;
import com.rbkmoney.fraudo.resolver.GroupByModelResolver;
import com.rbkmoney.fraudo.visitor.impl.FirstFindVisitorImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FraudoConfig {

    @Bean
    public FraudVisitorFactory fraudVisitorFactory() {
        return new FirstFraudVisitorFactory();
    }

    @Bean
    public CountAggregator countAggregator(SourcePool sourcePool, DBPaymentFieldResolver dbPaymentFieldResolver) {
        return new CountAggregatorImpl(dbPaymentFieldResolver, sourcePool);
    }

    @Bean
    public CountAggregator countP2PAggregator(AggregationRepository eventP2PRepository, DbP2pFieldResolver dbP2pFieldResolver) {
        return new CountP2PAggregatorImpl(eventP2PRepository, dbP2pFieldResolver);
    }

    @Bean
    public SumAggregator sumAggregator(SourcePool sourcePool, DBPaymentFieldResolver dbPaymentFieldResolver) {
        return new SumAggregatorImpl(dbPaymentFieldResolver, sourcePool);
    }

    @Bean
    public SumAggregator sumP2PAggregator(EventP2PRepository eventP2PRepository, DbP2pFieldResolver dbP2pFieldResolver) {
        return new SumP2PAggregatorImpl(eventP2PRepository, dbP2pFieldResolver);
    }

    @Bean
    public UniqueValueAggregator uniqueValueAggregator(SourcePool sourcePool, DBPaymentFieldResolver dbPaymentFieldResolver) {
        return new UniqueValueAggregatorImpl(dbPaymentFieldResolver, sourcePool);
    }

    @Bean
    public UniqueValueAggregator uniqueValueP2PAggregator(EventP2PRepository eventP2PRepository, DbP2pFieldResolver dbP2pFieldResolver) {
        return new P2PUniqueValueAggregatorImpl(eventP2PRepository, dbP2pFieldResolver);
    }

    @Bean
    public CountryResolver countryResolver(CountryByIpResolver countryByIpResolver) {
        return new CountryResolverImpl(countryByIpResolver);
    }

    @Bean
    public CountryResolver countryP2PResolver(CountryByIpResolver countryByIpResolver) {
        return new CountryP2PResolverImpl(countryByIpResolver);
    }

    @Bean
    public FieldResolver<PaymentModel, PaymentCheckedField> paymentModelFieldResolver() {
        return new PaymentModelFieldResolver();
    }

    @Bean
    public FieldResolver<P2PModel, P2PCheckedField> p2PModelFieldResolver() {
        return new P2PModelFieldResolver();
    }

    @Bean
    public InListFinder<PaymentModel, PaymentCheckedField> paymentInListFinder(WbListServiceSrv.Iface wbListServiceSrv,
                                                                               SourcePool sourcePool,
                                                                               DBPaymentFieldResolver dbPaymentFieldResolver) {
        return new PaymentInListFinderImpl(wbListServiceSrv, dbPaymentFieldResolver, sourcePool);
    }

    @Bean
    public InListFinder<P2PModel, P2PCheckedField> p2pInListFinder(WbListServiceSrv.Iface wbListServiceSrv,
                                                                   EventP2PRepository eventP2PRepository,
                                                                   DbP2pFieldResolver dbP2pFieldResolver) {
        return new P2pInListFinderImpl(wbListServiceSrv, dbP2pFieldResolver, eventP2PRepository);
    }

    @Bean
    public FirstFindVisitorImpl<PaymentModel, PaymentCheckedField> paymentRuleVisitor(
            FraudVisitorFactory fraudVisitorFactory,
            CountAggregator<PaymentModel, PaymentCheckedField> countAggregator,
            SumAggregator<PaymentModel, PaymentCheckedField> sumAggregator,
            UniqueValueAggregator<PaymentModel, PaymentCheckedField> uniqueValueAggregator,
            CountryResolver<PaymentCheckedField> countryResolver,
            InListFinder<PaymentModel, PaymentCheckedField> paymentInListFinder,
            FieldResolver<PaymentModel, PaymentCheckedField> paymentModelFieldResolver) {
        return fraudVisitorFactory.createVisitor(
                countAggregator,
                sumAggregator,
                uniqueValueAggregator,
                countryResolver,
                paymentInListFinder,
                paymentModelFieldResolver,
                new GroupByModelResolver<>(paymentModelFieldResolver));
    }

    @Bean
    public FirstFindVisitorImpl<P2PModel, P2PCheckedField> p2pRuleVisitor(
            FraudVisitorFactory fraudVisitorFactory,
            CountAggregator<P2PModel, P2PCheckedField> countP2PAggregator,
            SumAggregator<P2PModel, P2PCheckedField> sumP2PAggregator,
            UniqueValueAggregator<P2PModel, P2PCheckedField> uniqueValueP2PAggregator,
            CountryResolver<P2PCheckedField> countryP2PResolver,
            InListFinder<P2PModel, P2PCheckedField> p2pInListFinder,
            FieldResolver<P2PModel, P2PCheckedField> p2PModelFieldResolver) {
        return fraudVisitorFactory.createVisitor(
                countP2PAggregator,
                sumP2PAggregator,
                uniqueValueP2PAggregator,
                countryP2PResolver,
                p2pInListFinder,
                p2PModelFieldResolver,
                new GroupByModelResolver<>(p2PModelFieldResolver));
    }

}
