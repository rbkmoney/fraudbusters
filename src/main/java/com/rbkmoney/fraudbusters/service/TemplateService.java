package com.rbkmoney.fraudbusters.service;

import com.rbkmoney.fraudbusters.domain.dgraph.DgraphPayment;

public interface TemplateService {

    String buildPaymentNqs(DgraphPayment payment);

}
