package com.rbkmoney.fraudbusters.fraud.payment.aggregator.dgraph.query.builder;

import com.rbkmoney.fraudbusters.fraud.constant.DgraphEntity;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.DgraphAggregationQueryModel;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.DgraphEntityResolver;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.DgraphQueryConditionResolver;
import com.rbkmoney.fraudbusters.service.template.TemplateService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

@Service
public class DgraphCountQueryBuilderService
        extends AbstractDgraphAggregationQueryBuilderService implements DgraphAggregationQueryBuilderService {

    private final TemplateService<DgraphAggregationQueryModel> countQueryTemplateService;
    private final TemplateService<DgraphAggregationQueryModel> rootCountQueryTemplateService;

    public DgraphCountQueryBuilderService(DgraphEntityResolver dgraphEntityResolver,
                                          DgraphQueryConditionResolver dgraphQueryConditionResolver,
                                          TemplateService<DgraphAggregationQueryModel> countQueryTemplateService,
                                          TemplateService<DgraphAggregationQueryModel> rootCountQueryTemplateService) {
        super(dgraphEntityResolver, dgraphQueryConditionResolver);
        this.countQueryTemplateService = countQueryTemplateService;
        this.rootCountQueryTemplateService = rootCountQueryTemplateService;
    }

    @Override
    public String getQuery(DgraphEntity rootEntity,
                           DgraphEntity targetEntity,
                           Map<DgraphEntity, Set<PaymentCheckedField>> dgraphEntityMap,
                           PaymentModel paymentModel,
                           Instant startWindowTime,
                           Instant endWindowTime,
                           String status) {
        DgraphAggregationQueryModel queryModel = prepareAggregationQueryModel(
                rootEntity,
                targetEntity,
                dgraphEntityMap,
                paymentModel,
                startWindowTime,
                endWindowTime,
                status
        );
        return queryModel.isRootModel()
                ? rootCountQueryTemplateService.build(queryModel)
                : countQueryTemplateService.build(queryModel);
    }

}
