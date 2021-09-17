package com.rbkmoney.fraudbusters.fraud.finder;

import com.rbkmoney.damsel.wb_list.WbListServiceSrv;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.fraud.payment.finder.PaymentInListFinderImpl;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.DatabasePaymentFieldResolver;
import com.rbkmoney.fraudbusters.repository.PaymentRepository;
import com.rbkmoney.fraudo.finder.InListFinder;
import com.rbkmoney.fraudo.model.Pair;
import org.apache.thrift.TException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PaymentInListFinderImplTest {

    public static final String PARTY_ID = "partyId";
    public static final String SHOP_ID = "shopId";
    public static final String VALUE = "value";
    private InListFinder<PaymentModel, PaymentCheckedField> listFinder;

    @Mock
    private WbListServiceSrv.Iface wbListServiceSrv;
    @Mock
    private DatabasePaymentFieldResolver dbPaymentFieldResolver;
    @Mock
    private PaymentRepository paymentRepository;

    @BeforeEach
    public void init() {
        listFinder = new PaymentInListFinderImpl(wbListServiceSrv, dbPaymentFieldResolver, paymentRepository);
    }

    @Test
    public void findInList() throws TException {
        Mockito.when(wbListServiceSrv.isAnyExist(any())).thenReturn(true);
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setPartyId(PARTY_ID);
        paymentModel.setShopId(SHOP_ID);
        Boolean isInList = listFinder.findInBlackList(List.of(new Pair<>(PaymentCheckedField.IP, VALUE)), paymentModel);
        assertTrue(isInList);
    }

    @Test
    public void findInListEmpty() throws TException {
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setPartyId(PARTY_ID);
        paymentModel.setShopId(SHOP_ID);
        Boolean isInList = listFinder.findInBlackList(List.of(new Pair<>(PaymentCheckedField.IP, null)), paymentModel);
        assertFalse(isInList);

        isInList = listFinder.findInBlackList(List.of(new Pair<>(PaymentCheckedField.IP, "")), paymentModel);
        assertFalse(isInList);
        verify(wbListServiceSrv, times(0)).isAnyExist(anyList());
    }
}
