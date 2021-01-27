package com.rbkmoney.fraudbusters.fraud.payment.resolver;

import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.fraud.pool.CardTokenPool;
import com.rbkmoney.fraudo.payment.resolver.CustomerTypeResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerTypeResolverImpl implements CustomerTypeResolver<PaymentModel> {

    private final CardTokenPool cardTokenPool;

    @Override
    public Boolean isTrusted(PaymentModel paymentModel) {
        return cardTokenPool.isExist(paymentModel.getCardToken());
    }

}
