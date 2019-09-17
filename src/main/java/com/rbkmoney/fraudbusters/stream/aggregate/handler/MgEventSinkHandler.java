package com.rbkmoney.fraudbusters.stream.aggregate.handler;

import com.rbkmoney.damsel.payment_processing.EventPayload;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.fraudbusters.domain.MgEventSinkRow;
import com.rbkmoney.fraudbusters.listener.event.sink.SourceEventParser;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MgEventSinkHandler {

    private final SourceEventParser eventParser;
    private final List<InvoiceChangeHandler> invoiceChangeHandlers;

    public MgEventSinkRow handle(SinkEvent sinkEvent) {
        MgEventSinkRow mgEventSinkRow = new MgEventSinkRow();
        EventPayload eventPayload = eventParser.parseEvent(sinkEvent);
        if (eventPayload.isSetInvoiceChanges()) {
            for (InvoiceChange change : eventPayload.getInvoiceChanges()) {
                invoiceChangeHandlers.stream()
                        .filter(invoiceChangeHandler -> invoiceChangeHandler.filter(change))
                        .findFirst()
                        .ifPresent(invoiceChangeHandler -> invoiceChangeHandler.handle(mgEventSinkRow, change));
            }
        }
        mgEventSinkRow.setInvoiceId(sinkEvent.getEvent().getSourceId());
        return mgEventSinkRow;
    }

}
