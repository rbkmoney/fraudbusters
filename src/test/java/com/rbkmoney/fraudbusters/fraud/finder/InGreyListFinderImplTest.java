package com.rbkmoney.fraudbusters.fraud.finder;

import com.rbkmoney.damsel.wb_list.*;
import com.rbkmoney.fraudbusters.fraud.resolver.FieldResolver;
import com.rbkmoney.fraudbusters.repository.EventRepository;
import com.rbkmoney.fraudo.constant.CheckedField;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

public class InGreyListFinderImplTest {

    public static final String PARTY_ID = "partyId";
    public static final String SHOP_ID = "shopId";
    public static final String VALUE = "1234123";
    private InGreyListFinderImpl inGreyListFinder;
    @Mock
    private WbListServiceSrv.Iface wbListServiceSrv;
    @Mock
    private EventRepository eventRepository;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        inGreyListFinder = new InGreyListFinderImpl(wbListServiceSrv, ListType.grey, eventRepository, new FieldResolver());
    }

    @Test
    public void findInList() throws TException {
        Mockito.when(wbListServiceSrv.getRowInfo(any())).thenReturn(new Result().setRowInfo(new RowInfo()));
        Boolean inList = inGreyListFinder.findInList(PARTY_ID, SHOP_ID, CheckedField.CARD_TOKEN, VALUE);

        Assert.assertFalse(inList);
        Instant now = Instant.now();
        Result result = new Result().setRowInfo(RowInfo
                .count_info(new CountInfo()
                        .setCount(5L)
                        .setTimeToLive(now.plusSeconds(10L).toString())
                        .setStartCountTime(now.toString())));

        Mockito.when(wbListServiceSrv.getRowInfo(any())).thenReturn(result);
        Mockito.when(eventRepository.countOperationByField(any(), any(), any(), any())).thenReturn(6);

        inList = inGreyListFinder.findInList(PARTY_ID, SHOP_ID, CheckedField.CARD_TOKEN, VALUE);
        Assert.assertFalse(inList);

        Mockito.when(eventRepository.countOperationByField(any(), any(), any(), any())).thenReturn(4);
        inList = inGreyListFinder.findInList(PARTY_ID, SHOP_ID, CheckedField.CARD_TOKEN, VALUE);
        Assert.assertTrue(inList);

    }

    @Test
    public void testFindInList() throws TException {
        Instant now = Instant.now();
        Result result = new Result().setRowInfo(RowInfo
                .count_info(new CountInfo()
                        .setCount(5L)
                        .setTimeToLive(now.plusSeconds(10L).toString())
                        .setStartCountTime(now.toString())));
        Mockito.when(wbListServiceSrv.getRowInfo(any())).thenReturn(result);
        Mockito.when(eventRepository.countOperationByField(any(), any(), any(), any())).thenReturn(4);
        Boolean inList = inGreyListFinder.findInList(PARTY_ID, SHOP_ID, List.of(CheckedField.CARD_TOKEN), List.of(VALUE));

        Assert.assertTrue(inList);
    }
}