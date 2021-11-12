package com.rbkmoney.fraudbusters.dgraph.aggregates;

import com.rbkmoney.damsel.fraudbusters.Payment;
import com.rbkmoney.fraudbusters.dgraph.DgraphAbstractIntegrationTest;
import com.rbkmoney.fraudbusters.dgraph.insert.model.Aggregates;
import com.rbkmoney.fraudbusters.factory.properties.OperationProperties;
import com.rbkmoney.fraudbusters.fraud.constant.DgraphTargetAggregationType;
import com.rbkmoney.fraudbusters.fraud.constant.DgraphEntity;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.DgraphAggregationQueryModel;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.DgraphEntityResolver;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.DgraphQueryConditionResolver;
import com.rbkmoney.fraudbusters.serde.PaymentDeserializer;
import com.rbkmoney.fraudo.model.TimeWindow;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.logging.log4j.util.Strings;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.io.StringWriter;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.rbkmoney.fraudbusters.factory.TestDgraphObjectFactory.generatePayment;
import static com.rbkmoney.fraudbusters.util.DgraphTestAggregationUtils.createTestPaymentModel;
import static com.rbkmoney.fraudbusters.util.DgraphTestAggregationUtils.createTestTimeWindow;

@Slf4j
@ActiveProfiles("full-prod")
public class DgraphAggregationTest extends DgraphAbstractIntegrationTest {

    @Autowired
    private VelocityEngine velocityEngine;

    @Autowired
    private DgraphEntityResolver dgraphEntityResolver;

    @Autowired
    private DgraphQueryConditionResolver dgraphQueryConditionResolver;

    private static final String PREPARE_PAYMENTS_COUNT_QUERY = "vm/aggregate/count/prepare_count_query.vm";

    private static final String KAFKA_PAYMENT_TOPIC = "payment_event";

    @Test
    public void paymentCountAggregationTest() throws Exception {
        prepareGraphDb();

        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setCardToken("token1");
        paymentModel.setPartyId("party1");
        paymentModel.setShopId("party1shop2");
        paymentModel.setBin("bin2");

        var countSuccess = countSuccessPayments(
                PaymentCheckedField.CARD_TOKEN,
                paymentModel,
                createTestTimeWindow(),
                List.of(PaymentCheckedField.PARTY_ID, PaymentCheckedField.SHOP_ID, PaymentCheckedField.BIN));

        System.out.println("qwe  " + countSuccess);
    }

    public Integer countSuccessPayments(
            PaymentCheckedField checkedField,
            PaymentModel paymentModel,
            TimeWindow timeWindow,
            List<PaymentCheckedField> list) {
        Instant timestamp = paymentModel.getTimestamp() != null
                ? Instant.ofEpochMilli(paymentModel.getTimestamp())
                : Instant.now();
        Instant startWindowTime = timestamp.minusMillis(timeWindow.getStartWindowTime());
        Instant endWindowTime = timestamp.minusMillis(timeWindow.getEndWindowTime());

        DgraphEntity dgraphEntity = dgraphEntityResolver.resolvePaymentCheckedField(checkedField);
        //TODO: подумать нужно ли так делать
        Map<DgraphEntity, Set<PaymentCheckedField>> dgraphEntityMap =
                dgraphEntityResolver.resolvePaymentCheckedFieldsToMap(checkedField, list);
        return getPaymentsCount(
                dgraphEntity, dgraphEntityMap, paymentModel, startWindowTime, endWindowTime, "captured"
        );
    }

    private Integer getPaymentsCount(DgraphEntity rootEntity,
                                     Map<DgraphEntity, Set<PaymentCheckedField>> dgraphEntityMap,
                                     PaymentModel paymentModel,
                                     Instant startWindowTime,
                                     Instant endWindowTime,
                                     String status) {
        DgraphTargetAggregationType basicType = DgraphTargetAggregationType.PAYMENT;
        Set<String> innerConditions = createInnerConditions(rootEntity, dgraphEntityMap, paymentModel);
        String rootCondition = createRootCondition(rootEntity, dgraphEntityMap, paymentModel);

        DgraphAggregationQueryModel model = DgraphAggregationQueryModel.builder()
                .rootType(rootEntity.getTypeName())
                .rootFilter(Strings.isEmpty(rootCondition)
                        ? Strings.EMPTY : String.format("@filter(%s)", rootCondition))
                .targetType(basicType.getFieldName())
                .targetFaset(createTargetFacet(startWindowTime, endWindowTime, "captured"))
                .targetFilter(createTargetFilterCondition(
                        DgraphTargetAggregationType.PAYMENT,
                        dgraphEntityMap,
                        paymentModel)
                )
                .innerTypesFilters(innerConditions)
                .build();
        String getCountPaymentsQuery = buildTemplate(
                velocityEngine.getTemplate(PREPARE_PAYMENTS_COUNT_QUERY),
                createPreparePaymentsCountContext(model)
        );
        int count = getAggregates(getCountPaymentsQuery).getCount();
        return count;
    }

    private Set<String> createInnerConditions(DgraphEntity rootDgraphEntity,
                                               Map<DgraphEntity, Set<PaymentCheckedField>> dgraphEntityMap,
                                               PaymentModel paymentModel) {
        Set<String> innerConditions = new HashSet<>();
        for (DgraphEntity dgraphEntity : dgraphEntityMap.keySet()) {
            if (dgraphEntity == rootDgraphEntity) {
                continue;
            }
            Set<PaymentCheckedField> paymentCheckedFields = dgraphEntityMap.get(dgraphEntity);
            if (paymentCheckedFields == null || paymentCheckedFields.isEmpty()) {
                log.warn("PaymentCheckedField set for {} is empty!", rootDgraphEntity);
                continue;
            }
            String condition = paymentCheckedFields.stream()
                    .map(checkedField -> dgraphQueryConditionResolver.resolveConditionByPaymentCheckedField(
                            checkedField, paymentModel))
                    .collect(Collectors.joining(" and "));
            innerConditions.add(String.format(
                    dgraphQueryConditionResolver.resolvePaymentFilterByDgraphEntity(dgraphEntity), condition));
        }
        return innerConditions;
    }

    private String createRootCondition(DgraphEntity rootDgraphEntity,
                                       Map<DgraphEntity, Set<PaymentCheckedField>> dgraphEntityMap,
                                       PaymentModel paymentModel) {
        Set<PaymentCheckedField> paymentCheckedFields = dgraphEntityMap.get(rootDgraphEntity);
        return paymentCheckedFields == null || paymentCheckedFields.isEmpty()
                ? Strings.EMPTY : paymentCheckedFields.stream()
                .map(checkedField ->
                        dgraphQueryConditionResolver.resolveConditionByPaymentCheckedField(checkedField, paymentModel))
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
                .map(field -> dgraphQueryConditionResolver.resolveConditionByPaymentCheckedField(field, paymentModel))
                .collect(Collectors.joining(" and "));
        return Strings.isEmpty(targetCondition) ? Strings.EMPTY : String.format("@filter(%s)", targetCondition);
    }



    private String createTargetFacet(Instant fromTime, Instant toTime, String status) {
        StringBuilder basicFacet = new StringBuilder();
        basicFacet.append(String.format("ge(createdAt, \"%s\") and le(createdAt, \"%s\")", fromTime, toTime));
        if (Strings.isNotEmpty(status)) {
            basicFacet.append(String.format(" and eq(status, \"%s\")", status));
        }
        return String.format("@facets(%s)", basicFacet);
    }


    private void prepareGraphDb() throws Exception {
        PaymentModel paymentModel = createTestPaymentModel();
        OperationProperties operationProperties = OperationProperties.builder()
                .tokenId(paymentModel.getCardToken())
                .maskedPan(paymentModel.getPan())
                .email(paymentModel.getEmail())
                .fingerprint(paymentModel.getFingerprint())
                .partyId(paymentModel.getPartyId())
                .shopId(paymentModel.getShopId())
                .bin(paymentModel.getBin())
                .ip(paymentModel.getIp())
                .country(paymentModel.getBinCountryCode())
                .eventTimeDispersion(true)
                .build();
        producePayments(KAFKA_PAYMENT_TOPIC, generatePayments(5, operationProperties));
        waitingTopic(KAFKA_PAYMENT_TOPIC, PaymentDeserializer.class);

        operationProperties.setShopId(paymentModel.getShopId() + "-2");
        producePayments(KAFKA_PAYMENT_TOPIC, generatePayments(5, operationProperties));

        operationProperties.setBin(paymentModel.getBin() + "-2");
        operationProperties.setIp(paymentModel.getIp() + "-2");
        producePayments(KAFKA_PAYMENT_TOPIC, generatePayments(5, operationProperties));

        operationProperties.setTokenId(paymentModel.getCardToken() + "-2");
        operationProperties.setShopId(paymentModel.getShopId() + "-3");
        producePayments(KAFKA_PAYMENT_TOPIC, generatePayments(7, operationProperties));

        long currentTimeMillis = System.currentTimeMillis();
        while (getCountOfObjects("Payment") < 17
                || System.currentTimeMillis() - currentTimeMillis < 10_000L) {
        }
    }

    private List<Payment> generatePayments(int count, OperationProperties properties) {
        List<Payment> payments = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            payments.add(generatePayment(properties, i));
        }
        return payments;
    }

    void producePayments(String topicName, List<Payment> payments)
            throws InterruptedException, ExecutionException {
        try (Producer<String, Payment> producer = createProducer()) {
            for (Payment payment : payments) {
                ProducerRecord<String, Payment> producerRecord =
                        new ProducerRecord<>(topicName, payment.getId(), payment);
                producer.send(producerRecord).get();
            }
        }
    }

    public String buildTemplate(Template template, VelocityContext context) {
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }

    private VelocityContext createPreparePaymentsCountContext(DgraphAggregationQueryModel aggregationQueryModel) {
        VelocityContext context = new VelocityContext();
        context.put("queryModel", aggregationQueryModel);
        return context;
    }

    private Aggregates getCountOfPaymentsByToken2() {
        String query = String.format("""
                query all() {
                  aggregates(func: type(Token)) @filter(eq(tokenId, "token1")) @normalize {
                     payments @cascade {
                      count : count(uid)
                      partyShop @filter(eq(shopId, "party1shop2"))
                      bin @filter(eq(cardBin, "bin2"))
                    }
                  }
                }
                """);

        return getAggregates(query);
    }

}

//    query all() {
//        aggregates(func: type(Token)) @filter(eq(tokenId, "token1")) @normalize {
//            payments @filter(eq(mobile, false)) @cascade {
//                count : count(uid)
//                partyShop @filter(eq(shopId, "party1shop2"))
//                bin @filter(eq(cardBin, "bin2"))
//            }
//        }
//    }
