package com.rbkmoney.fraudbusters.config;

import com.rbkmoney.damsel.geo_ip.GeoIpServiceSrv;
import com.rbkmoney.damsel.wb_list.WbListServiceSrv;
import com.rbkmoney.fraudbusters.fraud.aggragator.CountAggregatorImpl;
import com.rbkmoney.fraudbusters.fraud.aggragator.SumAggregatorImpl;
import com.rbkmoney.fraudbusters.fraud.aggragator.UniqueValueAggregatorImpl;
import com.rbkmoney.fraudbusters.fraud.finder.PaymentInListFinderImpl;
import com.rbkmoney.fraudbusters.fraud.resolver.CountryResolverImpl;
import com.rbkmoney.fraudbusters.fraud.resolver.DBPaymentFieldResolver;
import com.rbkmoney.fraudbusters.repository.EventRepository;
import com.rbkmoney.fraudbusters.repository.MgEventSinkRepository;
import com.rbkmoney.fraudo.aggregator.CountAggregator;
import com.rbkmoney.fraudo.aggregator.SumAggregator;
import com.rbkmoney.fraudo.aggregator.UniqueValueAggregator;
import com.rbkmoney.fraudo.constant.PaymentCheckedField;
import com.rbkmoney.fraudo.factory.FastFraudVisitorFactory;
import com.rbkmoney.fraudo.factory.FraudVisitorFactory;
import com.rbkmoney.fraudo.finder.InListFinder;
import com.rbkmoney.fraudo.model.PaymentModel;
import com.rbkmoney.fraudo.resolver.CountryResolver;
import com.rbkmoney.fraudo.resolver.FieldResolver;
import com.rbkmoney.fraudo.resolver.GroupByModelResolver;
import com.rbkmoney.fraudo.resolver.payout.PaymentModelFieldResolver;
import com.rbkmoney.fraudo.visitor.impl.FastFraudVisitorImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FraudoConfig {

    @Bean
    public FraudVisitorFactory fraudVisitorFactory() {
        return new FastFraudVisitorFactory();
    }

    @Bean
    public CountAggregator countAggregator(EventRepository eventRepository, MgEventSinkRepository mgEventSinkRepository, DBPaymentFieldResolver DBPaymentFieldResolver) {
        return new CountAggregatorImpl(eventRepository, mgEventSinkRepository, DBPaymentFieldResolver);
    }

    @Bean
    public SumAggregator sumAggregator(EventRepository eventRepository, MgEventSinkRepository mgEventSinkRepository, DBPaymentFieldResolver DBPaymentFieldResolver) {
        return new SumAggregatorImpl(eventRepository, mgEventSinkRepository, DBPaymentFieldResolver);
    }

    @Bean
    public UniqueValueAggregator uniqueValueAggregator(EventRepository eventRepository, DBPaymentFieldResolver DBPaymentFieldResolver) {
        return new UniqueValueAggregatorImpl(eventRepository, DBPaymentFieldResolver);
    }

    @Bean
    public CountryResolver countryResolver(GeoIpServiceSrv.Iface geoIpServiceSrv) {
        return new CountryResolverImpl(geoIpServiceSrv);
    }


    @Bean
    public FieldResolver<PaymentModel, PaymentCheckedField> paymentModelFieldResolver() {
        return new PaymentModelFieldResolver();
    }

    @Bean
    public InListFinder<PaymentModel, PaymentCheckedField> paymentInListFinder(WbListServiceSrv.Iface wbListServiceSrv,
                                                                               EventRepository eventRepository,
                                                                               DBPaymentFieldResolver dbPaymentFieldResolver) {


        return new PaymentInListFinderImpl(wbListServiceSrv, dbPaymentFieldResolver, eventRepository) {
        };
    }

    @Bean
    public FastFraudVisitorImpl paymentRuleVisitor(
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


}
