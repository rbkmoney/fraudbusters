package com.rbkmoney.fraudbusters.config;

import com.rbkmoney.damsel.geo_ip.GeoIpServiceSrv;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
public class ExternalServiceConfig {


    @Value("${geo.ip.service.url}")
    private Resource resource;

    @Bean
    public GeoIpServiceSrv.Iface geoIpServiceSrv() {
        try {
            return new THSpawnClientBuilder().withAddress(resource.getURI()).build(GeoIpServiceSrv.Iface.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
