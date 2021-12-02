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
public class DgraphSumQueryBuilderService
        extends AbstractDgraphAggregationQueryBuilderService implements DgraphAggregationQueryBuilderService {

    private final TemplateService<DgraphAggregationQueryModel> sumQueryTemplateService;
    private final TemplateService<DgraphAggregationQueryModel> rootSumQueryTemplateService;

    public DgraphSumQueryBuilderService(DgraphEntityResolver dgraphEntityResolver,
                                        DgraphQueryConditionResolver dgraphQueryConditionResolver,
                                        TemplateService<DgraphAggregationQueryModel> sumQueryTemplateService,
                                        TemplateService<DgraphAggregationQueryModel> rootSumQueryTemplateService) {
        super(dgraphEntityResolver, dgraphQueryConditionResolver);
        this.sumQueryTemplateService = sumQueryTemplateService;
        this.rootSumQueryTemplateService = rootSumQueryTemplateService;
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
                ? rootSumQueryTemplateService.build(queryModel)
                : sumQueryTemplateService.build(queryModel);
    }
}
