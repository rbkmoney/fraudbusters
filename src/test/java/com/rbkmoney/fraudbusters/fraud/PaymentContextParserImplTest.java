package com.rbkmoney.fraudbusters.fraud;

import com.rbkmoney.fraudbusters.fraud.payment.PaymentContextParserImpl;
import com.rbkmoney.fraudo.FraudoPaymentParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class PaymentContextParserImplTest {

    private FraudContextParser<FraudoPaymentParser.ParseContext> fraudContextParser = new PaymentContextParserImpl();

    @Test
    public void parse() {
        FraudoPaymentParser.ParseContext parse = fraudContextParser.parse("rule: 3 > 2 AND 1 = 1 -> accept;");
        assertFalse(parse.fraud_rule().isEmpty());
    }
}
