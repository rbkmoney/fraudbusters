package com.rbkmoney.fraudbusters.fraud.p2p.resolver;

import com.rbkmoney.fraudbusters.constant.ClickhouseUtilsValue;
import com.rbkmoney.fraudbusters.fraud.constant.P2PCheckedField;
import com.rbkmoney.fraudbusters.fraud.payment.CountryByIpResolver;
import com.rbkmoney.fraudo.resolver.CountryResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CountryP2PResolverImpl implements CountryResolver<P2PCheckedField> {

    private final CountryByIpResolver countryByIpResolver;

    @Override
    public String resolveCountry(P2PCheckedField checkedField, String fieldValue) {
        String location = null;
        if (P2PCheckedField.IP == checkedField) {
            location = countryByIpResolver.resolveCountry(fieldValue);
        } else if (P2PCheckedField.COUNTRY_BANK == checkedField) {
            location = fieldValue;
        }
        if (location == null) {
            return ClickhouseUtilsValue.UNKNOWN;
        }
        log.debug("CountryResolverImpl resolve ip: {} country_id: {}", fieldValue, location);
        return location;
    }

}
