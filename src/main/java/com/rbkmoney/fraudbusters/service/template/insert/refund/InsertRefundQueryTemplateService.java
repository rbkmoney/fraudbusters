package com.rbkmoney.fraudbusters.service.template.insert.refund;

import com.rbkmoney.fraudbusters.domain.dgraph.common.DgraphRefund;
import com.rbkmoney.fraudbusters.service.template.AbstractDgraphTemplateService;
import com.rbkmoney.fraudbusters.service.template.TemplateService;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.stereotype.Service;

@Service
public class InsertRefundQueryTemplateService
        extends AbstractDgraphTemplateService implements TemplateService<DgraphRefund> {

    private static final String VELOCITY_VARIABLE_NAME = "refund";
    private static final String VELOCITY_TEMPLATE = "vm/insert/refund/insert_refund_to_dgraph.vm";

    public InsertRefundQueryTemplateService(VelocityEngine velocityEngine) {
        super(velocityEngine);
    }

    @Override
    public String build(DgraphRefund dgraphRefund) {
        return buildDgraphTemplate(VELOCITY_TEMPLATE, VELOCITY_VARIABLE_NAME, dgraphRefund);
    }
}
