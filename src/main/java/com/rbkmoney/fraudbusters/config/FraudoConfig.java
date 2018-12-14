package com.rbkmoney.fraudbusters.config;

import com.rbkmoney.fraudbusters.fraud.aggragator.CountAggregatorImpl;
import com.rbkmoney.fraudbusters.fraud.aggragator.SumAggregatorImpl;
import com.rbkmoney.fraudbusters.fraud.aggragator.UniqueValueAggregatorImpl;
import com.rbkmoney.fraudbusters.fraud.finder.BlackListFinder;
import com.rbkmoney.fraudbusters.fraud.finder.WightListFinder;
import com.rbkmoney.fraudbusters.fraud.resolver.CountryResolverImpl;
import com.rbkmoney.fraudo.aggregator.CountAggregator;
import com.rbkmoney.fraudo.aggregator.SumAggregator;
import com.rbkmoney.fraudo.aggregator.UniqueValueAggregator;
import com.rbkmoney.fraudo.factory.FastFraudVisitorFactory;
import com.rbkmoney.fraudo.factory.FraudVisitorFactory;
import com.rbkmoney.fraudo.finder.InListFinder;
import com.rbkmoney.fraudo.resolver.CountryResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FraudoConfig {

    @Bean
    public FraudVisitorFactory fraudVisitorFactory() {
        return new FastFraudVisitorFactory();
    }

    @Bean
    public CountAggregator countAggregator() {
        return new CountAggregatorImpl();
    }

    @Bean
    public SumAggregator sumAggregator() {
        return new SumAggregatorImpl();
    }

    @Bean
    public UniqueValueAggregator uniqueValueAggregator() {
        return new UniqueValueAggregatorImpl();
    }

    @Bean
    public CountryResolver countryResolver() {
        return new CountryResolverImpl();
    }

    @Bean
    public InListFinder blackListFinder() {
        return new BlackListFinder();
    }

    @Bean
    public InListFinder whiteListFinder() {
        return new WightListFinder();
    }

}
