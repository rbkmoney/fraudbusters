package com.rbkmoney.fraudbusters.fraud.finder;

import com.rbkmoney.damsel.wb_list.WbListServiceSrv;
import com.rbkmoney.fraudbusters.constant.ListType;
import com.rbkmoney.fraudo.constant.CheckedField;
import com.rbkmoney.fraudo.finder.InListFinder;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.anyString;

public class InListFinderImplTest {

    public static final String PARTY_ID = "partyId";
    public static final String SHOP_ID = "shopId";
    public static final String VALUE = "value";
    private InListFinder listFinder;

    @Mock
    private WbListServiceSrv.Iface wbListServiceSrv;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        listFinder = new InListFinderImpl(wbListServiceSrv, ListType.BLACK);
    }

    @Test
    public void findInList() throws TException {
        Mockito.when(wbListServiceSrv.isExist(anyString(), anyString(), anyString(), anyString())).thenReturn(true);
        Boolean isInList = listFinder.findInList(PARTY_ID, SHOP_ID, CheckedField.IP, VALUE);
        Assert.assertTrue(isInList);
    }
}