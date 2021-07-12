package com.rbkmoney.fraudbusters.resource;

import com.rbkmoney.damsel.fraudbusters.Filter;
import com.rbkmoney.damsel.fraudbusters.HistoricalDataServiceSrv;
import com.rbkmoney.damsel.fraudbusters.Page;
import com.rbkmoney.damsel.fraudbusters.PaymentInfoResult;
import org.springframework.stereotype.Service;

@Service
public class HistoricalDataHandler implements HistoricalDataServiceSrv.Iface {

    @Override
    public PaymentInfoResult getPayments(Filter filter, Page page) {
        return null;
    }
}
