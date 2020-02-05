package com.rbkmoney.fraudbusters.fraud.resolver;

import com.rbkmoney.damsel.geo_ip.GeoIpServiceSrv;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.payment.CountryByIpResolver;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.CountryResolverImpl;
import com.rbkmoney.fraudo.resolver.CountryResolver;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class CountryResolverImplTest {

    public static final String TEST = "test";
    public static final String COUNTRY_GEO_ISO_CODE = "RU";

    @Mock
    GeoIpServiceSrv.Iface geoIpServiceSrv;

    CountryResolverImpl countryResolver;

    @Before
    public void init() throws TException {
        MockitoAnnotations.initMocks(this);
        Mockito.when(geoIpServiceSrv.getLocationIsoCode(TEST)).thenReturn(COUNTRY_GEO_ISO_CODE);
        countryResolver = new CountryResolverImpl(new CountryByIpResolver(geoIpServiceSrv));
    }

    @Test
    public void resolveCountry() {
        String country = countryResolver.resolveCountry(PaymentCheckedField.IP, TEST);
        Assert.assertEquals(COUNTRY_GEO_ISO_CODE, country);
    }

    @Test
    public void resolveCountryUnknownLocationTest() throws TException {
        Mockito.when(geoIpServiceSrv.getLocation(TEST)).thenReturn(null);
        String result = countryResolver.resolveCountry(PaymentCheckedField.IP, "123.123.123.123");
        Assert.assertEquals(result, CountryResolver.UNKNOWN_VALUE);
    }

    @Test
    public void resolveCountryExceptionInvocationTest() throws TException {
        Mockito.when(geoIpServiceSrv.getLocation(TEST)).thenThrow(new TException());
        String result = countryResolver.resolveCountry(PaymentCheckedField.IP, "123.123.123.123");
        Assert.assertEquals(result, CountryResolver.UNKNOWN_VALUE);
    }
}