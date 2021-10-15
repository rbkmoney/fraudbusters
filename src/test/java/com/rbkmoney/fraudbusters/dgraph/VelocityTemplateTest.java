package com.rbkmoney.fraudbusters.dgraph;

import com.rbkmoney.fraudbusters.config.dgraph.TemplateConfig;
import com.rbkmoney.fraudbusters.dgraph.data.VelocityTestData;
import com.rbkmoney.fraudbusters.service.TemplateService;
import com.rbkmoney.fraudbusters.service.TemplateServiceImpl;
import org.apache.velocity.app.VelocityEngine;
import org.junit.jupiter.api.Test;

import static com.rbkmoney.fraudbusters.factory.TestDgraphObjectFactory.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class VelocityTemplateTest {

    private final VelocityEngine velocityEngine = new TemplateConfig().velocityEngine();

    private final TemplateService templateService = new TemplateServiceImpl(velocityEngine);

    @Test
    public void generatePaymentUpsertQueryTest() {
        String firstQuery = templateService.buildUpsetPaymentQuery(createSmallTestDgraphPayment());
        assertNotNull(firstQuery);
        assertEquals(VelocityTestData.TEST_SHORT_PAYMENT_UPSERT_QUERY, firstQuery);

        String secondQuery = templateService.buildUpsetPaymentQuery(createFullTestDgraphPayment());
        assertNotNull(secondQuery);
        assertEquals(VelocityTestData.TEST_FULL_PAYMENT_UPSERT_QUERY, secondQuery);
    }

    @Test
    public void generatePaymentInsertTest() {
        String firstInsertBlock = templateService.buildInsertPaymentNqsBlock(createSmallTestDgraphPayment());
        assertNotNull(firstInsertBlock);
        assertEquals(VelocityTestData.TEST_INSERT_PAYMENT_SHORT_BLOCK, firstInsertBlock);

        String secondInsertBlock = templateService.buildInsertPaymentNqsBlock(createFullTestDgraphPayment());
        assertNotNull(secondInsertBlock);
        assertEquals(VelocityTestData.TEST_INSERT_FULL_PAYMENT_BLOCK, secondInsertBlock);
    }

    @Test
    public void generateFraudPaymentUpsertQueryTemplateTest() {
        String query = templateService.buildUpsetFraudPaymentQuery(createTestFraudDgraphPayment());
        assertNotNull(query);
        assertEquals(VelocityTestData.TEST_FRAUD_PAYMENT_UPSERT_QUERY, query);
    }

    @Test
    public void generateInsertFraudPaymentTemplateTest() {
        String insertBlock = templateService.buildInsertFraudPaymentNqsBlock(createTestFraudDgraphPayment());
        assertNotNull(insertBlock);
        assertEquals(VelocityTestData.TEST_INSERT_FRAUD_PAYMENT_BLOCK, insertBlock);
    }

}
