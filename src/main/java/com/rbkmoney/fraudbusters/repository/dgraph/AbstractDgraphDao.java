package com.rbkmoney.fraudbusters.repository.dgraph;

import com.google.protobuf.ByteString;
import com.rbkmoney.fraudbusters.exception.DgraphException;
import io.dgraph.DgraphClient;
import io.dgraph.DgraphProto;
import io.dgraph.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.support.RetryTemplate;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractDgraphDao {

    private final DgraphClient dgraphClient;
    private final RetryTemplate dgraphRetryTemplate;

    public void saveNqsToDgraph(String nqs, String query) {
        dgraphRetryTemplate.execute(context -> insertNqsToDgraph(nqs, query));
    }

    public boolean insertNqsToDgraph(String nqs, String query) {
        try (Transaction transaction = dgraphClient.newTransaction()) {
            log.trace("InsertJsonToDgraph will save data (nqs: {})", nqs);
            DgraphProto.Mutation mutation = DgraphProto.Mutation.newBuilder()
                    .setSetNquads(ByteString.copyFromUtf8(nqs))
                    .build();
            var req = DgraphProto.Request.newBuilder()
                    .addMutations(mutation)
                    .setQuery(query)
                    .setCommitNow(true)
                    .build();
            final DgraphProto.Response response = transaction.doRequest(req);
            log.debug("InsertJsonToDgraph received response (nqs: {}): {}", nqs, response);
            return true;
        } catch (RuntimeException ex) {
            log.warn("Received a dgraph exception while the service add new data)", ex);
            throw new DgraphException(String.format("Received exception from dgraph while the service " +
                    "was saving data (nqs: %s)", nqs), ex);
        }
    }

    protected DgraphProto.Response processDgraphQuery(String query) {
        try (Transaction transaction = dgraphClient.newTransaction()) {
            return transaction.query(query);
        } catch (RuntimeException ex) {
            throw new DgraphException(String.format("Received exception from dgraph while the service " +
                    "process query with args (query: %s)", query), ex);
        }
    }

}
