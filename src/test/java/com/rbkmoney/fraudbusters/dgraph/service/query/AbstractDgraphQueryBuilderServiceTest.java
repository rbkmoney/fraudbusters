package com.rbkmoney.fraudbusters.dgraph.service.query;

import com.rbkmoney.fraudbusters.config.dgraph.TemplateConfig;
import com.rbkmoney.fraudbusters.fraud.payment.aggregator.dgraph.DgraphAggregationQueryBuilderServiceImpl;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.DgraphEntityResolver;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.DgraphQueryConditionResolver;
import com.rbkmoney.fraudbusters.service.TemplateService;
import com.rbkmoney.fraudbusters.service.TemplateServiceImpl;

public abstract class AbstractDgraphQueryBuilderServiceTest {

    private TemplateService templateService = new TemplateServiceImpl(new TemplateConfig().velocityEngine());

    DgraphAggregationQueryBuilderServiceImpl aggregationQueryBuilderService =
            new DgraphAggregationQueryBuilderServiceImpl(
                    new DgraphEntityResolver(),
                    new DgraphQueryConditionResolver(),
                    templateService
            );

}
