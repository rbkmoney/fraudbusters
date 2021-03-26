package com.rbkmoney.fraudbusters.config.p2p;

import com.rbkmoney.damsel.wb_list.WbListServiceSrv;
import com.rbkmoney.fraudbusters.fraud.constant.P2PCheckedField;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.P2PModel;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.fraud.p2p.aggregator.P2PUniqueValueAggregatorImpl;
import com.rbkmoney.fraudbusters.fraud.p2p.aggregator.SumP2PAggregatorImpl;
import com.rbkmoney.fraudbusters.fraud.p2p.finder.P2pInListFinderImpl;
import com.rbkmoney.fraudbusters.fraud.p2p.resolver.CountryP2PResolverImpl;
import com.rbkmoney.fraudbusters.fraud.p2p.resolver.DbP2pFieldResolver;
import com.rbkmoney.fraudbusters.fraud.p2p.resolver.P2PModelFieldResolver;
import com.rbkmoney.fraudbusters.fraud.payment.CountryByIpResolver;
import com.rbkmoney.fraudbusters.fraud.payment.finder.PaymentInListFinderImpl;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.DatabasePaymentFieldResolver;
import com.rbkmoney.fraudbusters.repository.PaymentRepository;
import com.rbkmoney.fraudbusters.repository.impl.p2p.EventP2PRepository;
import com.rbkmoney.fraudo.aggregator.CountAggregator;
import com.rbkmoney.fraudo.aggregator.SumAggregator;
import com.rbkmoney.fraudo.aggregator.UniqueValueAggregator;
import com.rbkmoney.fraudo.finder.InListFinder;
import com.rbkmoney.fraudo.p2p.factory.P2PFraudVisitorFactory;
import com.rbkmoney.fraudo.p2p.resolver.P2PGroupResolver;
import com.rbkmoney.fraudo.p2p.resolver.P2PTimeWindowResolver;
import com.rbkmoney.fraudo.p2p.visitor.impl.FirstFindP2PVisitorImpl;
import com.rbkmoney.fraudo.resolver.CountryResolver;
import com.rbkmoney.fraudo.resolver.FieldResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class P2PFraudoConfig {

    @Bean
    public SumAggregator<P2PModel, P2PCheckedField> sumP2PAggregator(EventP2PRepository eventP2PRepository,
                                                                     DbP2pFieldResolver dbP2pFieldResolver) {
        return new SumP2PAggregatorImpl(eventP2PRepository, dbP2pFieldResolver);
    }

    @Bean
    public UniqueValueAggregator<P2PModel, P2PCheckedField> uniqueValueP2PAggregator(
            EventP2PRepository eventP2PRepository, DbP2pFieldResolver dbP2pFieldResolver) {
        return new P2PUniqueValueAggregatorImpl(eventP2PRepository, dbP2pFieldResolver);
    }

    @Bean
    public CountryResolver<P2PCheckedField> countryP2PResolver(CountryByIpResolver countryByIpResolver) {
        return new CountryP2PResolverImpl(countryByIpResolver);
    }

    @Bean
    public FieldResolver<P2PModel, P2PCheckedField> p2PModelFieldResolver() {
        return new P2PModelFieldResolver();
    }

    @Bean
    public InListFinder<PaymentModel, PaymentCheckedField> paymentInListFinder(
            WbListServiceSrv.Iface wbListServiceSrv,
            PaymentRepository fraudResultRepository,
            DatabasePaymentFieldResolver databasePaymentFieldResolver) {
        return new PaymentInListFinderImpl(wbListServiceSrv, databasePaymentFieldResolver, fraudResultRepository);
    }

    @Bean
    public InListFinder<P2PModel, P2PCheckedField> p2pInListFinder(WbListServiceSrv.Iface wbListServiceSrv,
                                                                   EventP2PRepository eventP2PRepository,
                                                                   DbP2pFieldResolver dbP2pFieldResolver) {
        return new P2pInListFinderImpl(wbListServiceSrv, dbP2pFieldResolver, eventP2PRepository);
    }

    @Bean
    public FirstFindP2PVisitorImpl<P2PModel, P2PCheckedField> p2pRuleVisitor(
            CountAggregator<P2PModel, P2PCheckedField> countP2PAggregator,
            SumAggregator<P2PModel, P2PCheckedField> sumP2PAggregator,
            UniqueValueAggregator<P2PModel, P2PCheckedField> uniqueValueP2PAggregator,
            CountryResolver<P2PCheckedField> countryP2PResolver,
            InListFinder<P2PModel, P2PCheckedField> p2pInListFinder,
            FieldResolver<P2PModel, P2PCheckedField> p2PModelFieldResolver) {
        return new P2PFraudVisitorFactory().createVisitor(
                countP2PAggregator,
                sumP2PAggregator,
                uniqueValueP2PAggregator,
                countryP2PResolver,
                p2pInListFinder,
                p2PModelFieldResolver,
                new P2PGroupResolver<>(p2PModelFieldResolver),
                new P2PTimeWindowResolver());
    }
}
