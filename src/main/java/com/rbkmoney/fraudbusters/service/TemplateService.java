package com.rbkmoney.fraudbusters.service;

import com.rbkmoney.fraudbusters.domain.dgraph.DgraphChargeback;
import com.rbkmoney.fraudbusters.domain.dgraph.DgraphFraudPayment;
import com.rbkmoney.fraudbusters.domain.dgraph.DgraphPayment;
import com.rbkmoney.fraudbusters.domain.dgraph.DgraphRefund;
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

    String buildTemplate(Template template, VelocityContext context);

}
