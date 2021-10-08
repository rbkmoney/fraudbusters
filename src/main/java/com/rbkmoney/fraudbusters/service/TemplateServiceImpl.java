package com.rbkmoney.fraudbusters.service;

import com.rbkmoney.fraudbusters.domain.dgraph.DgraphPayment;
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

    @Override
    public String buildPaymentNqs(DgraphPayment payment) {
        VelocityContext transactionContext = new VelocityContext();
        transactionContext.put("payment", payment);
        return build(velocityEngine.getTemplate("vm/insert_payment_to_dgraph.vm"), transactionContext);
    }

    public String build(Template template, VelocityContext context) {
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }

}
