package com.rbkmoney.fraudbusters;

import com.rbkmoney.fraudbusters.config.dgraph.TemplateConfig;
import com.rbkmoney.fraudbusters.constant.DgraphPaymentUpsertConstants;
import com.rbkmoney.fraudbusters.data.VelocityTestData;
import com.rbkmoney.fraudbusters.domain.dgraph.*;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.junit.Before;
import org.junit.Test;

import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class VelocityTemplateTest {

    VelocityEngine velocityEngine;

    @Before
    public void before() {
        velocityEngine = new TemplateConfig().velocityEngine();
    }

    @Test
    public void generatePaymentUpsertQueryTest() {
        VelocityContext shortPaymentQueryContext = new VelocityContext();
        shortPaymentQueryContext.put("payment",
                createTestDgraphPayment(false, false, false, false));
        shortPaymentQueryContext.put("constants", new DgraphPaymentUpsertConstants());
        String firstQuery =
                build(velocityEngine.getTemplate("vm/upsert_global_payment_data_query.vm"), shortPaymentQueryContext);
        assertNotNull(firstQuery);
        assertEquals(VelocityTestData.TEST_SHORT_PAYMENT_UPSERT_QUERY, firstQuery);

        VelocityContext fullPaymentQueryContext = new VelocityContext();
        fullPaymentQueryContext.put("payment",
                createTestDgraphPayment(true, true, true, true));
        fullPaymentQueryContext.put("constants", new DgraphPaymentUpsertConstants());
        String secondQuery =
                build(velocityEngine.getTemplate("vm/upsert_global_payment_data_query.vm"), fullPaymentQueryContext);
        assertNotNull(secondQuery);
        assertEquals(VelocityTestData.TEST_FULL_PAYMENT_UPSERT_QUERY, secondQuery);
    }

    @Test
    public void generatePaymentIpsertTest() {
        VelocityContext paymentQueryContext = new VelocityContext();
        paymentQueryContext.put("payment",
                createTestDgraphPayment(false, false, false, false));
        paymentQueryContext.put("constants", new DgraphPaymentUpsertConstants());
        String firstInsertBlock =
                build(velocityEngine.getTemplate("vm/insert_payment_to_dgraph.vm"), paymentQueryContext);
        assertNotNull(firstInsertBlock);
        assertEquals(VelocityTestData.TEST_INSERT_PAYMENT_SHORT_BLOCK, firstInsertBlock);

        VelocityContext fullPaymentQueryContext = new VelocityContext();
        fullPaymentQueryContext.put("payment",
                createTestDgraphPayment(true, true, true, true));
        fullPaymentQueryContext.put("constants", new DgraphPaymentUpsertConstants());
        String secondInsertBlock =
                build(velocityEngine.getTemplate("vm/insert_payment_to_dgraph.vm"), fullPaymentQueryContext);
        assertNotNull(secondInsertBlock);
        assertEquals(VelocityTestData.TEST_INSERT_FULL_PAYMENT_BLOCK, secondInsertBlock);

    }

    private DgraphPayment createTestDgraphPayment(boolean ipExists,
                                                  boolean countryExists,
                                                  boolean fingerprintExists,
                                                  boolean emailExists) {
        DgraphPayment dgraphPayment = new DgraphPayment();
        dgraphPayment.setPaymentId("TestPayment");
        String partyId = "partyId-1";
        String shopId = "shopId-1";
        dgraphPayment.setPartyId(partyId);
        dgraphPayment.setShopId(shopId);
        DgraphPartyShop partyShop = new DgraphPartyShop();
        partyShop.setPartyId(partyId);
        partyShop.setShopId(shopId);
        dgraphPayment.setPartyShop(partyShop);

        dgraphPayment.setCreatedAt("2021-10-05T18:00:00");
        dgraphPayment.setAmount(1000L);
        dgraphPayment.setCurrency("RUB");
        dgraphPayment.setStatus("captured");
        dgraphPayment.setPaymentTool("tool");
        dgraphPayment.setTerminal("10001");
        dgraphPayment.setProviderId("21");
        dgraphPayment.setBankCountry("Russia");
        dgraphPayment.setPayerType("type-1");
        dgraphPayment.setTokenProvider("provider-1");
        dgraphPayment.setMobile(false);
        dgraphPayment.setRecurrent(false);
        dgraphPayment.setErrorReason(null);
        dgraphPayment.setErrorCode(null);
        dgraphPayment.setCheckedTemplate(null);
        dgraphPayment.setCheckedRule(null);
        dgraphPayment.setResultStatus(null);
        dgraphPayment.setCheckedResultsJson(null);

        DgraphBin dgraphBin = new DgraphBin();
        dgraphBin.setBin("000000");
        dgraphPayment.setDgraphBin(dgraphBin);
        DgraphToken dgraphToken = new DgraphToken();
        dgraphToken.setTokenId("token-1");
        dgraphToken.setMaskedPan("pan-1");
        dgraphPayment.setCardToken(dgraphToken);
        if (countryExists) {
            DgraphCountry country = new DgraphCountry();
            country.setCountryName("Russia");
            dgraphPayment.setCountry(country);
        }
        if (ipExists) {
            DgraphIp dgraphIp = new DgraphIp();
            dgraphIp.setIp("127.0.0.1");
            dgraphPayment.setDgraphIp(dgraphIp);
        }
        if (fingerprintExists) {
            DgraphFingerprint fingerprint = new DgraphFingerprint();
            fingerprint.setFingerprintData("fData");
            dgraphPayment.setFingerprint(fingerprint);
        }
        if (emailExists) {
            DgraphEmail dgraphEmail = new DgraphEmail();
            dgraphEmail.setUserEmail("1@1.ru");
            dgraphPayment.setContactEmail(dgraphEmail);
        }
        return dgraphPayment;
    }

    public String build(Template template, VelocityContext context) {
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }

}
