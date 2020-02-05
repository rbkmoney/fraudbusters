package com.rbkmoney.fraudbusters.fraud.p2p.resolver;

import com.rbkmoney.fraudbusters.exception.RuleFunctionException;
import com.rbkmoney.fraudbusters.fraud.constant.P2PCheckedField;
import com.rbkmoney.fraudbusters.fraud.payment.CountryByIpResolver;
import com.rbkmoney.fraudo.resolver.CountryResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;

@Slf4j
@RequiredArgsConstructor
public class CountryP2PResolverImpl implements CountryResolver<P2PCheckedField> {

    private final CountryByIpResolver countryByIpResolver;

    @Override
    public String resolveCountry(P2PCheckedField checkedField, String fieldValue) {
        try {
            String location = null;
            if (P2PCheckedField.IP == checkedField) {
                location = countryByIpResolver.resolveCountry(fieldValue);
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
