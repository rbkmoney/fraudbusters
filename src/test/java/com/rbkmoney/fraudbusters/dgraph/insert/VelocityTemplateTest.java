package com.rbkmoney.fraudbusters.dgraph.insert;

import com.rbkmoney.fraudbusters.config.dgraph.TemplateConfig;
import com.rbkmoney.fraudbusters.dgraph.insert.data.VelocityTestData;
import com.rbkmoney.fraudbusters.domain.dgraph.common.*;
import com.rbkmoney.fraudbusters.service.template.TemplateService;
import com.rbkmoney.fraudbusters.service.template.insert.chargeback.InsertChargebackQueryTemplateService;
import com.rbkmoney.fraudbusters.service.template.insert.chargeback.UpsertChargebackQueryTemplateService;
import com.rbkmoney.fraudbusters.service.template.insert.fraud.InsertFraudPaymentQueryTemplateService;
import com.rbkmoney.fraudbusters.service.template.insert.fraud.UpsertFraudPaymentQueryTemplateService;
import com.rbkmoney.fraudbusters.service.template.insert.payment.InsertPaymentQueryTemplateService;
import com.rbkmoney.fraudbusters.service.template.insert.payment.UpsertPaymentQueryTemplateService;
import com.rbkmoney.fraudbusters.service.template.insert.refund.InsertRefundQueryTemplateService;
import com.rbkmoney.fraudbusters.service.template.insert.refund.UpsertRefundQueryTemplateService;
import com.rbkmoney.fraudbusters.service.template.insert.withdrawal.InsertWithdrawalQueryTemplateService;
import com.rbkmoney.fraudbusters.service.template.insert.withdrawal.UpsertWithdrawalQueryTemplateService;
import org.apache.velocity.app.VelocityEngine;
import org.junit.jupiter.api.Test;

import static com.rbkmoney.fraudbusters.factory.TestDgraphObjectFactory.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class VelocityTemplateTest {

    private final VelocityEngine velocityEngine = new TemplateConfig().velocityEngine();
    private final TemplateService<DgraphPayment> insertPaymentQueryTemplateService =
            new InsertPaymentQueryTemplateService(velocityEngine);
    private final TemplateService<DgraphPayment> upsertPaymentQueryTemplateService =
            new UpsertPaymentQueryTemplateService(velocityEngine);
    private final TemplateService<DgraphFraudPayment> insertFraudPaymentQueryTemplateService =
            new InsertFraudPaymentQueryTemplateService(velocityEngine);
    private final TemplateService<DgraphFraudPayment> upsertFraudPaymentQueryTemplateService =
            new UpsertFraudPaymentQueryTemplateService(velocityEngine);
    private final TemplateService<DgraphRefund> upsertRefundQueryTemplateService =
            new UpsertRefundQueryTemplateService(velocityEngine);
    private final TemplateService<DgraphRefund> insertRefundQueryTemplateService =
            new InsertRefundQueryTemplateService(velocityEngine);
    private final TemplateService<DgraphChargeback> upsertChargebackQueryTemplateService =
            new UpsertChargebackQueryTemplateService(velocityEngine);
    private final TemplateService<DgraphChargeback> insertChargebackQueryTemplateService =
            new InsertChargebackQueryTemplateService(velocityEngine);
    private final TemplateService<DgraphWithdrawal> upsertWithdrawalQueryTemplateService =
            new UpsertWithdrawalQueryTemplateService(velocityEngine);
    private final TemplateService<DgraphWithdrawal> insertWithdrawalQueryTemplateService =
            new InsertWithdrawalQueryTemplateService(velocityEngine);

    @Test
    public void generatePaymentUpsertQueryTest() {
        String firstQuery = upsertPaymentQueryTemplateService.build(createSmallTestDgraphPayment());
        assertNotNull(firstQuery);
        assertEquals(VelocityTestData.TEST_SHORT_PAYMENT_UPSERT_QUERY, firstQuery);

        String secondQuery = upsertPaymentQueryTemplateService.build(createFullTestDgraphPayment());
        assertNotNull(secondQuery);
        assertEquals(VelocityTestData.TEST_FULL_PAYMENT_UPSERT_QUERY, secondQuery);
    }

    @Test
    public void generatePaymentInsertTest() {
        String firstInsertBlock = insertPaymentQueryTemplateService.build(createSmallTestDgraphPayment());
        assertNotNull(firstInsertBlock);
        assertEquals(VelocityTestData.TEST_INSERT_PAYMENT_SHORT_BLOCK, firstInsertBlock);

        String secondInsertBlock = insertPaymentQueryTemplateService.build(createFullTestDgraphPayment());
        assertNotNull(secondInsertBlock);
        assertEquals(VelocityTestData.TEST_INSERT_FULL_PAYMENT_BLOCK, secondInsertBlock);
    }

    @Test
    public void generateRefundUpsertQueryTemplateTest() {
        String firstQuery = upsertRefundQueryTemplateService.build(createSmallTestDgraphRefund());
        assertNotNull(firstQuery);
        assertEquals(VelocityTestData.TEST_SMALL_REFUND_UPSERT_QUERY, firstQuery);

        String secondQuery = upsertRefundQueryTemplateService.build(createFullTestDgraphRefund());
        assertNotNull(secondQuery);
        assertEquals(VelocityTestData.TEST_FULL_REFUND_UPSERT_QUERY, secondQuery);
    }

    @Test
    public void generateInsertRefundTemplateTest() {
        String firstQuery = insertRefundQueryTemplateService.build(createSmallTestDgraphRefund());
        assertNotNull(firstQuery);
        assertEquals(VelocityTestData.TEST_INSERT_SMALL_REFUND_BLOCK, firstQuery);

        String secondQuery = insertRefundQueryTemplateService.build(createFullTestDgraphRefund());
        assertNotNull(secondQuery);
        assertEquals(VelocityTestData.TEST_INSERT_FULL_REFUND_BLOCK, secondQuery);
    }

    @Test
    public void generateChargebackUpsertQueryTemplateTest() {
        String firstQuery = upsertChargebackQueryTemplateService.build(createSmallTestDgraphChargeback());
        assertNotNull(firstQuery);
        assertEquals(VelocityTestData.TEST_SMALL_CHARGEBACK_UPSERT_QUERY, firstQuery);

        String secondQuery = upsertChargebackQueryTemplateService.build(createFullTestDgraphChargeback());
        assertNotNull(secondQuery);
        assertEquals(VelocityTestData.TEST_FULL_CHARGEBACK_UPSERT_QUERY, secondQuery);
    }

    @Test
    public void generateInsertChargebackTemplateTest() {
        String firstQuery = insertChargebackQueryTemplateService.build(createSmallTestDgraphChargeback());
        assertNotNull(firstQuery);
        assertEquals(VelocityTestData.TEST_INSERT_SMALL_CHARGEBACK_BLOCK, firstQuery);

        String secondQuery = insertChargebackQueryTemplateService.build(createFullTestDgraphChargeback());
        assertNotNull(secondQuery);
        assertEquals(VelocityTestData.TEST_INSERT_FULL_CHARGEBACK_BLOCK, secondQuery);
    }

    @Test
    public void generateWithdrawalUpsertQueryTemplateTest() {
        String firstQuery = upsertWithdrawalQueryTemplateService.build(createTestSmallDgraphWithdrawal());
        assertNotNull(firstQuery);
        assertEquals(VelocityTestData.TEST_SMALL_WITHDRAWAL_UPSERT_QUERY, firstQuery);

        String secondQuery = upsertWithdrawalQueryTemplateService.build(createTestFullDgraphWithdrawal());
        assertNotNull(secondQuery);
        assertEquals(VelocityTestData.TEST_FULL_WITHDRAWAL_UPSERT_QUERY, secondQuery);
    }

    @Test
    public void generateInsertWithdrawalTemplateTest() {
        String firstQuery = insertWithdrawalQueryTemplateService.build(createTestSmallDgraphWithdrawal());
        assertNotNull(firstQuery);
        assertEquals(VelocityTestData.TEST_INSERT_SMALL_WITHDRAWAL_BLOCK, firstQuery);

        String secondQuery = insertWithdrawalQueryTemplateService.build(createTestFullDgraphWithdrawal());
        assertNotNull(secondQuery);
        assertEquals(VelocityTestData.TEST_INSERT_FULL_WITHDRAWAL_BLOCK, secondQuery);
    }

    @Test
    public void generateFraudPaymentUpsertQueryTemplateTest() {
        String query = upsertFraudPaymentQueryTemplateService.build(createTestFraudDgraphPayment());
        assertNotNull(query);
        assertEquals(VelocityTestData.TEST_FRAUD_PAYMENT_UPSERT_QUERY, query);
    }

    @Test
    public void generateInsertFraudPaymentTemplateTest() {
        String insertBlock = insertFraudPaymentQueryTemplateService.build(createTestFraudDgraphPayment());
        assertNotNull(insertBlock);
        assertEquals(VelocityTestData.TEST_INSERT_FRAUD_PAYMENT_BLOCK, insertBlock);
    }

}
