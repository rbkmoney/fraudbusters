package com.rbkmoney.fraudbusters.exception;

public class UnknownLocationException extends RuntimeException {

    private final static String ERROR_MESSAGE = "Unknown ip location!";

    public UnknownLocationException() {
        super(ERROR_MESSAGE);
    }

}
