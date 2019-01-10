package com.rbkmoney.fraudbusters.fraud.resolver;

import com.rbkmoney.damsel.geo_ip.GeoIpServiceSrv;
import com.rbkmoney.damsel.geo_ip.LocationInfo;
import com.rbkmoney.fraudbusters.exception.UnknownLocationException;
import com.rbkmoney.fraudo.constant.CheckedField;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class CountryResolverImplTest {

    public static final String TEST = "test";
    public static final int COUNTRY_GEO_ID = 12345;

    @Mock
    GeoIpServiceSrv.Iface geoIpServiceSrv;
    CountryResolverImpl countryResolver;

    @Before
    public void init() throws TException {
        MockitoAnnotations.initMocks(this);
        LocationInfo locationInfo = new LocationInfo();
        locationInfo.setCountryGeoId(COUNTRY_GEO_ID);
        Mockito.when(geoIpServiceSrv.getLocation(TEST)).thenReturn(locationInfo);
        countryResolver = new CountryResolverImpl(geoIpServiceSrv);
    }

    @Test
    public void resolveCountry() {
        String country = countryResolver.resolveCountry(CheckedField.IP, TEST);
        Assert.assertEquals(String.valueOf(COUNTRY_GEO_ID), country);
    }

    @Test(expected = UnknownLocationException.class)
    public void resolveCountryUnknownLocationTest() throws TException {
        Mockito.when(geoIpServiceSrv.getLocation(TEST)).thenReturn(null);
        countryResolver.resolveCountry(CheckedField.IP, "123.123.123.123");
    }
}