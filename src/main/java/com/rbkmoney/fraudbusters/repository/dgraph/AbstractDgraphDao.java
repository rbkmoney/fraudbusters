package com.rbkmoney.fraudbusters.repository.dgraph;

import com.google.protobuf.ByteString;
import com.rbkmoney.fraudbusters.exception.DgraphException;
import io.dgraph.DgraphClient;
import io.dgraph.DgraphProto;
import io.dgraph.Transaction;
import io.dgraph.TxnConflictException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.listener.RetryListenerSupport;
import org.springframework.retry.support.RetryTemplate;

import javax.annotation.PostConstruct;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractDgraphDao {

    private final DgraphClient dgraphClient;
    private final RetryTemplate retryTemplate;

    @PostConstruct
    public void init() {
        retryTemplate.registerListener(new RegisterJobFailListener());
    }

    public void saveNqsToDgraph(String nqs, String query) {
        retryTemplate.execute(context -> insertNqsToDgraph(nqs, query));
    }

    public boolean insertNqsToDgraph(String nqs, String query) {
        Transaction transaction = null;
        try {
            transaction = dgraphClient.newTransaction();
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
        } catch (TxnConflictException ex) {
            log.warn("Received TxnConflictException while the service add new data)", ex);
            throw ex;
        } catch (RuntimeException ex) {
            if (ex.getMessage().contains("TxnConflictException")) {
                log.warn("Received inner TxnConflictException while the service add new data", ex);
                throw ex;
            }
            throw new DgraphException(String.format("Received exception from dgraph while the service " +
                    "was saving data (nqs: %s)", nqs), ex);
        } finally {
            if (transaction != null) {
                transaction.discard();
            }
        }
    }

    private static final class RegisterJobFailListener extends RetryListenerSupport {

        @Override
        public <T, E extends Throwable> void onError(RetryContext context,
                                                     RetryCallback<T, E> callback,
                                                     Throwable throwable) {
            log.warn("Register dgraph transaction failed event. Retry count: {}",
                    context.getRetryCount(), context.getLastThrowable());
        }
    }

}
