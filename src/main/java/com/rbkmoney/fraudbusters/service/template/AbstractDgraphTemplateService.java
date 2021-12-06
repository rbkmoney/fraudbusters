package com.rbkmoney.fraudbusters.service.template;

import com.rbkmoney.fraudbusters.constant.DgraphPaymentUpsertConstants;
import lombok.RequiredArgsConstructor;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;

@RequiredArgsConstructor
public abstract class AbstractDgraphTemplateService {

    private final VelocityEngine velocityEngine;

    protected String buildDgraphTemplate(String template, String variableName, Object object) {
        return buildTemplate(
                velocityEngine.getTemplate(template),
                createContext(variableName, object)
        );
    }

    protected VelocityContext createContext(String variableName, Object object) {
        VelocityContext context = new VelocityContext();
        context.put(variableName, object);
        context.put("constants", new DgraphPaymentUpsertConstants());
        return context;
    }

    private String buildTemplate(Template template, VelocityContext context) {
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }

}
