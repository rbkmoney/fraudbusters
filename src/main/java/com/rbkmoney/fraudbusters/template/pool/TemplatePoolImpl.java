package com.rbkmoney.fraudbusters.template.pool;

import com.rbkmoney.fraudo.FraudoParser;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TemplatePoolImpl implements Pool<FraudoParser.ParseContext> {

    private Map<String, FraudoParser.ParseContext> templates = new ConcurrentHashMap<>();

    @Override
    public void add(String key, FraudoParser.ParseContext parseContext) {
        templates.put(key, parseContext);
    }

    @Override
    public FraudoParser.ParseContext get(String key) {
        return key != null ? templates.get(key) : null;
    }

    @Override
    public void remove(String key) {
        templates.remove(key);
    }
}
