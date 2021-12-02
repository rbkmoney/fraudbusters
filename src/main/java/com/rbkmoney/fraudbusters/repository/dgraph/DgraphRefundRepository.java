package com.rbkmoney.fraudbusters.repository.dgraph;

import com.rbkmoney.fraudbusters.domain.dgraph.DgraphRefund;
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
@Lazy
@Component
public class DgraphRefundRepository extends AbstractDgraphDao implements Repository<DgraphRefund> {

    private final TemplateService<DgraphRefund> insertRefundQueryTemplateService;
    private final TemplateService<DgraphRefund> upsertRefundQueryTemplateService;

    public DgraphRefundRepository(DgraphClient dgraphClient,
                                  RetryTemplate dgraphRetryTemplate,
                                  TemplateService<DgraphRefund> insertRefundQueryTemplateService,
                                  TemplateService<DgraphRefund> upsertRefundQueryTemplateService) {
        super(dgraphClient, dgraphRetryTemplate);
        this.insertRefundQueryTemplateService = insertRefundQueryTemplateService;
        this.upsertRefundQueryTemplateService = upsertRefundQueryTemplateService;
    }

    @Override
    public void insert(DgraphRefund dgraphRefund) {
        String upsertQuery = upsertRefundQueryTemplateService.build(dgraphRefund);
        String insertNqsBlock = insertRefundQueryTemplateService.build(dgraphRefund);
        saveNqsToDgraph(insertNqsBlock, upsertQuery);
    }

    @Override
    public void insertBatch(List<DgraphRefund> batch) {
        throw new UnsupportedOperationException("The 'Insert batch' operation for the dgraph refund repository " +
                "is not implemented yet!");
    }

    @Override
    public List<DgraphRefund> getByFilter(FilterDto filter) {
        throw new UnsupportedOperationException("The 'getByFilter' operation for the dgraph refund repository " +
                "is not implemented yet!");
    }
}
