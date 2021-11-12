package com.rbkmoney.fraudbusters.fraud.payment.resolver;

import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.fraud.pool.CardTokenPool;
import com.rbkmoney.fraudbusters.util.ConditionTemplateFactory;
import com.rbkmoney.fraudo.model.TrustCondition;
import com.rbkmoney.fraudo.payment.resolver.CustomerTypeResolver;
import com.rbkmoney.trusted.tokens.ConditionTemplate;
import com.rbkmoney.trusted.tokens.TrustedTokensSrv;
import org.apache.thrift.TException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.rbkmoney.fraudbusters.factory.TestObjectsFactory.createConditionTemplate;
import static com.rbkmoney.fraudbusters.factory.TestObjectsFactory.createTrustCondition;
import static com.rbkmoney.fraudbusters.util.BeanUtil.TOKEN;
import static com.rbkmoney.fraudbusters.util.BeanUtil.createPaymentModel;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerTypeResolverImplTest {

    private CustomerTypeResolver<PaymentModel> customerTypeResolver;

    @Mock
    private CardTokenPool cardTokenPool;

    @Mock
    private TrustedTokensSrv.Iface trustedTokensSrv;

    @Mock
    private ConditionTemplateFactory conditionTemplateFactory;

    @BeforeEach
    void setUp() {
        customerTypeResolver = new CustomerTypeResolverImpl(cardTokenPool, trustedTokensSrv, conditionTemplateFactory);
    }

    @Test
    void isTrustedTest() {
        when(cardTokenPool.isExist(anyString()))
                .thenReturn(false)
                .thenReturn(true);

        PaymentModel paymentModel = createPaymentModel();
        assertFalse(customerTypeResolver.isTrusted(paymentModel));
        assertTrue(customerTypeResolver.isTrusted(paymentModel));

        verify(cardTokenPool, times(2)).isExist(TOKEN);
    }

    @Test
    void isTrustedByConditionTemplateNameTest() throws TException {
        when(trustedTokensSrv.isTokenTrustedByConditionTemplateName(anyString(), anyString()))
                .thenReturn(false)
                .thenReturn(true);

        PaymentModel paymentModel = createPaymentModel();
        String templateName = "templateName";
        assertFalse(customerTypeResolver.isTrusted(paymentModel, templateName));
        assertTrue(customerTypeResolver.isTrusted(paymentModel, templateName));

        verify(trustedTokensSrv, times(2))
                .isTokenTrustedByConditionTemplateName(TOKEN, templateName);
    }

    @Test
    void isTrustedByConditionTemplateTest() throws TException {
        when(trustedTokensSrv.isTokenTrusted(anyString(), any(ConditionTemplate.class)))
                .thenReturn(false)
                .thenReturn(true);
        ConditionTemplate conditionTemplate = createConditionTemplate();
        when(conditionTemplateFactory.createConditionTemplate(anyList(), anyList()))
                .thenReturn(conditionTemplate);

        PaymentModel paymentModel = createPaymentModel();
        List<TrustCondition> paymentsConditions =
                List.of(createTrustCondition(1), createTrustCondition(null));
        List<TrustCondition> withdrawalsConditions =
                List.of(createTrustCondition(2), createTrustCondition(null));
        assertFalse(customerTypeResolver.isTrusted(paymentModel, paymentsConditions, withdrawalsConditions));
        assertTrue(customerTypeResolver.isTrusted(paymentModel, paymentsConditions, withdrawalsConditions));

        verify(conditionTemplateFactory, times(2))
                .createConditionTemplate(paymentsConditions, withdrawalsConditions);
        verify(trustedTokensSrv, times(2)).isTokenTrusted(TOKEN, conditionTemplate);

    }
}
