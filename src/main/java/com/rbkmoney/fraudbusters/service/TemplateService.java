package com.rbkmoney.fraudbusters.service;

import com.rbkmoney.fraudbusters.domain.dgraph.*;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

public interface TemplateService {

    String buildInsertPaymentNqsBlock(DgraphPayment payment);

    String buildUpsetPaymentQuery(DgraphPayment payment);

    String buildInsertFraudPaymentNqsBlock(DgraphFraudPayment dgraphFraudPayment);

    String buildUpsetFraudPaymentQuery(DgraphFraudPayment dgraphFraudPayment);

    String buildInsertRefundNqsBlock(DgraphRefund dgraphRefund);

    String buildUpsetRefundQuery(DgraphRefund dgraphRefund);

    String buildInsertChargebackNqsBlock(DgraphChargeback dgraphChargeback);

    String buildUpsetChargebackQuery(DgraphChargeback dgraphChargeback);

    String buildInsertWithdrawalNqsBlock(DgraphWithdrawal dgraphWithdrawal);

    String buildUpsetWithdrawalQuery(DgraphWithdrawal dgraphWithdrawal);

    String buildTemplate(Template template, VelocityContext context);

}
