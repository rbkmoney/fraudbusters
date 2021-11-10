package com.rbkmoney.fraudbusters.fraud.payment.aggregator.dgraph;

import com.rbkmoney.fraudbusters.fraud.constant.DgraphEntity;
import com.rbkmoney.fraudbusters.fraud.constant.DgraphTargetAggregationType;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.DgraphAggregationQueryModel;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.DgraphEntityResolver;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.DgraphQueryConditionResolver;
import com.rbkmoney.fraudbusters.service.TemplateService;
import com.rbkmoney.fraudo.model.TimeWindow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DgraphAggregationQueryBuilderService {

    private final DgraphEntityResolver dgraphEntityResolver;

    private final DgraphQueryConditionResolver dgraphQueryConditionResolver;

    private final TemplateService templateService;

    private static final String DGRAPH_FILTER_PATTERN = "@filter(%s)";
    private static final String DGRAPH_FASET_PATTERN = "@facets(%s)";

    public String getCountQuery(DgraphEntity rootEntity,
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
                ? templateService.buildRootCountQuery(queryModel)
                : templateService.buildCountQuery(queryModel);
    }

    public String getSumQuery(DgraphEntity rootEntity,
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
                ? templateService.buildRootSumQuery(queryModel)
                : templateService.buildSumQuery(queryModel);
    }

    public String getUniqueQuery(DgraphEntity rootEntity,
                                 DgraphEntity onField,
                                 Map<DgraphEntity, Set<PaymentCheckedField>> dgraphEntityMap,
                                 PaymentModel paymentModel,
                                 Instant startWindowTime,
                                 Instant endWindowTime,
                                 String status) {
        DgraphAggregationQueryModel queryModel = prepareAggregationQueryModel(
                rootEntity,
                DgraphEntity.PAYMENT,
                dgraphEntityMap,
                paymentModel,
                startWindowTime,
                endWindowTime,
                status
        );
        queryModel.setInnerTypesFilters(
                createInnerConditions(rootEntity, DgraphEntity.PAYMENT, dgraphEntityMap, paymentModel, onField)
        );
        return queryModel.isRootModel()
                ? templateService.buildRootSumQuery(queryModel)
                : templateService.buildSumQuery(queryModel);
    }

    public DgraphAggregationQueryModel prepareAggregationQueryModel(
            DgraphEntity rootEntity,
            DgraphEntity targetEntity,
            Map<DgraphEntity, Set<PaymentCheckedField>> dgraphEntityMap,
            PaymentModel paymentModel,
            Instant startWindowTime,
            Instant endWindowTime,
            String status
    ) {
        DgraphTargetAggregationType targetType = dgraphEntityResolver.resolveDgraphTargetAggregationType(targetEntity);

        Set<String> innerConditions = createInnerConditions(rootEntity, targetEntity, dgraphEntityMap, paymentModel);
        String rootCondition = createRootCondition(rootEntity, dgraphEntityMap, paymentModel);
        String targetFasetCondition = createTargetFacetCondition(startWindowTime, endWindowTime, status);

        if (dgraphEntityResolver.resolveDgraphEntityByTargetAggregationType(targetType) == rootEntity) {
            String extendedRootCondition = Strings.isEmpty(rootCondition)
                    ? targetFasetCondition : String.format("%s and %s", targetFasetCondition, rootCondition);
            return DgraphAggregationQueryModel.builder()
                    .rootType(rootEntity.getTypeName())
                    .rootFilter(String.format(DGRAPH_FILTER_PATTERN, extendedRootCondition))
                    .innerTypesFilters(innerConditions)
                    .isRootModel(true)
                    .build();
        } else {
            return DgraphAggregationQueryModel.builder()
                    .rootType(rootEntity.getTypeName())
                    .rootFilter(Strings.isEmpty(rootCondition)
                            ? Strings.EMPTY : String.format(DGRAPH_FILTER_PATTERN, rootCondition))
                    .targetType(targetType.getFieldName())
                    .targetFaset(String.format(DGRAPH_FASET_PATTERN, targetFasetCondition))
                    .targetFilter(createTargetFilterCondition(targetType, dgraphEntityMap, paymentModel))
                    .innerTypesFilters(innerConditions)
                    .build();
        }
    }

    private Set<String> createInnerConditions(DgraphEntity rootDgraphEntity,
                                              DgraphEntity targetEntity,
                                              Map<DgraphEntity, Set<PaymentCheckedField>> dgraphEntityMap,
                                              PaymentModel paymentModel) {
        return createInnerConditions(rootDgraphEntity, targetEntity, dgraphEntityMap, paymentModel, null);
    }

    private Set<String> createInnerConditions(DgraphEntity rootDgraphEntity,
                                              DgraphEntity targetEntity,
                                              Map<DgraphEntity, Set<PaymentCheckedField>> dgraphEntityMap,
                                              PaymentModel paymentModel,
                                              DgraphEntity onField) {
        Set<String> innerFilters = new TreeSet<>();
        for (DgraphEntity dgraphEntity : dgraphEntityMap.keySet()) {
            if (dgraphEntity == rootDgraphEntity || dgraphEntity == targetEntity) {
                continue;
            }
            Set<PaymentCheckedField> paymentCheckedFields = dgraphEntityMap.get(dgraphEntity);
            if (paymentCheckedFields == null || paymentCheckedFields.isEmpty()) {
                log.warn("PaymentCheckedField set for {} is empty!", rootDgraphEntity);
                continue;
            }

            String condition = paymentCheckedFields.stream()
                    .sorted()
                    .map(checkedField -> dgraphQueryConditionResolver.resolveConditionByPaymentCheckedField(checkedField, paymentModel))
                    .collect(Collectors.joining(" and "));
            String filter = String.format(dgraphQueryConditionResolver.resolvePaymentFilterByDgraphEntity(dgraphEntity), condition);
            if (onField == dgraphEntity) {
                filter += " { objectUid as uid }";
            }
            innerFilters.add(filter);
        }
        return innerFilters;
    }

    private String createRootCondition(DgraphEntity rootDgraphEntity,
                                       Map<DgraphEntity, Set<PaymentCheckedField>> dgraphEntityMap,
                                       PaymentModel paymentModel) {
        Set<PaymentCheckedField> paymentCheckedFields = dgraphEntityMap.get(rootDgraphEntity);
        return paymentCheckedFields == null || paymentCheckedFields.isEmpty()
                ? Strings.EMPTY : paymentCheckedFields.stream()
                .sorted()
                .map(checkedField -> dgraphQueryConditionResolver.resolveConditionByPaymentCheckedField(checkedField, paymentModel))
                .collect(Collectors.joining(" and "));
    }

    private String createTargetFilterCondition(DgraphTargetAggregationType type,
                                               Map<DgraphEntity, Set<PaymentCheckedField>> dgraphEntityMap,
                                               PaymentModel paymentModel) {
        DgraphEntity dgraphEntity = dgraphEntityResolver.resolveDgraphEntityByTargetAggregationType(type);
        if (dgraphEntityMap == null || !dgraphEntityMap.containsKey(dgraphEntity)) {
            return Strings.EMPTY;
        }

        String targetCondition = dgraphEntityMap.get(dgraphEntity).stream()
                .sorted()
                .map(field -> dgraphQueryConditionResolver.resolveConditionByPaymentCheckedField(field, paymentModel))
                .collect(Collectors.joining(" and "));
        return Strings.isEmpty(targetCondition) ? Strings.EMPTY : String.format(DGRAPH_FILTER_PATTERN, targetCondition);
    }

    private String createTargetFacetCondition(Instant fromTime, Instant toTime, String status) {
        StringBuilder basicFacet = new StringBuilder();
        basicFacet.append(String.format("ge(createdAt, \"%s\") and le(createdAt, \"%s\")", fromTime, toTime));
        if (Strings.isNotEmpty(status)) {
            basicFacet.append(String.format(" and eq(status, \"%s\")", status));
        }
        return basicFacet.toString();
    }

}
