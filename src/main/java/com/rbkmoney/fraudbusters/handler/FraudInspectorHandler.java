package com.rbkmoney.fraudbusters.handler;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.rbkmoney.damsel.base.InvalidRequest;
import com.rbkmoney.damsel.domain.RiskScore;
import com.rbkmoney.damsel.proxy_inspector.Context;
import com.rbkmoney.damsel.proxy_inspector.InspectorProxySrv;
import org.apache.thrift.TException;

import java.security.Key;
import java.util.concurrent.*;

public class FraudInspectorHandler implements InspectorProxySrv.Iface {

    private ExecutorService service = Executors.newFixedThreadPool(100);

    Cache<Key, Future<RiskScore>> risk = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build();

    @Override

    public RiskScore inspectPayment(Context context) throws InvalidRequest, TException {

        Future<RiskScore> submit = service.submit(new Callable<RiskScore>() {
            @Override
            public RiskScore call() {
                return RiskScore.fatal;
            }
        });


        try {
            return submit.get();
        } catch (InterruptedException e) {

        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

}
