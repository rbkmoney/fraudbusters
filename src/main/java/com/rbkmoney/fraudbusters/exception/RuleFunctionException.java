package com.rbkmoney.fraudbusters.exception;


public class RuleFunctionException extends RuntimeException {

    public RuleFunctionException() {
    }

    public RuleFunctionException(String message) {
        super(message);
    }

    public RuleFunctionException(String message, Throwable cause) {
        super(message, cause);
    }

    public RuleFunctionException(Throwable cause) {
        super(cause);
    }

    public RuleFunctionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
