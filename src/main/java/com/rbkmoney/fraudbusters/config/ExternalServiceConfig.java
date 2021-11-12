package com.rbkmoney.fraudbusters.config;

import com.rbkmoney.damsel.geo_ip.GeoIpServiceSrv;
import com.rbkmoney.damsel.wb_list.WbListServiceSrv;
import com.rbkmoney.trusted.tokens.TrustedTokensSrv;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
public class ExternalServiceConfig {

    @Bean
    public GeoIpServiceSrv.Iface geoIpServiceSrv(@Value("${geo.ip.service.url}") Resource url) throws IOException {
        return new THSpawnClientBuilder()
                .withAddress(url.getURI())
                .build(GeoIpServiceSrv.Iface.class);
    }

    @Bean
    public WbListServiceSrv.Iface wbListServiceSrv(@Value("${wb.list.service.url}") Resource url) throws IOException {
        return new THSpawnClientBuilder()
                .withAddress(url.getURI())
                .build(WbListServiceSrv.Iface.class);
    }

    @Bean
    public TrustedTokensSrv.Iface trustedTokensSrv(@Value("${trusted.tokens.url}") Resource url,
                                                   @Value("${trusted.tokens.timeout:5000}") Integer timeout)
            throws IOException {
        return new THSpawnClientBuilder()
                .withAddress(url.getURI())
                .withNetworkTimeout(timeout)
                .build(TrustedTokensSrv.Iface.class);
    }

}
