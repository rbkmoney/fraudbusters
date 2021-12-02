package com.rbkmoney.fraudbusters.service.template.insert.fraud;

import com.rbkmoney.fraudbusters.domain.dgraph.DgraphFraudPayment;
import com.rbkmoney.fraudbusters.service.template.AbstractDgraphTemplateService;
import com.rbkmoney.fraudbusters.service.template.TemplateService;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.stereotype.Service;

@Service
public class UpsertFraudPaymentQueryTemplateService
        extends AbstractDgraphTemplateService implements TemplateService<DgraphFraudPayment> {

    private static final String VELOCITY_VARIABLE_NAME = "payment";
    private static final String VELOCITY_TEMPLATE = "vm/insert/fraud_payment/upsert_fraud_payment_data_query.vm";

    public UpsertFraudPaymentQueryTemplateService(VelocityEngine velocityEngine) {
        super(velocityEngine);
    }

    @Override
    public String build(DgraphFraudPayment dgraphFraudPayment) {
        return buildDgraphTemplate(VELOCITY_TEMPLATE, VELOCITY_VARIABLE_NAME, dgraphFraudPayment);

    }

}
