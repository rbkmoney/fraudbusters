package com.rbkmoney.fraudbusters.stream.aggregate.handler;

import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.fraudbusters.domain.MgEventSinkRow;
import com.rbkmoney.machinegun.eventsink.SinkEvent;

public interface InvoiceChangeHandler {

    boolean filter(InvoiceChange invoiceChange);

    MgEventSinkRow handle(MgEventSinkRow mgEventSinkRow, InvoiceChange invoiceChange);

}
