package com.rbkmoney.fraudbusters.repository.dgraph;

import com.rbkmoney.fraudbusters.domain.dgraph.common.DgraphFraudPayment;
import com.rbkmoney.fraudbusters.repository.Repository;
import com.rbkmoney.fraudbusters.service.template.TemplateService;
import com.rbkmoney.fraudbusters.service.dto.FilterDto;
import io.dgraph.DgraphClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Lazy
@Component
public class DgraphFraudPaymentRepository extends AbstractDgraphDao implements Repository<DgraphFraudPayment> {

    private final TemplateService<DgraphFraudPayment> insertFraudPaymentQueryTemplateService;
    private final TemplateService<DgraphFraudPayment> upsertFraudPaymentQueryTemplateService;

    public DgraphFraudPaymentRepository(DgraphClient dgraphClient,
                                        RetryTemplate dgraphRetryTemplate,
                                        TemplateService<DgraphFraudPayment> insertFraudPaymentQueryTemplateService,
                                        TemplateService<DgraphFraudPayment> upsertFraudPaymentQueryTemplateService) {
        super(dgraphClient, dgraphRetryTemplate);
        this.insertFraudPaymentQueryTemplateService = insertFraudPaymentQueryTemplateService;
        this.upsertFraudPaymentQueryTemplateService = upsertFraudPaymentQueryTemplateService;
    }

    @Override
    public void insert(DgraphFraudPayment dgraphFraudPayment) {
        String upsertQuery = upsertFraudPaymentQueryTemplateService.build(dgraphFraudPayment);
        String insertNqsBlock = insertFraudPaymentQueryTemplateService.build(dgraphFraudPayment);
        saveNqsToDgraph(insertNqsBlock, upsertQuery);
    }

    @Override
    public void insertBatch(List<DgraphFraudPayment> batch) {
        throw new UnsupportedOperationException("The 'Insert batch' operation for the dgraph fraud payment " +
                "repository is not implemented yet!");
    }

    @Override
    public List<DgraphFraudPayment> getByFilter(FilterDto filter) {
        throw new UnsupportedOperationException("The 'getByFilter' operation for the dgraph fraud payment " +
                "repository is not implemented yet!");
    }
}
