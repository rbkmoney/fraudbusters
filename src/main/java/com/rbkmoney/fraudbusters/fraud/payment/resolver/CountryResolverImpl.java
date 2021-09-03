package com.rbkmoney.fraudbusters.fraud.payment.resolver;

import com.rbkmoney.fraudbusters.constant.ClickhouseUtilsValue;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.payment.CountryByIpResolver;
import com.rbkmoney.fraudo.resolver.CountryResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CountryResolverImpl implements CountryResolver<PaymentCheckedField> {

    private final CountryByIpResolver countryByIpResolver;

    @Override
    public String resolveCountry(PaymentCheckedField checkedField, String fieldValue) {
        String location = null;
        if (PaymentCheckedField.IP.equals(checkedField)) {
            location = countryByIpResolver.resolveCountry(fieldValue);
        } else if (PaymentCheckedField.COUNTRY_BANK.equals(checkedField)) {
            location = fieldValue;
        }
        if (location == null) {
            return ClickhouseUtilsValue.UNKNOWN;
        }
        log.debug("CountryResolverImpl resolve ip: {} country_id: {}", fieldValue, location);
        return location;
    }

}
