package com.rbkmoney.fraudbusters.fraud.payment.resolver;


import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudo.payment.resolver.PaymentTypeResolver;
import org.springframework.stereotype.Component;

@Component
public class PaymentTypeResolverImpl implements PaymentTypeResolver<PaymentModel> {

    @Override
    public Boolean isMobile(PaymentModel model) {
        return model.isMobile();
    }

    @Override
    public Boolean isRecurrent(PaymentModel model) {
        return model.isRecurrent();
    }

}
