package com.rbkmoney.fraudbusters.config.payment;

import com.rbkmoney.fraudbusters.fraud.constant.P2PCheckedField;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.localstorage.LocalResultStorageRepository;
import com.rbkmoney.fraudbusters.fraud.localstorage.aggregator.LocalCountAggregatorDecorator;
import com.rbkmoney.fraudbusters.fraud.localstorage.aggregator.LocalSumAggregatorDecorator;
import com.rbkmoney.fraudbusters.fraud.localstorage.aggregator.LocalUniqueValueAggregatorDecorator;
import com.rbkmoney.fraudbusters.fraud.model.P2PModel;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.fraud.p2p.aggregator.CountP2PAggregatorImpl;
import com.rbkmoney.fraudbusters.fraud.p2p.resolver.DbP2pFieldResolver;
import com.rbkmoney.fraudbusters.fraud.payment.CountryByIpResolver;
import com.rbkmoney.fraudbusters.fraud.payment.aggregator.CountAggregatorImpl;
import com.rbkmoney.fraudbusters.fraud.payment.aggregator.SumAggregatorImpl;
import com.rbkmoney.fraudbusters.fraud.payment.aggregator.UniqueValueAggregatorImpl;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.CountryResolverImpl;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.DBPaymentFieldResolver;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.PaymentModelFieldResolver;
import com.rbkmoney.fraudbusters.repository.PaymentRepository;
import com.rbkmoney.fraudbusters.repository.impl.analytics.AnalyticsChargebackRepository;
import com.rbkmoney.fraudbusters.repository.impl.analytics.AnalyticsRefundRepository;
import com.rbkmoney.fraudbusters.repository.impl.p2p.EventP2PRepository;
import com.rbkmoney.fraudo.aggregator.CountAggregator;
import com.rbkmoney.fraudo.aggregator.UniqueValueAggregator;
import com.rbkmoney.fraudo.finder.InListFinder;
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
public class PaymentFraudoConfig {

    @Bean
    public CountPaymentAggregator<PaymentModel, PaymentCheckedField> countAggregator(PaymentRepository fraudResultRepository,
                                                                                     AnalyticsRefundRepository analyticsRefundRepository,
                                                                                     AnalyticsChargebackRepository analyticsChargebackRepository,
                                                                                     DBPaymentFieldResolver dbPaymentFieldResolver) {
        return new CountAggregatorImpl(dbPaymentFieldResolver, fraudResultRepository, analyticsRefundRepository, analyticsChargebackRepository);
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
    public UniqueValueAggregator<PaymentModel, PaymentCheckedField> uniqueValueAggregator(PaymentRepository fraudResultRepository,
                                                                                          DBPaymentFieldResolver dbPaymentFieldResolver) {
        return new UniqueValueAggregatorImpl(dbPaymentFieldResolver, fraudResultRepository);
    }

    @Bean
    public CountryResolver<PaymentCheckedField> countryResolver(CountryByIpResolver countryByIpResolver) {
        return new CountryResolverImpl(countryByIpResolver);
    }

    @Bean
    public FieldResolver<PaymentModel, PaymentCheckedField> paymentModelFieldResolver() {
        return new PaymentModelFieldResolver();
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
    public SumPaymentAggregator<PaymentModel, PaymentCheckedField> sumResultAggregator(LocalResultStorageRepository localResultStorageRepository,
                                                                                       PaymentRepository paymentRepositoryImpl,
                                                                                       AnalyticsRefundRepository analyticsRefundRepository,
                                                                                       AnalyticsChargebackRepository analyticsChargebackRepository,
                                                                                       DBPaymentFieldResolver dbPaymentFieldResolver) {
        SumAggregatorImpl sumAggregator = new SumAggregatorImpl(dbPaymentFieldResolver, paymentRepositoryImpl, analyticsRefundRepository, analyticsChargebackRepository);
        return new LocalSumAggregatorDecorator(sumAggregator, dbPaymentFieldResolver, localResultStorageRepository);
    }

    @Bean
    public UniqueValueAggregator<PaymentModel, PaymentCheckedField> uniqueValueResultAggregator(LocalResultStorageRepository localResultStorageRepository,
                                                                                                PaymentRepository fraudResultRepository,
                                                                                                DBPaymentFieldResolver dbPaymentFieldResolver) {
        UniqueValueAggregatorImpl uniqueValueAggregator = new UniqueValueAggregatorImpl(dbPaymentFieldResolver, fraudResultRepository);
        return new LocalUniqueValueAggregatorDecorator(uniqueValueAggregator, dbPaymentFieldResolver, localResultStorageRepository);
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

}
