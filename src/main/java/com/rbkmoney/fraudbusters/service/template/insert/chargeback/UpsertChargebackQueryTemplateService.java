package com.rbkmoney.fraudbusters.service.template.insert.chargeback;

import com.rbkmoney.fraudbusters.domain.dgraph.DgraphChargeback;
import com.rbkmoney.fraudbusters.service.template.AbstractDgraphTemplateService;
import com.rbkmoney.fraudbusters.service.template.TemplateService;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.stereotype.Service;

@Service
public class UpsertChargebackQueryTemplateService
        extends AbstractDgraphTemplateService implements TemplateService<DgraphChargeback> {

    private static final String VELOCITY_VARIABLE_NAME = "chargeback";
    private static final String VELOCITY_TEMPLATE = "vm/insert/chargeback/upsert_chargeback_data_query.vm";

    public UpsertChargebackQueryTemplateService(VelocityEngine velocityEngine) {
        super(velocityEngine);
    }

    @Override
    public String build(DgraphChargeback dgraphChargeback) {
        return buildDgraphTemplate(VELOCITY_TEMPLATE, VELOCITY_VARIABLE_NAME, dgraphChargeback);
    }
}
