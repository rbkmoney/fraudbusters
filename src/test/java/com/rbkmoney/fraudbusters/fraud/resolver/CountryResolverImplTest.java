package com.rbkmoney.fraudbusters.fraud.resolver;

import com.rbkmoney.damsel.geo_ip.GeoIpServiceSrv;
import com.rbkmoney.fraudbusters.config.CachingConfig;
import com.rbkmoney.fraudbusters.constant.ClickhouseUtilsValue;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.payment.CountryByIpResolver;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.CountryResolverImpl;
import org.apache.thrift.TException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
@ContextConfiguration(classes = {CachingConfig.class, CountryByIpResolver.class, CountryResolverImpl.class})
public class CountryResolverImplTest {

    public static final String IP = "123.123.123.123";
    public static final String COUNTRY_GEO_ISO_CODE = "RU";

    @MockBean
    private GeoIpServiceSrv.Iface geoIpServiceSrv;

    @Autowired
    private CountryResolverImpl countryResolver;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        cacheManager.getCache("resolveCountry").clear();
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

    @Test
    void resolveCountryWithCache() throws TException {
        Mockito.when(geoIpServiceSrv.getLocationIsoCode(IP)).thenReturn(COUNTRY_GEO_ISO_CODE);

        String firstResult = countryResolver.resolveCountry(PaymentCheckedField.IP, IP);
        String secondResult = countryResolver.resolveCountry(PaymentCheckedField.IP, IP);

        verify(geoIpServiceSrv, times(1)).getLocationIsoCode(IP);
        assertEquals(COUNTRY_GEO_ISO_CODE, firstResult);
        assertEquals(COUNTRY_GEO_ISO_CODE, secondResult);
    }
}