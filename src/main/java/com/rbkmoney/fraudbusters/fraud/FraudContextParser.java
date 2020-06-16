package com.rbkmoney.fraudbusters.fraud;

import org.antlr.v4.runtime.ParserRuleContext;

public interface FraudContextParser<T extends ParserRuleContext> {

    T parse(String template);

}
