package com.rbkmoney.fraudbusters.service;

import com.rbkmoney.fraudbusters.constant.DgraphPaymentUpsertConstants;
import com.rbkmoney.fraudbusters.domain.dgraph.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.stereotype.Service;

import java.io.StringWriter;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {

    private final VelocityEngine velocityEngine;

    private static final String INSERT_PAYMENT_TO_DGRAPH = "vm/payment/insert_payment_to_dgraph.vm";
    private static final String UPSERT_GLOBAL_PAYMENT_DATA_QUERY = "vm/payment/upsert_global_payment_data_query.vm";
    private static final String INSERT_FRAUD_PAYMENT_TO_DGRAPH = "vm/fraud_payment/insert_fraud_payment_to_dgraph.vm";
    private static final String UPSERT_FRAUD_PAYMENT_DATA_QUERY = "vm/fraud_payment/upsert_fraud_payment_data_query.vm";
    private static final String INSERT_REFUND_TO_DGRAPH = "vm/refund/insert_refund_to_dgraph.vm";
    private static final String UPSERT_REFUND_DATA_QUERY = "vm/refund/upsert_global_refund_data_query.vm";
    private static final String INSERT_CHARGEBACK_TO_DGRAPH = "vm/chargeback/insert_chargeback_to_dgraph.vm";
    private static final String UPSERT_CHARGEBACK_DATA_QUERY = "vm/chargeback/upsert_chargeback_data_query.vm";
    private static final String INSERT_WITHDRAWAL_TO_DGRAPH = "vm/withdrawal/insert_withdrawal_to_dgraph.vm";
    private static final String UPSERT_WITHDRAWAL_DATA_QUERY = "vm/withdrawal/upsert_withdrawal_data_query.vm";

    @Override
    public String buildInsertPaymentNqsBlock(DgraphPayment dgraphPayment) {
        return buildDefaultPaymentTemplate(dgraphPayment, INSERT_PAYMENT_TO_DGRAPH);
    }

    @Override
    public String buildUpsetPaymentQuery(DgraphPayment dgraphPayment) {
        return buildDefaultPaymentTemplate(dgraphPayment, UPSERT_GLOBAL_PAYMENT_DATA_QUERY);
    }

    @Override
    public String buildInsertFraudPaymentNqsBlock(DgraphFraudPayment dgraphFraudPayment) {
        return buildTemplate(
                velocityEngine.getTemplate(INSERT_FRAUD_PAYMENT_TO_DGRAPH),
                createInsertFraudPaymentContext(dgraphFraudPayment)
        );
    }

    @Override
    public String buildUpsetFraudPaymentQuery(DgraphFraudPayment dgraphFraudPayment) {
        return buildTemplate(
                velocityEngine.getTemplate(UPSERT_FRAUD_PAYMENT_DATA_QUERY),
                createInsertFraudPaymentContext(dgraphFraudPayment)
        );
    }

    @Override
    public String buildInsertRefundNqsBlock(DgraphRefund dgraphRefund) {
        return buildTemplate(
                velocityEngine.getTemplate(INSERT_REFUND_TO_DGRAPH),
                createInsertRefundContext(dgraphRefund)
        );
    }

    @Override
    public String buildUpsetRefundQuery(DgraphRefund dgraphRefund) {
        return buildTemplate(
                velocityEngine.getTemplate(UPSERT_REFUND_DATA_QUERY),
                createInsertRefundContext(dgraphRefund)
        );
    }

    @Override
    public String buildInsertChargebackNqsBlock(DgraphChargeback dgraphChargeback) {
        return buildTemplate(
                velocityEngine.getTemplate(INSERT_CHARGEBACK_TO_DGRAPH),
                createInsertChargebackContext(dgraphChargeback)
        );
    }

    @Override
    public String buildUpsetChargebackQuery(DgraphChargeback dgraphChargeback) {
        return buildTemplate(
                velocityEngine.getTemplate(UPSERT_CHARGEBACK_DATA_QUERY),
                createInsertChargebackContext(dgraphChargeback)
        );
    }

    @Override
    public String buildInsertWithdrawalNqsBlock(DgraphWithdrawal dgraphWithdrawal) {
        return buildTemplate(
                velocityEngine.getTemplate(INSERT_WITHDRAWAL_TO_DGRAPH),
                createInsertWithdrawalContext(dgraphWithdrawal)
        );
    }

    @Override
    public String buildUpsetWithdrawalQuery(DgraphWithdrawal dgraphWithdrawal) {
        return buildTemplate(
                velocityEngine.getTemplate(UPSERT_WITHDRAWAL_DATA_QUERY),
                createInsertWithdrawalContext(dgraphWithdrawal)
        );
    }

    @Override
    public String buildTemplate(Template template, VelocityContext context) {
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }

    private VelocityContext createInsertPaymentContext(DgraphPayment dgraphPayment) {
        VelocityContext context = new VelocityContext();
        context.put("payment", dgraphPayment);
        context.put("constants", new DgraphPaymentUpsertConstants());
        return context;
    }

    private VelocityContext createInsertFraudPaymentContext(DgraphFraudPayment fraudPayment) {
        VelocityContext context = new VelocityContext();
        context.put("payment", fraudPayment);
        context.put("constants", new DgraphPaymentUpsertConstants());
        return context;
    }

    private VelocityContext createInsertRefundContext(DgraphRefund dgraphRefund) {
        VelocityContext context = new VelocityContext();
        context.put("refund", dgraphRefund);
        context.put("constants", new DgraphPaymentUpsertConstants());
        return context;
    }

    private VelocityContext createInsertChargebackContext(DgraphChargeback dgraphChargeback) {
        VelocityContext context = new VelocityContext();
        context.put("chargeback", dgraphChargeback);
        context.put("constants", new DgraphPaymentUpsertConstants());
        return context;
    }

    private VelocityContext createInsertWithdrawalContext(DgraphWithdrawal dgraphWithdrawal) {
        VelocityContext context = new VelocityContext();
        context.put("withdrawal", dgraphWithdrawal);
        context.put("constants", new DgraphPaymentUpsertConstants());
        return context;
    }

    private String buildDefaultPaymentTemplate(DgraphPayment dgraphPayment, String template) {
        return buildTemplate(
                velocityEngine.getTemplate(template),
                createInsertPaymentContext(dgraphPayment)
        );
    }

}
