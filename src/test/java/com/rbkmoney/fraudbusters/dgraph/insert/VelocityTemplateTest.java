package com.rbkmoney.fraudbusters.dgraph.insert;

import com.rbkmoney.fraudbusters.config.dgraph.TemplateConfig;
import com.rbkmoney.fraudbusters.dgraph.insert.data.VelocityTestData;
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
    public void generateRefundUpsertQueryTemplateTest() {
        String firstQuery = templateService.buildUpsetRefundQuery(createSmallTestDgraphRefund());
        assertNotNull(firstQuery);
        assertEquals(VelocityTestData.TEST_SMALL_REFUND_UPSERT_QUERY, firstQuery);

        String secondQuery = templateService.buildUpsetRefundQuery(createFullTestDgraphRefund());
        assertNotNull(secondQuery);
        assertEquals(VelocityTestData.TEST_FULL_REFUND_UPSERT_QUERY, secondQuery);
    }

    @Test
    public void generateInsertRefundTemplateTest() {
        String firstQuery = templateService.buildInsertRefundNqsBlock(createSmallTestDgraphRefund());
        assertNotNull(firstQuery);
        assertEquals(VelocityTestData.TEST_INSERT_SMALL_REFUND_BLOCK, firstQuery);

        String secondQuery = templateService.buildInsertRefundNqsBlock(createFullTestDgraphRefund());
        assertNotNull(secondQuery);
        assertEquals(VelocityTestData.TEST_INSERT_FULL_REFUND_BLOCK, secondQuery);
    }


    @Test
    public void generateChargebackUpsertQueryTemplateTest() {
        String firstQuery = templateService.buildUpsetChargebackQuery(createSmallTestDgraphChargeback());
        assertNotNull(firstQuery);
        assertEquals(VelocityTestData.TEST_SMALL_CHARGEBACK_UPSERT_QUERY, firstQuery);

        String secondQuery = templateService.buildUpsetChargebackQuery(createFullTestDgraphChargeback());
        assertNotNull(secondQuery);
        assertEquals(VelocityTestData.TEST_FULL_CHARGEBACK_UPSERT_QUERY, secondQuery);
    }

    @Test
    public void generateInsertChargebackTemplateTest() {
        String firstQuery = templateService.buildInsertChargebackNqsBlock(createSmallTestDgraphChargeback());
        assertNotNull(firstQuery);
        assertEquals(VelocityTestData.TEST_INSERT_SMALL_CHARGEBACK_BLOCK, firstQuery);

        String secondQuery = templateService.buildInsertChargebackNqsBlock(createFullTestDgraphChargeback());
        assertNotNull(secondQuery);
        assertEquals(VelocityTestData.TEST_INSERT_FULL_CHARGEBACK_BLOCK, secondQuery);
    }

    @Test
    public void generateWithdrawalUpsertQueryTemplateTest() {
        String firstQuery = templateService.buildUpsetWithdrawalQuery(createTestSmallDgraphWithdrawal());
        assertNotNull(firstQuery);
        assertEquals(VelocityTestData.TEST_SMALL_WITHDRAWAL_UPSERT_QUERY, firstQuery);

        String secondQuery = templateService.buildUpsetWithdrawalQuery(createTestFullDgraphWithdrawal());
        assertNotNull(secondQuery);
        assertEquals(VelocityTestData.TEST_FULL_WITHDRAWAL_UPSERT_QUERY, secondQuery);
    }

    @Test
    public void generateInsertWithdrawalTemplateTest() {
        String firstQuery = templateService.buildInsertWithdrawalNqsBlock(createTestSmallDgraphWithdrawal());
        assertNotNull(firstQuery);
        assertEquals(VelocityTestData.TEST_INSERT_SMALL_WITHDRAWAL_BLOCK, firstQuery);

        String secondQuery = templateService.buildInsertWithdrawalNqsBlock(createTestFullDgraphWithdrawal());
        assertNotNull(secondQuery);
        assertEquals(VelocityTestData.TEST_INSERT_FULL_WITHDRAWAL_BLOCK, secondQuery);
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
