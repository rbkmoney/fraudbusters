package com.rbkmoney.fraudbusters.fraud.resolver;

import com.rbkmoney.damsel.geo_ip.GeoIpServiceSrv;
import com.rbkmoney.fraudbusters.constant.ClickhouseUtilsValue;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.payment.CountryByIpResolver;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.CountryResolverImpl;
import org.apache.thrift.TException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.reset;

@ExtendWith(MockitoExtension.class)
public class CountryResolverImplTest {

    public static final String IP = "123.123.123.123";
    public static final String COUNTRY_GEO_ISO_CODE = "RU";

    @Mock
    GeoIpServiceSrv.Iface geoIpServiceSrv;

    CountryResolverImpl countryResolver;

    @BeforeEach
    void init() {
        reset(geoIpServiceSrv);
        countryResolver = new CountryResolverImpl(new CountryByIpResolver(geoIpServiceSrv));
    }

    @Test
    void resolveCountryUnknownLocationTest() throws TException {
        Mockito.when(geoIpServiceSrv.getLocationIsoCode(IP)).thenReturn(null);
        String result = countryResolver.resolveCountry(PaymentCheckedField.IP, IP);
        assertEquals(result, ClickhouseUtilsValue.UNKNOWN);
    }

    @Test
    void resolveCountryExceptionInvocationTest() throws TException {
        Mockito.when(geoIpServiceSrv.getLocationIsoCode(IP)).thenThrow(new TException());
        String result = countryResolver.resolveCountry(PaymentCheckedField.IP, IP);
        assertEquals(result, ClickhouseUtilsValue.UNKNOWN);
    }

    @Test
    void resolveCountry() throws TException {
        Mockito.when(geoIpServiceSrv.getLocationIsoCode(IP)).thenReturn(COUNTRY_GEO_ISO_CODE);
        String country = countryResolver.resolveCountry(PaymentCheckedField.IP, IP);
        assertEquals(COUNTRY_GEO_ISO_CODE, country);
    }
}