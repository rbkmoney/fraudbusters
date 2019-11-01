package com.rbkmoney.fraudbusters.fraud.finder;

import com.rbkmoney.damsel.wb_list.CountInfo;
import com.rbkmoney.damsel.wb_list.Result;
import com.rbkmoney.damsel.wb_list.RowInfo;
import com.rbkmoney.damsel.wb_list.WbListServiceSrv;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.fraud.resolver.DBPaymentFieldResolver;
import com.rbkmoney.fraudbusters.repository.EventRepository;
import com.rbkmoney.fraudo.finder.InListFinder;
import com.rbkmoney.fraudo.model.Pair;
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

    private static final String PARTY_ID = "partyId";
    private static final String SHOP_ID = "shopId";
    private static final String VALUE = "1234123";

    private InListFinder<PaymentModel, PaymentCheckedField> inGreyListFinder;

    @Mock
    private WbListServiceSrv.Iface wbListServiceSrv;
    @Mock
    private EventRepository eventRepository;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        inGreyListFinder = new PaymentInListFinderImpl(wbListServiceSrv, new DBPaymentFieldResolver(), eventRepository);
    }

    @Test
    public void findInList() throws TException {
        Mockito.when(wbListServiceSrv.getRowInfo(any())).thenReturn(new Result().setRowInfo(new RowInfo()));

        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setPartyId(PARTY_ID);
        paymentModel.setShopId(SHOP_ID);
        Boolean inList = inGreyListFinder.findInGreyList(List.of(new Pair<>(PaymentCheckedField.CARD_TOKEN, VALUE)), paymentModel);

        Assert.assertFalse(inList);

        Instant now = Instant.now();
        Result result = new Result().setRowInfo(RowInfo
                .count_info(new CountInfo()
                        .setCount(5L)
                        .setTimeToLive(now.plusSeconds(10L).toString())
                        .setStartCountTime(now.toString())));

        Mockito.when(wbListServiceSrv.getRowInfo(any())).thenReturn(result);
        Mockito.when(eventRepository.countOperationByField(any(), any(), any(), any())).thenReturn(6);

        inList = inGreyListFinder.findInGreyList(List.of(new Pair<>(PaymentCheckedField.CARD_TOKEN, VALUE)), paymentModel);
        Assert.assertFalse(inList);

        Mockito.when(eventRepository.countOperationByField(any(), any(), any(), any())).thenReturn(4);
        inList = inGreyListFinder.findInGreyList(List.of(new Pair<>(PaymentCheckedField.CARD_TOKEN, VALUE)), paymentModel);
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
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setPartyId(PARTY_ID);
        paymentModel.setShopId(SHOP_ID);
        Boolean inList = inGreyListFinder.findInGreyList(List.of(new Pair<>(PaymentCheckedField.CARD_TOKEN, VALUE)), paymentModel);

        Assert.assertTrue(inList);
    }
}