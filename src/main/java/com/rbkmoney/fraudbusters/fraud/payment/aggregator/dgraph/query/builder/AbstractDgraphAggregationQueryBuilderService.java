package com.rbkmoney.fraudbusters.fraud.payment.aggregator.dgraph.query.builder;

import com.rbkmoney.fraudbusters.fraud.constant.DgraphEntity;
import com.rbkmoney.fraudbusters.fraud.constant.DgraphTargetAggregationType;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.DgraphAggregationQueryModel;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.DgraphEntityResolver;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.DgraphQueryConditionResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractDgraphAggregationQueryBuilderService {

    private final DgraphEntityResolver dgraphEntityResolver;
    private final DgraphQueryConditionResolver dgraphQueryConditionResolver;

    private static final String DGRAPH_FILTER_PATTERN = "@filter(%s)";
    private static final String DGRAPH_FASET_PATTERN = "@facets(%s)";
    private static final String DGRAPH_CONDITION_AND = " and ";

    protected DgraphAggregationQueryModel prepareAggregationQueryModel(
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
        String targetFacetCondition = createTargetFacetCondition(startWindowTime, endWindowTime, status);

        if (dgraphEntityResolver.resolveDgraphEntityByTargetAggregationType(targetType) == rootEntity) {
            String extendedRootCondition = Strings.isEmpty(rootCondition)
                    ? targetFacetCondition : String.format("%s and %s", targetFacetCondition, rootCondition);
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
                    .targetFaset(String.format(DGRAPH_FASET_PATTERN, targetFacetCondition))
                    .targetFilter(createTargetFilterCondition(targetType, dgraphEntityMap, paymentModel))
                    .innerTypesFilters(innerConditions)
                    .build();
        }
    }

    private Set<String> createInnerConditions(DgraphEntity rootDgraphEntity,
                                              DgraphEntity targetEntity,
                                              Map<DgraphEntity, Set<PaymentCheckedField>> dgraphEntityMap,
                                              PaymentModel paymentModel) {
        Set<String> innerFilters = new TreeSet<>();
        for (DgraphEntity dgraphEntity : dgraphEntityMap.keySet()) {
            createInnerFilter(rootDgraphEntity, targetEntity, dgraphEntity, dgraphEntityMap, paymentModel)
                    .ifPresent(filter -> innerFilters.add(filter));
        }
        return innerFilters;
    }

    private Optional<String> createInnerFilter(DgraphEntity rootDgraphEntity,
                                               DgraphEntity targetEntity,
                                               DgraphEntity dgraphEntity,
                                               Map<DgraphEntity, Set<PaymentCheckedField>> dgraphEntityMap,
                                               PaymentModel paymentModel) {
        if (dgraphEntity == rootDgraphEntity || dgraphEntity == targetEntity) {
            return Optional.empty();
        }
        Set<PaymentCheckedField> paymentCheckedFields = dgraphEntityMap.get(dgraphEntity);
        if (CollectionUtils.isEmpty(paymentCheckedFields)) {
            log.warn("PaymentCheckedField set for {} is empty!", rootDgraphEntity);
            return Optional.empty();
        }

        String condition = createConditionLine(paymentCheckedFields, paymentModel);
        String filter = String.format(
                dgraphQueryConditionResolver.resolvePaymentFilterByDgraphEntity(dgraphEntity), condition);
        return Optional.of(filter);
    }

    private String createRootCondition(DgraphEntity rootDgraphEntity,
                                       Map<DgraphEntity, Set<PaymentCheckedField>> dgraphEntityMap,
                                       PaymentModel paymentModel) {
        Set<PaymentCheckedField> paymentCheckedFields = dgraphEntityMap.get(rootDgraphEntity);
        return CollectionUtils.isEmpty(paymentCheckedFields)
                ? Strings.EMPTY : createConditionLine(paymentCheckedFields, paymentModel);
    }

    private String createTargetFilterCondition(DgraphTargetAggregationType type,
                                               Map<DgraphEntity, Set<PaymentCheckedField>> dgraphEntityMap,
                                               PaymentModel paymentModel) {
        DgraphEntity dgraphEntity = dgraphEntityResolver.resolveDgraphEntityByTargetAggregationType(type);
        if (CollectionUtils.isEmpty(dgraphEntityMap) || !dgraphEntityMap.containsKey(dgraphEntity)) {
            return Strings.EMPTY;
        }

        String targetCondition = createConditionLine(dgraphEntityMap.get(dgraphEntity), paymentModel);
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

    private String createConditionLine(Set<PaymentCheckedField> paymentCheckedFields, PaymentModel paymentModel) {
        return paymentCheckedFields.stream()
                .map(field ->
                        dgraphQueryConditionResolver.resolveConditionByPaymentCheckedField(field, paymentModel))
                .collect(Collectors.joining(DGRAPH_CONDITION_AND));
    }

}
