package com.rbkmoney.fraudbusters.repository.dgraph;

import com.rbkmoney.fraudbusters.domain.dgraph.DgraphRefund;
import com.rbkmoney.fraudbusters.repository.Repository;
import com.rbkmoney.fraudbusters.service.TemplateService;
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
public class DgraphRefundRepository extends AbstractDgraphDao implements Repository<DgraphRefund> {

    private final TemplateService templateService;

    public DgraphRefundRepository(DgraphClient dgraphClient,
                                  RetryTemplate dgraphRetryTemplate,
                                  TemplateService templateService) {
        super(dgraphClient, dgraphRetryTemplate);
        this.templateService = templateService;
    }

    @Override
    public void insert(DgraphRefund dgraphRefund) {
        String upsertQuery = templateService.buildUpsetRefundQuery(dgraphRefund);
        String insertNqsBlock = templateService.buildInsertRefundNqsBlock(dgraphRefund);
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
