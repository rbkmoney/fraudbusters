package com.rbkmoney.fraudbusters.listener.payment.historical;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.fraudbusters.pool.HistoricalPool;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
public class AbstractTimePoolCommandListenerExecutor {

    @Getter
    private final AtomicBoolean isTemplateReady = new AtomicBoolean(false);

    protected <T> void execCommand(Command command, String key, Long time, HistoricalPool<T> pool,
                                   Supplier<T> supplier) {
        switch (command.command_type) {
            case CREATE:
                pool.add(key, time, supplier.get());
                return;
            case DELETE:
                pool.add(key, time, null);
                return;
            default:
                log.error("Unknown command: {}", command);
        }
    }

    protected <T, R> void execCommand(Command command, String key, Long time, HistoricalPool<R> pool,
                                      Function<T, R> function, T param) {
        switch (command.command_type) {
            case CREATE:
                pool.add(key, time, function.apply(param));
                return;
            case DELETE:
                pool.add(key, time, null);
                return;
            default:
                log.error("Unknown command: {}", command);
        }
    }

}
