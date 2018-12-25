package com.rbkmoney.fraudbusters.fraud;

import com.rbkmoney.fraudo.FraudoParser;

public interface FraudContextParser {

    FraudoParser.ParseContext parse(String template);

}
