package com.rbkmoney.fraudbusters.service;

import com.rbkmoney.fraudbusters.domain.dgraph.DgraphPayment;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

public interface TemplateService {

    String buildInsertPaymentNqsBlock(DgraphPayment payment);

    String buildUpsetPaymentQuery(DgraphPayment payment);

    String buildTemplate(Template template, VelocityContext context);

}
