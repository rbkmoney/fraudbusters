package com.rbkmoney.fraudbusters.fraud;

import com.rbkmoney.fraudo.FraudoLexer;
import com.rbkmoney.fraudo.FraudoParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.springframework.stereotype.Component;

@Component
public class FraudContextParserImpl implements FraudContextParser {

    @Override
    public FraudoParser.ParseContext parse(String template) {
        FraudoLexer lexer = new FraudoLexer(CharStreams.fromString(template));
        FraudoParser parser = new FraudoParser(new CommonTokenStream(lexer));
        return parser.parse();
    }

}
