package com.rbkmoney.fraudbusters.config;

import com.rbkmoney.damsel.geo_ip.GeoIpServiceSrv;
import com.rbkmoney.damsel.wb_list.WbListServiceSrv;
import com.rbkmoney.fraudbusters.fraud.aggragator.p2p.CountP2PAggregatorImpl;
import com.rbkmoney.fraudbusters.fraud.aggragator.p2p.P2PUniqueValueAggregatorImpl;
import com.rbkmoney.fraudbusters.fraud.aggragator.p2p.SumP2PAggregatorImpl;
import com.rbkmoney.fraudbusters.fraud.aggragator.payment.CountAggregatorImpl;
import com.rbkmoney.fraudbusters.fraud.aggragator.payment.SumAggregatorImpl;
import com.rbkmoney.fraudbusters.fraud.aggragator.payment.UniqueValueAggregatorImpl;
import com.rbkmoney.fraudbusters.fraud.constant.P2PCheckedField;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.finder.P2pInListFinderImpl;
import com.rbkmoney.fraudbusters.fraud.finder.PaymentInListFinderImpl;
import com.rbkmoney.fraudbusters.fraud.model.P2PModel;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.fraud.p2p.P2PModelFieldResolver;
import com.rbkmoney.fraudbusters.fraud.payout.PaymentModelFieldResolver;
import com.rbkmoney.fraudbusters.fraud.resolver.CountryResolverImpl;
import com.rbkmoney.fraudbusters.fraud.resolver.DBPaymentFieldResolver;
import com.rbkmoney.fraudbusters.fraud.resolver.DbP2pFieldResolver;
import com.rbkmoney.fraudbusters.fraud.resolver.p2p.CountryP2PResolverImpl;
import com.rbkmoney.fraudbusters.repository.EventP2PRepository;
import com.rbkmoney.fraudbusters.repository.EventRepository;
import com.rbkmoney.fraudbusters.repository.MgEventSinkRepository;
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
    public CountAggregator countAggregator(EventRepository eventRepository, MgEventSinkRepository mgEventSinkRepository, DBPaymentFieldResolver dbPaymentFieldResolver) {
        return new CountAggregatorImpl(eventRepository, mgEventSinkRepository, dbPaymentFieldResolver);
    }


    @Bean
    public CountAggregator countP2PAggregator(EventP2PRepository eventP2PRepository, MgEventSinkRepository mgEventSinkRepository, DbP2pFieldResolver dbP2pFieldResolver) {
        return new CountP2PAggregatorImpl(eventP2PRepository, mgEventSinkRepository, dbP2pFieldResolver);
    }

    @Bean
    public SumAggregator sumAggregator(EventRepository eventRepository, MgEventSinkRepository mgEventSinkRepository, DBPaymentFieldResolver dbPaymentFieldResolver) {
        return new SumAggregatorImpl(eventRepository, mgEventSinkRepository, dbPaymentFieldResolver);
    }

    @Bean
    public SumAggregator sumP2PAggregator(EventP2PRepository eventRepository, MgEventSinkRepository mgEventSinkRepository, DbP2pFieldResolver dbP2pFieldResolver) {
        return new SumP2PAggregatorImpl(eventRepository, mgEventSinkRepository, dbP2pFieldResolver);
    }

    @Bean
    public UniqueValueAggregator uniqueValueAggregator(EventRepository eventRepository, DBPaymentFieldResolver dbPaymentFieldResolver) {
        return new UniqueValueAggregatorImpl(eventRepository, dbPaymentFieldResolver);
    }

    @Bean
    public UniqueValueAggregator uniqueValueP2PAggregator(EventP2PRepository eventP2PRepository, DbP2pFieldResolver dbP2pFieldResolver) {
        return new P2PUniqueValueAggregatorImpl(eventP2PRepository, dbP2pFieldResolver);
    }

    @Bean
    public CountryResolver countryResolver(GeoIpServiceSrv.Iface geoIpServiceSrv) {
        return new CountryResolverImpl(geoIpServiceSrv);
    }

    @Bean
    public CountryResolver countryP2PResolver(GeoIpServiceSrv.Iface geoIpServiceSrv) {
        return new CountryP2PResolverImpl(geoIpServiceSrv);
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
                                                                               EventRepository eventRepository,
                                                                               DBPaymentFieldResolver dbPaymentFieldResolver) {
        return new PaymentInListFinderImpl(wbListServiceSrv, dbPaymentFieldResolver, eventRepository);
    }

    @Bean
    public InListFinder<P2PModel, P2PCheckedField> p2pInListFinder(WbListServiceSrv.Iface wbListServiceSrv,
                                                                   EventP2PRepository eventP2PRepository,
                                                                   DbP2pFieldResolver dbP2pFieldResolver) {
        return new P2pInListFinderImpl(wbListServiceSrv, dbP2pFieldResolver, eventP2PRepository);
    }

    @Bean
    public FirstFindVisitorImpl paymentRuleVisitor(
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
    public FirstFindVisitorImpl p2pRuleVisitor(
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
