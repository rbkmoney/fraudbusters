package com.rbkmoney.fraudbusters.dgraph.aggregates;

import com.rbkmoney.fraudbusters.fraud.constant.DgraphEntity;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.DgraphEntityResolver;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DgraphEntityResolverTest {

    private DgraphEntityResolver dgraphEntityResolver = new DgraphEntityResolver();

    @Test
    public void resolvePaymentCheckedFieldsToMapTest() {
        List<PaymentCheckedField> fields = List.of(
                PaymentCheckedField.BIN,
                PaymentCheckedField.PARTY_ID,
                PaymentCheckedField.SHOP_ID,
                PaymentCheckedField.CARD_TOKEN
        );
        var dgraphEntitySetMap =
                dgraphEntityResolver.resolvePaymentCheckedFieldsToMap(fields);
        assertNotNull(dgraphEntitySetMap);
        assertEquals(4, dgraphEntitySetMap.keySet().size());
        assertEquals(1, dgraphEntitySetMap.get(DgraphEntity.BIN).size());
        assertEquals(1, dgraphEntitySetMap.get(DgraphEntity.SHOP).size());
        assertEquals(1, dgraphEntitySetMap.get(DgraphEntity.PARTY).size());
        assertEquals(1, dgraphEntitySetMap.get(DgraphEntity.TOKEN).size());
    }

}
