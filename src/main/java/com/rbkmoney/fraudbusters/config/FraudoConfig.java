package com.rbkmoney.fraudbusters.config;

import com.rbkmoney.damsel.geo_ip.GeoIpServiceSrv;
import com.rbkmoney.damsel.wb_list.ListType;
import com.rbkmoney.damsel.wb_list.WbListServiceSrv;
import com.rbkmoney.fraudbusters.fraud.aggragator.CountAggregatorImpl;
import com.rbkmoney.fraudbusters.fraud.aggragator.SumAggregatorImpl;
import com.rbkmoney.fraudbusters.fraud.aggragator.UniqueValueAggregatorImpl;
import com.rbkmoney.fraudbusters.fraud.finder.InListFinderImpl;
import com.rbkmoney.fraudbusters.fraud.resolver.CountryResolverImpl;
import com.rbkmoney.fraudbusters.fraud.resolver.FieldResolver;
import com.rbkmoney.fraudbusters.repository.EventRepository;
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
    public CountAggregator countAggregator(EventRepository eventRepository, FieldResolver fieldResolver) {
        return new CountAggregatorImpl(eventRepository, fieldResolver);
    }

    @Bean
    public SumAggregator sumAggregator(EventRepository eventRepository, FieldResolver fieldResolver) {
        return new SumAggregatorImpl(eventRepository, fieldResolver);
    }

    @Bean
    public UniqueValueAggregator uniqueValueAggregator(EventRepository eventRepository, FieldResolver fieldResolver) {
        return new UniqueValueAggregatorImpl(eventRepository, fieldResolver);
    }

    @Bean
    public CountryResolver countryResolver(GeoIpServiceSrv.Iface geoIpServiceSrv) {
        return new CountryResolverImpl(geoIpServiceSrv);
    }

    @Bean
    public InListFinder blackListFinder(WbListServiceSrv.Iface wbListServiceSrv) {
        return new InListFinderImpl(wbListServiceSrv, ListType.black);

    }

    @Bean
    public InListFinder whiteListFinder(WbListServiceSrv.Iface wbListServiceSrv) {
        return new InListFinderImpl(wbListServiceSrv, ListType.white);
    }

}
