package com.rbkmoney.fraudbusters.repository.dgraph;

import com.rbkmoney.fraudbusters.domain.dgraph.DgraphWithdrawal;
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
public class DgraphWithdrawalRepository extends AbstractDgraphDao implements Repository<DgraphWithdrawal> {

    private final TemplateService templateService;

    public DgraphWithdrawalRepository(DgraphClient dgraphClient,
                                      RetryTemplate dgraphRetryTemplate,
                                      TemplateService templateService) {
        super(dgraphClient, dgraphRetryTemplate);
        this.templateService = templateService;
    }

    @Override
    public void insert(DgraphWithdrawal dgraphWithdrawal) {
        String upsertQuery = templateService.buildUpsetWithdrawalQuery(dgraphWithdrawal);
        String insertNqsBlock = templateService.buildInsertWithdrawalNqsBlock(dgraphWithdrawal);
        saveNqsToDgraph(insertNqsBlock, upsertQuery);
    }

    @Override
    public void insertBatch(List<DgraphWithdrawal> batch) {
        throw new UnsupportedOperationException("The 'Insert batch' operation for the dgraph withdrawal repository " +
                "is not implemented yet!");
    }

    @Override
    public List<DgraphWithdrawal> getByFilter(FilterDto filter) {
        throw new UnsupportedOperationException("The 'getByFilter' operation for the dgraph withdrawal repository " +
                "is not implemented yet!");
    }
}
