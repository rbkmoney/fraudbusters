package com.rbkmoney.fraudbusters.repository.dgraph;

import com.rbkmoney.fraudbusters.domain.dgraph.DgraphPayment;
import com.rbkmoney.fraudbusters.repository.Repository;
import com.rbkmoney.fraudbusters.service.dto.FilterDto;
import com.rbkmoney.fraudbusters.service.template.TemplateService;
import io.dgraph.DgraphClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@Lazy
public class DgraphPaymentRepository extends AbstractDgraphDao implements Repository<DgraphPayment> {

    private final TemplateService<DgraphPayment> insertPaymentQueryTemplateService;
    private final TemplateService<DgraphPayment> upsertPaymentQueryTemplateService;

    public DgraphPaymentRepository(DgraphClient dgraphClient,
                                   RetryTemplate dgraphRetryTemplate,
                                   TemplateService<DgraphPayment> insertPaymentQueryTemplateService,
                                   TemplateService<DgraphPayment> upsertPaymentQueryTemplateService) {
        super(dgraphClient, dgraphRetryTemplate);
        this.insertPaymentQueryTemplateService = insertPaymentQueryTemplateService;
        this.upsertPaymentQueryTemplateService = upsertPaymentQueryTemplateService;
    }

    @Override
    public void insert(DgraphPayment dgraphPayment) {
        String upsertQuery = upsertPaymentQueryTemplateService.build(dgraphPayment);
        String insertNqsBlock = insertPaymentQueryTemplateService.build(dgraphPayment);
        saveNqsToDgraph(insertNqsBlock, upsertQuery);
    }

    @Override
    public void insertBatch(List<DgraphPayment> batch) {
        throw new UnsupportedOperationException("The 'Insert batch' operation for the dgraph payment repository " +
                "is not implemented yet!");
    }

    @Override
    public List<DgraphPayment> getByFilter(FilterDto filter) {
        throw new UnsupportedOperationException("The 'getByFilter' operation for the dgraph payment repository " +
                "is not implemented yet!");
    }

}
