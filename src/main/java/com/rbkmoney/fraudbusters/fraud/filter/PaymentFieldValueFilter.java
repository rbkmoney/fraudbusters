package com.rbkmoney.fraudbusters.fraud.filter;

import com.rbkmoney.fraudbusters.domain.CheckedPayment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentFieldValueFilter {

    private final PaymentFieldValueResolver paymentFieldValueResolver;

    public <T> boolean filter(String fieldName, T value, CheckedPayment checkedPayment) {
        Optional<String> valueByName = paymentFieldValueResolver.resolve(fieldName, checkedPayment);
        if (value != null && valueByName.isPresent()) {
            return value.equals(valueByName.get());
        }
        return false;
    }

}
