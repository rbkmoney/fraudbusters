package com.rbkmoney.fraudbusters.resource.handler;

import com.rbkmoney.damsel.fraudbusters.P2PServiceSrv;
import com.rbkmoney.damsel.fraudbusters.Template;
import com.rbkmoney.damsel.fraudbusters.ValidateTemplateResponse;
import com.rbkmoney.fraudbusters.fraud.ListTemplateValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class P2PTemplateValidatorHandler implements P2PServiceSrv.Iface {

    private final ListTemplateValidator p2pTemplatesValidator;

    @Override
    public ValidateTemplateResponse validateCompilationTemplate(List<Template> list) throws TException {
        try {
            return p2pTemplatesValidator.validateCompilationTemplate(list);
        } catch (Exception e) {
            log.error("P2PTemplateValidatorHandler error when validateCompilationTemplate() e: ", e);
            throw new TException("P2PTemplateValidatorHandler error when validateCompilationTemplate() e: ", e);
        }
    }
}
