package com.rbkmoney.fraudbusters.fraud;

import com.rbkmoney.fraudbusters.fraud.payment.PaymentContextParserImpl;
import com.rbkmoney.fraudo.FraudoPaymentParser;
import org.junit.Assert;
import org.junit.Test;

public class PaymentContextParserImplTest {

    private FraudContextParser<FraudoPaymentParser.ParseContext> fraudContextParser = new PaymentContextParserImpl();

    @Test
    public void parse() {
        FraudoPaymentParser.ParseContext parse = fraudContextParser.parse("rule: 3 > 2 AND 1 = 1\n" +
                                                                          "-> accept;");
        Assert.assertFalse(parse.fraud_rule().isEmpty());
    }
}