package com.rbkmoney.fraudbusters.template.pool;

import com.rbkmoney.fraudo.FraudoParser;

public interface RuleTemplatePool {

    void add(String key, FraudoParser.ParseContext parseContext);

    FraudoParser.ParseContext get(String key);

    void remove(String key);
}
