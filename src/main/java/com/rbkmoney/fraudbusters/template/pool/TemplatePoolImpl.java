package com.rbkmoney.fraudbusters.template.pool;

import lombok.ToString;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ToString
public class TemplatePoolImpl implements Pool<ParserRuleContext> {

    private final Map<String, ParserRuleContext> templates = new ConcurrentHashMap<>();

    @Override
    public void add(String key, ParserRuleContext parseContext) {
        templates.put(key, parseContext);
    }

    @Override
    public ParserRuleContext get(String key) {
        return key != null ? templates.get(key) : null;
    }

    @Override
    public void remove(String key) {
        templates.remove(key);
    }

    @Override
    public int size() {
        return templates.size();
    }

}
