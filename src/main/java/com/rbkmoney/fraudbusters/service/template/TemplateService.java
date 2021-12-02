package com.rbkmoney.fraudbusters.service.template;

public interface TemplateService<T> {

    String build(T object);

}
