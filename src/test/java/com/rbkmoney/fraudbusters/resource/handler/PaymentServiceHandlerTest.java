package com.rbkmoney.fraudbusters.resource.handler;

import com.rbkmoney.damsel.fraudbusters.Template;
import com.rbkmoney.damsel.fraudbusters.ValidateTemplateResponse;
import com.rbkmoney.fraudbusters.fraud.payment.validator.PaymentTemplateValidator;
import com.rbkmoney.fraudbusters.fraud.validator.ListTemplateValidatorImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

@Slf4j
class PaymentServiceHandlerTest {

    PaymentServiceHandler paymentServiceHandler = new PaymentServiceHandler(
            new ListTemplateValidatorImpl(new PaymentTemplateValidator()));

    @Test
    void validateCompilationTemplateEmptyList() throws TException {
        ArrayList<Template> list = new ArrayList<>();
        ValidateTemplateResponse validateTemplateResponse = paymentServiceHandler.validateCompilationTemplate(list);
        assertNotNull(validateTemplateResponse.getErrors());
        assertTrue(validateTemplateResponse.getErrors().isEmpty());
    }

    @Test
    void validateCompilationTemplateSuccessList() throws TException {
        ArrayList<Template> list = new ArrayList<>();
        list.add(createTemplate("test_1", "rule: inBlackList(\"email\")-> notify;"));
        list.add(createTemplate("test_2", "rule: inWhiteList(\"email\")-> notify;"));
        list.add(createTemplate("test_3", "rule:white:inWhiteList(\"email\",\"fingerprint\",\"card_token\",\"bin\",\"ip\")->accept;rule:black:inBlackList(\"email\",\"fingerprint\",\"card_token\",\"ip\")->decline;rule:highirsk_geo:in(countryBy(\"country_bank\"),\"IRN\",\"IRQ\",\"YEM\",\"PSE\",\"MMR\",\"SYR\")->decline;rule:cards_email_count_3:unique(\"email\",\"card_token\",1440)>3->decline;rule:cards_device_count_4:unique(\"fingerprint\",\"card_token\",1440)>3 AND not in(countryBy(\"country_bank\"),\"ARM\",\"AZE\",\"BLR\",\"GEO\",\"KAZ\",\"KGZ\",\"MDA\",\"TJK\",\"TKM\",\"UKR\",\"UZB\")->decline;rule:count5:count(\"card_token\",1440,\"party_id\")>4 AND not in(countryBy(\"country_bank\"),\"ARM\",\"AZE\",\"BLR\",\"GEO\",\"KAZ\",\"KGZ\",\"MDA\",\"TJK\",\"TKM\",\"UKR\",\"UZB\")->decline;"));
        ValidateTemplateResponse validateTemplateResponse = paymentServiceHandler.validateCompilationTemplate(list);
        assertNotNull(validateTemplateResponse.getErrors());
        assertTrue(validateTemplateResponse.getErrors().isEmpty());
    }

    @Test
    void validateCompilationTemplateErrorList() throws TException {
        ArrayList<Template> list = new ArrayList<>();
        list.add(createTemplate("test_1", "rule: inBlackList(\"email\")-> notify;"));
        list.add(createTemplate("test_2", "rule: inWhiteList(\"email\")-> notify;"));
        String errorTemplId = "test_3";
        list.add(createTemplate(errorTemplId, "rule:::inWhiteList(\"email\",\"fingerprint\",\"card_token\",\"bin\",\"ip\")->accept;rule:black:inBlackList(\"email\",\"fingerprint\",\"card_token\",\"ip\")->decline;rule:highirsk_geo:in(countryBy(\"country_bank\"),\"IRN\",\"IRQ\",\"YEM\",\"PSE\",\"MMR\",\"SYR\")->decline;rule:cards_email_count_3:unique(\"email\",\"card_token\",1440)>3->decline;rule:cards_device_count_4:unique(\"fingerprint\",\"card_token\",1440)>3 AND not in(countryBy(\"country_bank\"),\"ARM\",\"AZE\",\"BLR\",\"GEO\",\"KAZ\",\"KGZ\",\"MDA\",\"TJK\",\"TKM\",\"UKR\",\"UZB\")->decline;rule:count5:count(\"card_token\",1440,\"party_id\")>4 AND not in(countryBy(\"country_bank\"),\"ARM\",\"AZE\",\"BLR\",\"GEO\",\"KAZ\",\"KGZ\",\"MDA\",\"TJK\",\"TKM\",\"UKR\",\"UZB\")->decline;"));
        ValidateTemplateResponse validateTemplateResponse = paymentServiceHandler.validateCompilationTemplate(list);
        assertNotNull(validateTemplateResponse.getErrors());
        assertEquals(errorTemplId, validateTemplateResponse.getErrors().get(0).id);
    }

    private Template createTemplate(String id, String template) {
        return new Template()
                .setTemplate(template.getBytes())
                .setId(id);
    }

}