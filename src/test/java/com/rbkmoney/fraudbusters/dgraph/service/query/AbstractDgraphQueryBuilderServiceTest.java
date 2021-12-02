package com.rbkmoney.fraudbusters.dgraph.service.query;

import com.rbkmoney.fraudbusters.config.dgraph.TemplateConfig;
import com.rbkmoney.fraudbusters.fraud.payment.aggregator.dgraph.query.builder.DgraphCountQueryBuilderService;
import com.rbkmoney.fraudbusters.fraud.payment.aggregator.dgraph.query.builder.DgraphSumQueryBuilderService;
import com.rbkmoney.fraudbusters.fraud.payment.aggregator.dgraph.query.builder.DgraphUniqueQueryBuilderService;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.DgraphEntityResolver;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.DgraphQueryConditionResolver;
import com.rbkmoney.fraudbusters.service.template.aggregate.count.CountQueryTemplateService;
import com.rbkmoney.fraudbusters.service.template.aggregate.count.RootCountQueryTemplateService;
import com.rbkmoney.fraudbusters.service.template.aggregate.sum.RootSumQueryTemplateService;
import com.rbkmoney.fraudbusters.service.template.aggregate.sum.SumQueryTemplateService;
import com.rbkmoney.fraudbusters.service.template.aggregate.unique.EqualFieldsUniqueQueryTemplateService;
import com.rbkmoney.fraudbusters.service.template.aggregate.unique.RootUniqueQueryTemplateService;
import com.rbkmoney.fraudbusters.service.template.aggregate.unique.UniqueQueryTemplateService;
import org.apache.velocity.app.VelocityEngine;

public abstract class AbstractDgraphQueryBuilderServiceTest {

    private VelocityEngine velocityEngine = new TemplateConfig().velocityEngine();
    private DgraphEntityResolver dgraphEntityResolver = new DgraphEntityResolver();
    private DgraphQueryConditionResolver dgraphQueryConditionResolver = new DgraphQueryConditionResolver();

    DgraphCountQueryBuilderService countQueryBuilderService = new DgraphCountQueryBuilderService(
            dgraphEntityResolver,
            dgraphQueryConditionResolver,
            new CountQueryTemplateService(velocityEngine),
            new RootCountQueryTemplateService(velocityEngine)
    );

    DgraphSumQueryBuilderService sumQueryBuilderService = new DgraphSumQueryBuilderService(
            dgraphEntityResolver,
            dgraphQueryConditionResolver,
            new SumQueryTemplateService(velocityEngine),
            new RootSumQueryTemplateService(velocityEngine)
    );

    DgraphUniqueQueryBuilderService uniqueQueryBuilderService = new DgraphUniqueQueryBuilderService(
            dgraphEntityResolver,
            dgraphQueryConditionResolver,
            new UniqueQueryTemplateService(velocityEngine),
            new RootUniqueQueryTemplateService(velocityEngine),
            new EqualFieldsUniqueQueryTemplateService(velocityEngine)
    );

}
