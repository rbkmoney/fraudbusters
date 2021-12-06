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
public class DgraphUniqueQueryBuilderService
        extends AbstractDgraphAggregationQueryBuilderService implements DgraphAggregationQueryBuilderService {

    private final TemplateService<DgraphAggregationQueryModel> uniqueQueryTemplateService;
    private final TemplateService<DgraphAggregationQueryModel> rootUniqueQueryTemplateService;
    private final TemplateService<DgraphAggregationQueryModel> equalFieldsUniqueQueryTemplateService;

    public DgraphUniqueQueryBuilderService(
            DgraphEntityResolver dgraphEntityResolver,
            DgraphQueryConditionResolver dgraphQueryConditionResolver,
            TemplateService<DgraphAggregationQueryModel> uniqueQueryTemplateService,
            TemplateService<DgraphAggregationQueryModel> rootUniqueQueryTemplateService,
            TemplateService<DgraphAggregationQueryModel> equalFieldsUniqueQueryTemplateService
    ) {
        super(dgraphEntityResolver, dgraphQueryConditionResolver);
        this.uniqueQueryTemplateService = uniqueQueryTemplateService;
        this.rootUniqueQueryTemplateService = rootUniqueQueryTemplateService;
        this.equalFieldsUniqueQueryTemplateService = equalFieldsUniqueQueryTemplateService;
    }

    @Override
    public String getQuery(DgraphEntity rootEntity,
                           DgraphEntity onField,
                           Map<DgraphEntity, Set<PaymentCheckedField>> dgraphEntityMap,
                           PaymentModel paymentModel,
                           Instant startWindowTime,
                           Instant endWindowTime,
                           String status) {
        DgraphAggregationQueryModel queryModel = prepareAggregationQueryModel(
                onField,
                DgraphEntity.PAYMENT,
                dgraphEntityMap,
                paymentModel,
                startWindowTime,
                endWindowTime,
                status
        );
        if (rootEntity == onField) {
            return equalFieldsUniqueQueryTemplateService.build(queryModel);
        }
        return queryModel.isRootModel()
                ? rootUniqueQueryTemplateService.build(queryModel)
                : uniqueQueryTemplateService.build(queryModel);
    }
}
