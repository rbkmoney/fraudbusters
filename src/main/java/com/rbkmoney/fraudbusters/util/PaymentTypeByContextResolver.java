package com.rbkmoney.fraudbusters.util;

import com.rbkmoney.damsel.domain.BankCard;
import com.rbkmoney.damsel.domain.Payer;
import com.rbkmoney.mamsel.TokenProviderUtil;
import org.springframework.stereotype.Component;

@Component
public class PaymentTypeByContextResolver {

    public boolean isRecurrent(Payer payer) {
        return payer.isSetRecurrent() || payer.isSetCustomer();
    }

    public boolean isMobile(BankCard bankCard) {
        return TokenProviderUtil.isSetTokenProvider(bankCard);
    }

}
