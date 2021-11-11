package com.rbkmoney.fraudbusters.fraud.payment.resolver;

import com.rbkmoney.fraudbusters.exception.RuleFunctionException;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.fraud.pool.CardTokenPool;
import com.rbkmoney.fraudbusters.util.ConditionTemplateFactory;
import com.rbkmoney.fraudo.model.TrustCondition;
import com.rbkmoney.fraudo.payment.resolver.CustomerTypeResolver;
import com.rbkmoney.trusted.tokens.TrustedTokensSrv;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerTypeResolverImpl implements CustomerTypeResolver<PaymentModel> {

    private final CardTokenPool cardTokenPool;

    private final TrustedTokensSrv.Iface trustedTokensSrv;

    private final ConditionTemplateFactory conditionTemplateFactory;

    @Override
    public Boolean isTrusted(PaymentModel paymentModel) {
        return cardTokenPool.isExist(paymentModel.getCardToken());
    }

    @Override
    public Boolean isTrusted(PaymentModel model, String templateName) {
        try {
            return trustedTokensSrv.isTokenTrustedByConditionTemplateName(model.getCardToken(), templateName);
        } catch (TException e) {
            throw new RuleFunctionException(e);
        }
    }

    @Override
    public Boolean isTrusted(PaymentModel model, List<TrustCondition> paymentsConditions,
                             List<TrustCondition> withdrawalsConditions) {
        try {
            return trustedTokensSrv.isTokenTrusted(
                    model.getCardToken(),
                    conditionTemplateFactory.createConditionTemplate(paymentsConditions, withdrawalsConditions)
            );
        } catch (TException e) {
            throw new RuleFunctionException(e);
        }
    }

}
