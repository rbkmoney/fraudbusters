package com.rbkmoney.fraudbusters.config;

import com.rbkmoney.damsel.wb_list.WbListServiceSrv;
import com.rbkmoney.fraudbusters.fraud.constant.P2PCheckedField;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.localstorage.LocalResultStorageRepository;
import com.rbkmoney.fraudbusters.fraud.localstorage.aggregator.LocalCountAggregatorDecorator;
import com.rbkmoney.fraudbusters.fraud.localstorage.aggregator.LocalSumAggregatorDecorator;
import com.rbkmoney.fraudbusters.fraud.localstorage.aggregator.LocalUniqueValueAggregatorDecorator;
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
import com.rbkmoney.fraudbusters.repository.PaymentRepository;
import com.rbkmoney.fraudbusters.repository.impl.analytics.AnalyticsChargebackRepository;
import com.rbkmoney.fraudbusters.repository.impl.analytics.AnalyticsRefundRepository;
import com.rbkmoney.fraudbusters.repository.impl.p2p.EventP2PRepository;
import com.rbkmoney.fraudo.aggregator.CountAggregator;
import com.rbkmoney.fraudo.aggregator.SumAggregator;
import com.rbkmoney.fraudo.aggregator.UniqueValueAggregator;
import com.rbkmoney.fraudo.finder.InListFinder;
import com.rbkmoney.fraudo.p2p.factory.P2PFraudVisitorFactory;
import com.rbkmoney.fraudo.p2p.resolver.P2PGroupResolver;
import com.rbkmoney.fraudo.p2p.resolver.P2PTimeWindowResolver;
import com.rbkmoney.fraudo.p2p.visitor.impl.FirstFindP2PVisitorImpl;
import com.rbkmoney.fraudo.payment.aggregator.CountPaymentAggregator;
import com.rbkmoney.fraudo.payment.aggregator.SumPaymentAggregator;
import com.rbkmoney.fraudo.payment.factory.FraudVisitorFactoryImpl;
import com.rbkmoney.fraudo.payment.factory.FullVisitorFactoryImpl;
import com.rbkmoney.fraudo.payment.resolver.PaymentGroupResolver;
import com.rbkmoney.fraudo.payment.resolver.PaymentTimeWindowResolver;
import com.rbkmoney.fraudo.payment.visitor.impl.FirstFindVisitorImpl;
import com.rbkmoney.fraudo.resolver.CountryResolver;
import com.rbkmoney.fraudo.resolver.FieldResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FraudoConfig {

    @Bean
    public CountPaymentAggregator<PaymentModel, PaymentCheckedField> countAggregator(PaymentRepository fraudResultRepository,
                                                                                     AnalyticsRefundRepository analyticsRefundRepository,
                                                                                     AnalyticsChargebackRepository analyticsChargebackRepository,
                                                                                     DBPaymentFieldResolver dbPaymentFieldResolver) {
        return new CountAggregatorImpl(dbPaymentFieldResolver, fraudResultRepository, analyticsRefundRepository, analyticsChargebackRepository);
    }

    @Bean
    public CountPaymentAggregator<PaymentModel, PaymentCheckedField> countResultAggregator(
            LocalResultStorageRepository localResultStorageRepository,
            PaymentRepository paymentRepositoryImpl,
            AnalyticsRefundRepository analyticsRefundRepository,
            AnalyticsChargebackRepository analyticsChargebackRepository,
            DBPaymentFieldResolver dbPaymentFieldResolver) {
        CountAggregatorImpl countAggregatorDecorator = new CountAggregatorImpl(dbPaymentFieldResolver, paymentRepositoryImpl, analyticsRefundRepository, analyticsChargebackRepository);
        return new LocalCountAggregatorDecorator(countAggregatorDecorator, dbPaymentFieldResolver, localResultStorageRepository);
    }

    @Bean
    public CountAggregator<P2PModel, P2PCheckedField> countP2PAggregator(EventP2PRepository eventP2PRepository,
                                                                         DbP2pFieldResolver dbP2pFieldResolver) {
        return new CountP2PAggregatorImpl(eventP2PRepository, dbP2pFieldResolver);
    }

    @Bean
    public SumPaymentAggregator<PaymentModel, PaymentCheckedField> sumAggregator(PaymentRepository fraudResultRepository,
                                                                                 AnalyticsRefundRepository analyticsRefundRepository,
                                                                                 AnalyticsChargebackRepository analyticsChargebackRepository,
                                                                                 DBPaymentFieldResolver dbPaymentFieldResolver) {
        return new SumAggregatorImpl(dbPaymentFieldResolver, fraudResultRepository, analyticsRefundRepository, analyticsChargebackRepository);
    }

    @Bean
    public SumPaymentAggregator<PaymentModel, PaymentCheckedField> sumResultAggregator(LocalResultStorageRepository localResultStorageRepository,
                                                                                       PaymentRepository paymentRepositoryImpl,
                                                                                       AnalyticsRefundRepository analyticsRefundRepository,
                                                                                       AnalyticsChargebackRepository analyticsChargebackRepository,
                                                                                       DBPaymentFieldResolver dbPaymentFieldResolver) {
        SumAggregatorImpl sumAggregator = new SumAggregatorImpl(dbPaymentFieldResolver, paymentRepositoryImpl, analyticsRefundRepository, analyticsChargebackRepository);
        return new LocalSumAggregatorDecorator(sumAggregator, dbPaymentFieldResolver, localResultStorageRepository);
    }

    @Bean
    public SumAggregator<P2PModel, P2PCheckedField> sumP2PAggregator(EventP2PRepository eventP2PRepository,
                                                                     DbP2pFieldResolver dbP2pFieldResolver) {
        return new SumP2PAggregatorImpl(eventP2PRepository, dbP2pFieldResolver);
    }

    @Bean
    public UniqueValueAggregator<PaymentModel, PaymentCheckedField> uniqueValueAggregator(PaymentRepository fraudResultRepository,
                                                                                          DBPaymentFieldResolver dbPaymentFieldResolver) {
        return new UniqueValueAggregatorImpl(dbPaymentFieldResolver, fraudResultRepository);
    }

    @Bean
    public UniqueValueAggregator<PaymentModel, PaymentCheckedField> uniqueValueResultAggregator(LocalResultStorageRepository localResultStorageRepository,
                                                                                                PaymentRepository fraudResultRepository,
                                                                                                DBPaymentFieldResolver dbPaymentFieldResolver) {
        UniqueValueAggregatorImpl uniqueValueAggregator = new UniqueValueAggregatorImpl(dbPaymentFieldResolver, fraudResultRepository);
        return new LocalUniqueValueAggregatorDecorator(uniqueValueAggregator, dbPaymentFieldResolver, localResultStorageRepository);
    }

    @Bean
    public UniqueValueAggregator<P2PModel, P2PCheckedField> uniqueValueP2PAggregator(EventP2PRepository eventP2PRepository, DbP2pFieldResolver dbP2pFieldResolver) {
        return new P2PUniqueValueAggregatorImpl(eventP2PRepository, dbP2pFieldResolver);
    }

    @Bean
    public CountryResolver<PaymentCheckedField> countryResolver(CountryByIpResolver countryByIpResolver) {
        return new CountryResolverImpl(countryByIpResolver);
    }

    @Bean
    public CountryResolver<P2PCheckedField> countryP2PResolver(CountryByIpResolver countryByIpResolver) {
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
                                                                               PaymentRepository fraudResultRepository,
                                                                               DBPaymentFieldResolver dbPaymentFieldResolver) {
        return new PaymentInListFinderImpl(wbListServiceSrv, dbPaymentFieldResolver, fraudResultRepository);
    }

    @Bean
    public InListFinder<P2PModel, P2PCheckedField> p2pInListFinder(WbListServiceSrv.Iface wbListServiceSrv,
                                                                   EventP2PRepository eventP2PRepository,
                                                                   DbP2pFieldResolver dbP2pFieldResolver) {
        return new P2pInListFinderImpl(wbListServiceSrv, dbP2pFieldResolver, eventP2PRepository);
    }

    @Bean
    public FirstFindVisitorImpl<PaymentModel, PaymentCheckedField> paymentRuleVisitor(
            CountPaymentAggregator<PaymentModel, PaymentCheckedField> countAggregator,
            SumPaymentAggregator<PaymentModel, PaymentCheckedField> sumAggregator,
            UniqueValueAggregator<PaymentModel, PaymentCheckedField> uniqueValueAggregator,
            CountryResolver<PaymentCheckedField> countryResolver,
            InListFinder<PaymentModel, PaymentCheckedField> paymentInListFinder,
            FieldResolver<PaymentModel, PaymentCheckedField> paymentModelFieldResolver) {
        return new FraudVisitorFactoryImpl().createVisitor(
                countAggregator,
                sumAggregator,
                uniqueValueAggregator,
                countryResolver,
                paymentInListFinder,
                paymentModelFieldResolver,
                new PaymentGroupResolver<>(paymentModelFieldResolver),
                new PaymentTimeWindowResolver());
    }

    @Bean
    public FirstFindVisitorImpl<PaymentModel, PaymentCheckedField> fullPaymentRuleVisitor(
            CountPaymentAggregator<PaymentModel, PaymentCheckedField> countResultAggregator,
            SumPaymentAggregator<PaymentModel, PaymentCheckedField> sumResultAggregator,
            UniqueValueAggregator<PaymentModel, PaymentCheckedField> uniqueValueResultAggregator,
            CountryResolver<PaymentCheckedField> countryResolver,
            InListFinder<PaymentModel, PaymentCheckedField> paymentInListFinder,
            FieldResolver<PaymentModel, PaymentCheckedField> paymentModelFieldResolver) {
        return new FullVisitorFactoryImpl().createVisitor(
                countResultAggregator,
                sumResultAggregator,
                uniqueValueResultAggregator,
                countryResolver,
                paymentInListFinder,
                paymentModelFieldResolver,
                new PaymentGroupResolver<>(paymentModelFieldResolver),
                new PaymentTimeWindowResolver());
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
