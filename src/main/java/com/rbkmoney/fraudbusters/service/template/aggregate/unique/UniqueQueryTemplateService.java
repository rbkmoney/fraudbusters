package com.rbkmoney.fraudbusters.service.template.aggregate.unique;

import com.rbkmoney.fraudbusters.fraud.model.DgraphAggregationQueryModel;
import com.rbkmoney.fraudbusters.service.template.AbstractDgraphTemplateService;
import com.rbkmoney.fraudbusters.service.template.TemplateService;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.stereotype.Service;

@Service
public class UniqueQueryTemplateService
        extends AbstractDgraphTemplateService implements TemplateService<DgraphAggregationQueryModel> {

    private static final String VELOCITY_VARIABLE_NAME = "queryModel";
    private static final String VELOCITY_TEMPLATE = "vm/aggregate/unique/prepare_unique_query.vm";

    public UniqueQueryTemplateService(VelocityEngine velocityEngine) {
        super(velocityEngine);
    }

    @Override
    public String build(DgraphAggregationQueryModel dgraphAggregationQueryModel) {
        return buildDgraphTemplate(VELOCITY_TEMPLATE, VELOCITY_VARIABLE_NAME, dgraphAggregationQueryModel);
    }
}
