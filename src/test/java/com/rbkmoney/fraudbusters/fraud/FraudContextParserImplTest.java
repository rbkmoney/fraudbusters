package com.rbkmoney.fraudbusters.fraud;

import com.rbkmoney.fraudo.FraudoParser;
import org.junit.Assert;
import org.junit.Test;

public class FraudContextParserImplTest {

    private FraudContextParser fraudContextParser = new FraudContextParserImpl();

    @Test
    public void parse() {
        FraudoParser.ParseContext parse = fraudContextParser.parse("rule: 3 > 2 AND 1 = 1\n" +
                "-> accept;");
        Assert.assertFalse(parse.fraud_rule().isEmpty());
    }
}