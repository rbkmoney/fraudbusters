package com.rbkmoney.fraudbusters.fraud.p2p;

import com.rbkmoney.fraudbusters.fraud.FraudContextParser;
import com.rbkmoney.fraudo.FraudoP2PLexer;
import com.rbkmoney.fraudo.FraudoP2PParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class P2PContextParserImpl implements FraudContextParser<FraudoP2PParser.ParseContext> {

    @Override
    public FraudoP2PParser.ParseContext parse(String template) {
        if (!StringUtils.hasLength(template)) {
            return null;
        }
        FraudoP2PLexer lexer = new FraudoP2PLexer(CharStreams.fromString(template));
        FraudoP2PParser parser = new FraudoP2PParser(new CommonTokenStream(lexer));
        return parser.parse();
    }

}
