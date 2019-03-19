package com.rbkmoney.fraudbusters.fraud.resolver;

import com.rbkmoney.damsel.geo_ip.GeoIpServiceSrv;
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
    public String resolveCountry(CheckedField checkedField, String fieldValue) {
        try {
            String location = null;
            if (CheckedField.IP.equals(checkedField)) {
                location = geoIpServiceSrv.getLocationIsoCode(fieldValue);
            } else if (CheckedField.COUNTRY_BANK.equals(checkedField)) {
                location = fieldValue;
            }
            if (location == null) {
                return UNKNOWN_VALUE;
            }
            log.debug("CountryResolverImpl resolve ip: {} country_id: {}", fieldValue, location);
            return location;
        } catch (TException e) {
            log.error("CountryResolverImpl resolve ip: {} e: ", fieldValue, e);
            return UNKNOWN_VALUE;
        }
    }

}
