package com.rbkmoney.fraudbusters.listener;

import com.rbkmoney.damsel.fraudbusters.Command;
import com.rbkmoney.fraudbusters.pool.Pool;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
public class AbstractPoolCommandListenerExecutor {

    protected <T> void execCommand(Command command, String key, Pool<T> pool, Supplier<T> supplier) {
        switch (command.command_type) {
            case CREATE -> pool.add(key, supplier.get());
            case DELETE -> pool.remove(key);
            default -> log.error("Unknown command: {}", command);
        }
    }

    protected <T, R> void execCommand(Command command, String key, Pool<R> pool, Function<T, R> function, T param) {
        switch (command.command_type) {
            case CREATE -> pool.add(key, function.apply(param));
            case DELETE -> pool.remove(key);
            default -> log.error("Unknown command: {}", command);
        }
    }

}
