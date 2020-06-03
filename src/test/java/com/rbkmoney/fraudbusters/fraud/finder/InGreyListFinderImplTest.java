package com.rbkmoney.fraudbusters.fraud.finder;

import com.rbkmoney.damsel.wb_list.CountInfo;
import com.rbkmoney.damsel.wb_list.Result;
import com.rbkmoney.damsel.wb_list.RowInfo;
import com.rbkmoney.damsel.wb_list.WbListServiceSrv;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.fraud.payment.finder.PaymentInListFinderImpl;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.DBPaymentFieldResolver;
import com.rbkmoney.fraudbusters.repository.impl.PaymentRepositoryImpl;
import com.rbkmoney.fraudo.finder.InListFinder;
import com.rbkmoney.fraudo.model.Pair;
import org.apache.thrift.TException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class InGreyListFinderImplTest {

    private static final String PARTY_ID = "partyId";
    private static final String SHOP_ID = "shopId";
    private static final String VALUE = "1234123";

    private InListFinder<PaymentModel, PaymentCheckedField> inGreyListFinder;

    @Mock
    private WbListServiceSrv.Iface wbListServiceSrv;
    @Mock
    private PaymentRepositoryImpl analyticRepository;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        inGreyListFinder = new PaymentInListFinderImpl(wbListServiceSrv, new DBPaymentFieldResolver(), analyticRepository);
    }

    @Test
    public void findInList() throws TException {
        when(wbListServiceSrv.getRowInfo(any())).thenReturn(new Result().setRowInfo(new RowInfo()));

        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setPartyId(PARTY_ID);
        paymentModel.setShopId(SHOP_ID);
        Boolean inList = inGreyListFinder.findInGreyList(List.of(new Pair<>(PaymentCheckedField.CARD_TOKEN, VALUE)), paymentModel);

        assertFalse(inList);

        Instant now = Instant.now();
        Result result = new Result().setRowInfo(RowInfo
                .count_info(new CountInfo()
                        .setCount(5L)
                        .setTimeToLive(now.plusSeconds(10L).toString())
                        .setStartCountTime(now.toString())));

        when(wbListServiceSrv.getRowInfo(any())).thenReturn(result);
        when(analyticRepository.countOperationByField(any(), any(), any(), any())).thenReturn(6);

        inList = inGreyListFinder.findInGreyList(List.of(new Pair<>(PaymentCheckedField.CARD_TOKEN, VALUE)), paymentModel);
        assertFalse(inList);

        when(analyticRepository.countOperationByField(any(), any(), any(), any())).thenReturn(4);
        inList = inGreyListFinder.findInGreyList(List.of(new Pair<>(PaymentCheckedField.CARD_TOKEN, VALUE)), paymentModel);
        assertTrue(inList);
    }

    @Test
    public void testFindInList() throws TException {
        Instant now = Instant.now();
        Result result = new Result().setRowInfo(RowInfo
                .count_info(new CountInfo()
                        .setCount(5L)
                        .setTimeToLive(now.plusSeconds(10L).toString())
                        .setStartCountTime(now.toString())));
        when(wbListServiceSrv.getRowInfo(any())).thenReturn(result);
        when(analyticRepository.countOperationByField(any(), any(), any(), any())).thenReturn(4);
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setPartyId(PARTY_ID);
        paymentModel.setShopId(SHOP_ID);
        Boolean inList = inGreyListFinder.findInGreyList(List.of(new Pair<>(PaymentCheckedField.CARD_TOKEN, VALUE)), paymentModel);

        assertTrue(inList);
    }
}