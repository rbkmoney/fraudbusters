package com.rbkmoney.fraudbusters.stream.aggregate.handler;

import com.rbkmoney.damsel.domain.Failure;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChangePayload;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentStatusChanged;
import com.rbkmoney.fraudbusters.constant.ResultStatus;
import com.rbkmoney.fraudbusters.domain.MgEventSinkRow;
import org.springframework.stereotype.Component;

@Component
public class InvoicePaymentStatusChangedHandlerImpl implements InvoiceChangeHandler {

    public static final String OPERATION_TIMEOUT = "operation_timeout";

    @Override
    public boolean filter(InvoiceChange invoiceChange) {
        return invoiceChange.isSetInvoicePaymentChange()
                && invoiceChange.getInvoicePaymentChange().getPayload().isSetInvoicePaymentStatusChanged();
    }

    @Override
    public MgEventSinkRow handle(MgEventSinkRow mgEventSinkRow, InvoiceChange invoiceChange) {
        InvoicePaymentChange invoicePaymentChange = invoiceChange.getInvoicePaymentChange();
        mgEventSinkRow.setPaymentId(invoicePaymentChange.getId());
        InvoicePaymentChangePayload payload = invoicePaymentChange.getPayload();
        InvoicePaymentStatusChanged invoicePaymentStatusChanged = payload.getInvoicePaymentStatusChanged();
        if (invoicePaymentStatusChanged.getStatus().isSetCaptured()) {
            mgEventSinkRow.setResultStatus(ResultStatus.CAPTURED.name());
        } else if (invoicePaymentStatusChanged.getStatus().isSetFailed()) {
            mgEventSinkRow.setResultStatus(ResultStatus.FAILED.name());
            if (invoicePaymentStatusChanged.getStatus().getFailed().getFailure().isSetFailure()) {
                Failure failure = invoicePaymentStatusChanged.getStatus().getFailed().getFailure().getFailure();
                mgEventSinkRow.setErrorMessage(failure.getReason());
                mgEventSinkRow.setErrorCode(failure.getCode());
            } else if (invoicePaymentStatusChanged.getStatus().getFailed().getFailure().isSetOperationTimeout()) {
                mgEventSinkRow.setErrorCode(OPERATION_TIMEOUT);
            }
        } else if (invoicePaymentStatusChanged.getStatus().isSetCancelled()) {
            mgEventSinkRow.setResultStatus(ResultStatus.CANCELLED.name());
        } else if (invoicePaymentStatusChanged.getStatus().isSetRefunded()) {
            mgEventSinkRow.setResultStatus(ResultStatus.REFUNDED.name());
        }
        return mgEventSinkRow;
    }
}
