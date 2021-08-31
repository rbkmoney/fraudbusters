package com.rbkmoney.fraudbusters.exception;

import org.springframework.util.StringUtils;

import java.util.List;

public class InvalidTemplateException extends RuntimeException {

    private static final String DELIMITER = " ,";

    public InvalidTemplateException() {
    }

    public InvalidTemplateException(String templateId, List<String> reasons) {
        super(String.format("templateId: %s, validateError: %s",
                templateId, StringUtils.collectionToDelimitedString(reasons, DELIMITER)));
    }
}
