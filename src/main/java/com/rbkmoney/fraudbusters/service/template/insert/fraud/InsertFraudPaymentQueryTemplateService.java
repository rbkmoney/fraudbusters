package com.rbkmoney.fraudbusters.service.template.insert.fraud;

import com.rbkmoney.fraudbusters.domain.dgraph.common.DgraphFraudPayment;
import com.rbkmoney.fraudbusters.service.template.AbstractDgraphTemplateService;
import com.rbkmoney.fraudbusters.service.template.TemplateService;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.stereotype.Service;

@Service
public class InsertFraudPaymentQueryTemplateService
        extends AbstractDgraphTemplateService implements TemplateService<DgraphFraudPayment> {

    private static final String VELOCITY_VARIABLE_NAME = "payment";
    private static final String VELOCITY_TEMPLATE = "vm/insert/fraud_payment/insert_fraud_payment_to_dgraph.vm";

    public InsertFraudPaymentQueryTemplateService(VelocityEngine velocityEngine) {
        super(velocityEngine);
    }

    @Override
    public String build(DgraphFraudPayment dgraphFraudPayment) {
        return buildDgraphTemplate(VELOCITY_TEMPLATE, VELOCITY_VARIABLE_NAME, dgraphFraudPayment);
    }
}
