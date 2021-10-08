package com.rbkmoney.fraudbusters.repository.dgraph;

import com.rbkmoney.fraudbusters.constant.DgraphPaymentUpsertConstants;
import com.rbkmoney.fraudbusters.domain.dgraph.DgraphPayment;
import com.rbkmoney.fraudbusters.repository.Repository;
import com.rbkmoney.fraudbusters.service.dto.FilterDto;
import io.dgraph.DgraphClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.util.List;

@Slf4j
@Component
public class DgraphPaymentRepository extends AbstractDgraphDao implements Repository<DgraphPayment> {

    private final VelocityEngine velocityEngine;

    public DgraphPaymentRepository(DgraphClient dgraphClient,
                                   RetryTemplate dgraphRetryTemplate,
                                   VelocityEngine velocityEngine) {
        super(dgraphClient, dgraphRetryTemplate);
        this.velocityEngine = velocityEngine;
    }

    @Override
    public void insert(DgraphPayment dgraphPayment) {
        VelocityContext context = new VelocityContext();
        context.put("payment", dgraphPayment);
        context.put("constants", new DgraphPaymentUpsertConstants());
        String upsertQuery =
                build(velocityEngine.getTemplate("vm/upsert_global_payment_data_query.vm"), context);
        String insertNqsBlock =
                build(velocityEngine.getTemplate("vm/insert_payment_to_dgraph.vm"), context);

        saveNqsToDgraph(insertNqsBlock, upsertQuery);
    }

    @Override
    public void insertBatch(List<DgraphPayment> batch) {
        throw new UnsupportedOperationException("The 'Insert batch' operation for the dgraph payment repository " +
                "is not implemented yet!");
    }

    @Override
    public List<DgraphPayment> getByFilter(FilterDto filter) {
        return null;
    }

    public String build(Template template, VelocityContext context) {
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }

}
