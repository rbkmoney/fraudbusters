package com.rbkmoney.fraudbusters.fraud.resolver;

import com.rbkmoney.damsel.geo_ip.GeoIpServiceSrv;
import com.rbkmoney.fraudbusters.aspect.BasicMetric;
import com.rbkmoney.fraudbusters.exception.RuleFunctionException;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudo.resolver.CountryResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.cache.annotation.Cacheable;

@Slf4j
@RequiredArgsConstructor
public class CountryResolverImpl implements CountryResolver<PaymentCheckedField> {

    private final GeoIpServiceSrv.Iface geoIpServiceSrv;

    @Override
    @Cacheable("resolveCountry")
    @BasicMetric("resolveCountry")
    public String resolveCountry(PaymentCheckedField checkedField, String fieldValue) {
        try {
            String location = null;
            if (PaymentCheckedField.IP.equals(checkedField)) {
                location = geoIpServiceSrv.getLocationIsoCode(fieldValue);
            } else if (PaymentCheckedField.COUNTRY_BANK.equals(checkedField)) {
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
