package com.rbkmoney.fraudbusters.fraud.resolver.p2p;

import com.rbkmoney.damsel.geo_ip.GeoIpServiceSrv;
import com.rbkmoney.fraudbusters.aspect.BasicMetric;
import com.rbkmoney.fraudbusters.exception.RuleFunctionException;
import com.rbkmoney.fraudbusters.fraud.constant.P2PCheckedField;
import com.rbkmoney.fraudo.resolver.CountryResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.cache.annotation.Cacheable;

@Slf4j
@RequiredArgsConstructor
public class CountryP2PResolverImpl implements CountryResolver<P2PCheckedField> {

    private final GeoIpServiceSrv.Iface geoIpServiceSrv;

    @Override
    @Cacheable("resolveCountry")
    @BasicMetric("resolveCountry")
    public String resolveCountry(P2PCheckedField checkedField, String fieldValue) {
        try {
            String location = null;
            if (P2PCheckedField.IP == checkedField) {
                location = geoIpServiceSrv.getLocationIsoCode(fieldValue);
            } else if (P2PCheckedField.COUNTRY_BANK == checkedField) {
                location = fieldValue;
            }
            if (location == null) {
                return UNKNOWN_VALUE;
            }
            log.debug("CountryResolverImpl resolve ip: {} country_id: {}", fieldValue, location);
            return location;
        } catch (TException e) {
            log.warn("CountryResolverImpl error when resolveCountry e: ", e);
            throw new RuleFunctionException(e);
        }
    }

}
