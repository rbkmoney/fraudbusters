package com.rbkmoney.fraudbusters.fraud.payment.resolver;

import com.rbkmoney.fraudbusters.exception.RuleFunctionException;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.payment.CountryByIpResolver;
import com.rbkmoney.fraudo.resolver.CountryResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;

@Slf4j
@RequiredArgsConstructor
public class CountryResolverImpl implements CountryResolver<PaymentCheckedField> {

    private final CountryByIpResolver countryByIpResolver;

    @Override
    public String resolveCountry(PaymentCheckedField checkedField, String fieldValue) {
        try {
            String location = null;
            if (PaymentCheckedField.IP.equals(checkedField)) {
                location = countryByIpResolver.resolveCountry(fieldValue);
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
