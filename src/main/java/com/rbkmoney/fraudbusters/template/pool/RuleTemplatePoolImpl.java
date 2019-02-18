package com.rbkmoney.fraudbusters.template.pool;

import com.rbkmoney.fraudo.FraudoParser;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RuleTemplatePoolImpl implements RuleTemplatePool {

    private Map<String, FraudoParser.ParseContext> templates = new ConcurrentHashMap<>();

    @Override
    public void add(String key, FraudoParser.ParseContext parseContext) {
        templates.put(key, parseContext);
    }

    @Override
    public FraudoParser.ParseContext get(String key) {
        return templates.get(key);
    }

    @Override
    public void remove(String key) {
        templates.remove(key);
    }
}
