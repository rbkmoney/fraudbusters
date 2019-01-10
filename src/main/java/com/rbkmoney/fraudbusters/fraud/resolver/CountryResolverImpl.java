package com.rbkmoney.fraudbusters.fraud.resolver;

import com.rbkmoney.damsel.geo_ip.GeoIpServiceSrv;
import com.rbkmoney.damsel.geo_ip.LocationInfo;
import com.rbkmoney.fraudbusters.exception.UnknownLocationException;
import com.rbkmoney.fraudo.constant.CheckedField;
import com.rbkmoney.fraudo.resolver.CountryResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;

@Slf4j
@RequiredArgsConstructor
public class CountryResolverImpl implements CountryResolver {

    private final GeoIpServiceSrv.Iface geoIpServiceSrv;

    @Override
    public String resolveCountry(CheckedField checkedField, String ip) {
        try {
            LocationInfo location = geoIpServiceSrv.getLocation(ip);
            if (location == null) {
                throw new UnknownLocationException();
            }
            log.debug("CountryResolverImpl resolve ip: {} country_id: {}", ip, location.country_geo_id);
            return String.valueOf(location.country_geo_id);
        } catch (TException e) {
            throw new RuntimeException(e);
        }
    }
}
