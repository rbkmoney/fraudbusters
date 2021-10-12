package com.rbkmoney.fraudbusters.service;

import com.rbkmoney.fraudbusters.constant.DgraphPaymentUpsertConstants;
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

    private static final String INSERT_PAYMENT_TO_DGRAPH = "vm/insert_payment_to_dgraph.vm";

    private static final String UPSERT_GLOBAL_PAYMENT_DATA_QUERY = "vm/upsert_global_payment_data_query.vm";

    @Override
    public String buildInsertPaymentNqsBlock(DgraphPayment dgraphPayment) {
        return buildDefaultPaymentTemplate(dgraphPayment, INSERT_PAYMENT_TO_DGRAPH);
    }

    @Override
    public String buildUpsetPaymentQuery(DgraphPayment dgraphPayment) {
        return buildDefaultPaymentTemplate(dgraphPayment, UPSERT_GLOBAL_PAYMENT_DATA_QUERY);
    }

    @Override
    public String buildTemplate(Template template, VelocityContext context) {
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }

    private VelocityContext createDefaultPaymentInsertContext(DgraphPayment dgraphPayment) {
        VelocityContext context = new VelocityContext();
        context.put("payment", dgraphPayment);
        context.put("constants", new DgraphPaymentUpsertConstants());
        return context;
    }

    private String buildDefaultPaymentTemplate(DgraphPayment dgraphPayment, String template) {
        return buildTemplate(
                velocityEngine.getTemplate(template),
                createDefaultPaymentInsertContext(dgraphPayment)
        );
    }

}
