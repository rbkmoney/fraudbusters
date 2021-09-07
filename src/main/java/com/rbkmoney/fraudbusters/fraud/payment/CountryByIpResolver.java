package com.rbkmoney.fraudbusters.fraud.payment;

import com.rbkmoney.damsel.geo_ip.GeoIpServiceSrv;
import com.rbkmoney.fraudbusters.aspect.BasicMetric;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CountryByIpResolver {

    private final GeoIpServiceSrv.Iface geoIpServiceSrv;

    @Cacheable(value = "resolveCountry", key = "#ip")
    @BasicMetric("resolveCountry")
    public String resolveCountry(String ip) {
        try {
            return geoIpServiceSrv.getLocationIsoCode(ip);
        } catch (TException e) {
            log.warn("CountryByIpResolver error when getLocationIsoCode e: ", e);
            return null;
        }
    }

}
