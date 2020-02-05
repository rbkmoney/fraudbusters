package com.rbkmoney.fraudbusters.stream;

@FunctionalInterface
public interface TemplateVisitor<T, U> {

    U visit(T t);

}
