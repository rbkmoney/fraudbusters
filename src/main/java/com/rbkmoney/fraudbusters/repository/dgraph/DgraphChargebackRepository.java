package com.rbkmoney.fraudbusters.repository.dgraph;

import com.rbkmoney.fraudbusters.domain.dgraph.DgraphChargeback;
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
public class DgraphChargebackRepository extends AbstractDgraphDao implements Repository<DgraphChargeback> {

    private final TemplateService<DgraphChargeback> insertChargebackQueryTemplateService;
    private final TemplateService<DgraphChargeback> upsertChargebackQueryTemplateService;

    public DgraphChargebackRepository(DgraphClient dgraphClient,
                                      RetryTemplate dgraphRetryTemplate,
                                      TemplateService<DgraphChargeback> insertChargebackQueryTemplateService,
                                      TemplateService<DgraphChargeback> upsertChargebackQueryTemplateService) {
        super(dgraphClient, dgraphRetryTemplate);
        this.insertChargebackQueryTemplateService = insertChargebackQueryTemplateService;
        this.upsertChargebackQueryTemplateService = upsertChargebackQueryTemplateService;
    }

    @Override
    public void insert(DgraphChargeback dgraphChargeback) {
        String upsertQuery = upsertChargebackQueryTemplateService.build(dgraphChargeback);
        String insertNqsBlock = insertChargebackQueryTemplateService.build(dgraphChargeback);
        saveNqsToDgraph(insertNqsBlock, upsertQuery);
    }

    @Override
    public void insertBatch(List<DgraphChargeback> batch) {
        throw new UnsupportedOperationException("The 'Insert batch' operation for the dgraph chargeback " +
                "repository is not implemented yet!");
    }

    @Override
    public List<DgraphChargeback> getByFilter(FilterDto filter) {
        throw new UnsupportedOperationException("The 'getByFilter' operation for the dgraph chargeback " +
                "repository is not implemented yet!");
    }
}
