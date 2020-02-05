package com.rbkmoney.fraudbusters.fraud.payment;

import com.rbkmoney.damsel.geo_ip.GeoIpServiceSrv;
import com.rbkmoney.fraudbusters.aspect.BasicMetric;
import lombok.RequiredArgsConstructor;
import org.apache.thrift.TException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CountryByIpResolver {

    private final GeoIpServiceSrv.Iface geoIpServiceSrv;

    @Cacheable("resolveCountry")
    @BasicMetric("resolveCountry")
    public String resolveCountry(String ip) throws TException {
        return geoIpServiceSrv.getLocationIsoCode(ip);
    }

}
