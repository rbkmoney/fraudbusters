package com.rbkmoney.fraudbusters.stream.aggregate.handler;

import com.rbkmoney.damsel.domain.Cash;
import com.rbkmoney.damsel.domain.Invoice;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoiceCreated;
import com.rbkmoney.fraudbusters.domain.MgEventSinkRow;
import org.springframework.stereotype.Component;

@Component
public class InvoiceCreateHandlerImpl implements InvoiceChangeHandler {

    @Override
    public boolean filter(InvoiceChange invoiceChange) {
        return invoiceChange.isSetInvoiceCreated();
    }

    @Override
    public MgEventSinkRow handle(MgEventSinkRow mgEventSinkRow, InvoiceChange invoiceChange) {
        InvoiceCreated invoiceCreated = invoiceChange.getInvoiceCreated();
        Invoice invoice = invoiceCreated.getInvoice();
        mgEventSinkRow.setInvoiceId(invoice.getId());
        mgEventSinkRow.setShopId(invoice.getShopId());
        Cash cost = invoice.getCost();
        mgEventSinkRow.setAmount(cost.getAmount());
        mgEventSinkRow.setCurrency(cost.getCurrency().getSymbolicCode());
        mgEventSinkRow.setPartyId((invoice.getOwnerId()));
        return mgEventSinkRow;
    }
}
