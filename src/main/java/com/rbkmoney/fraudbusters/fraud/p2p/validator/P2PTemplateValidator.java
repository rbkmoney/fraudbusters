package com.rbkmoney.fraudbusters.fraud.p2p.validator;

import com.rbkmoney.fraudbusters.fraud.FraudTemplateValidator;
import com.rbkmoney.fraudbusters.fraud.listener.ValidateErrorListener;
import com.rbkmoney.fraudo.FraudoP2PLexer;
import com.rbkmoney.fraudo.FraudoP2PParser;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DiagnosticErrorListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class P2PTemplateValidator implements FraudTemplateValidator {

    @Override
    public List<String> validate(String template) {
        log.info("P2PTemplateValidator validate: {}", template);
        FraudoP2PLexer lexer = new FraudoP2PLexer(CharStreams.fromString(template));
        FraudoP2PParser parser = new FraudoP2PParser(new CommonTokenStream(lexer));
        ValidateErrorListener validateErrorListener = new ValidateErrorListener();
        parser.addErrorListener(validateErrorListener);
        parser.parse();
        log.info("P2PTemplateValidator validated errors: {}", validateErrorListener.getErrors());
        return validateErrorListener.getErrors();
    }

}
