package com.rbkmoney.fraudbusters.exception;

public class DgraphException extends RuntimeException {

    public DgraphException(String message) {
        super(message);
    }

    public DgraphException(String message, Throwable cause) {
        super(message, cause);
    }

    public DgraphException(Throwable cause) {
        super(cause);
    }

    public DgraphException(String message,
                           Throwable cause,
                           boolean enableSuppression,
                           boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
