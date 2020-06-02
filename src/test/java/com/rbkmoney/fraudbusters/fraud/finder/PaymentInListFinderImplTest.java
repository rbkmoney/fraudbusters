package com.rbkmoney.fraudbusters.fraud.finder;

import com.rbkmoney.damsel.wb_list.WbListServiceSrv;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.fraud.payment.finder.PaymentInListFinderImpl;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.DBPaymentFieldResolver;
import com.rbkmoney.fraudbusters.repository.impl.PaymentRepositoryImpl;
import com.rbkmoney.fraudo.finder.InListFinder;
import com.rbkmoney.fraudo.model.Pair;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;

public class PaymentInListFinderImplTest {

    public static final String PARTY_ID = "partyId";
    public static final String SHOP_ID = "shopId";
    public static final String VALUE = "value";
    private InListFinder<PaymentModel, PaymentCheckedField> listFinder;

    @Mock
    private WbListServiceSrv.Iface wbListServiceSrv;
    @Mock
    private DBPaymentFieldResolver dbPaymentFieldResolver;
    @Mock
    private PaymentRepositoryImpl analyticRepository;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        listFinder = new PaymentInListFinderImpl(wbListServiceSrv, dbPaymentFieldResolver, analyticRepository);
    }

    @Test
    public void findInList() throws TException {
        Mockito.when(wbListServiceSrv.isAnyExist(any())).thenReturn(true);
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setPartyId(PARTY_ID);
        paymentModel.setShopId(SHOP_ID);
        Boolean isInList = listFinder.findInBlackList(List.of(new Pair<>(PaymentCheckedField.IP, VALUE)), paymentModel);
        Assert.assertTrue(isInList);
    }

    @Test
    public void findInListEmpty() throws TException {
        Mockito.when(wbListServiceSrv.isAnyExist(any())).thenReturn(true);
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setPartyId(PARTY_ID);
        paymentModel.setShopId(SHOP_ID);
        Boolean isInList = listFinder.findInBlackList(List.of(new Pair<>(PaymentCheckedField.IP, null)), paymentModel);
        Assert.assertFalse(isInList);

        isInList = listFinder.findInBlackList(List.of(new Pair<>(PaymentCheckedField.IP, "")), paymentModel);
        Assert.assertFalse(isInList);
    }
}