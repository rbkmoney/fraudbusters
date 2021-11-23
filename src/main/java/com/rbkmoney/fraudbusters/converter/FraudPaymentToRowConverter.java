package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.damsel.fraudbusters.FraudPayment;
import com.rbkmoney.fraudbusters.domain.CheckedPayment;
import com.rbkmoney.fraudbusters.domain.FraudPaymentRow;
import com.rbkmoney.fraudbusters.exception.UnknownFraudPaymentException;
import com.rbkmoney.fraudbusters.service.PaymentInfoService;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class FraudPaymentToRowConverter implements Converter<FraudPayment, FraudPaymentRow> {

    private final PaymentInfoService paymentInfoService;

    @Override
    public FraudPaymentRow convert(FraudPayment fraudPayment) {
        LocalDateTime localDateTime = TimestampUtil.parseDate(fraudPayment.getEventTime());
        CheckedPayment checkedPayment =
                paymentInfoService.findPaymentByIdAndTimestamp(localDateTime.toLocalDate(), fraudPayment.getId());
        if (checkedPayment == null) {
            log.warn("Can't find payment for fraudPayment: {}", fraudPayment);
            throw new UnknownFraudPaymentException();
        }
        FraudPaymentRow payment = new FraudPaymentRow();
        payment.setTimestamp(checkedPayment.getTimestamp());
        payment.setEventTimeHour(checkedPayment.getEventTimeHour());
        payment.setEventTime(checkedPayment.getEventTime());
        payment.setId(checkedPayment.getId());
        payment.setEmail(checkedPayment.getEmail());
        payment.setIp(checkedPayment.getIp());
        payment.setFingerprint(checkedPayment.getFingerprint());
        payment.setPhone(checkedPayment.getPhone());
        payment.setBin(checkedPayment.getBin());
        payment.setMaskedPan(checkedPayment.getMaskedPan());
        payment.setCardToken(checkedPayment.getCardToken());
        payment.setCardCategory(checkedPayment.getCardCategory());
        payment.setPaymentSystem(checkedPayment.getPaymentSystem());
        payment.setPaymentTool(checkedPayment.getPaymentTool());
        payment.setTerminal(checkedPayment.getTerminal());
        payment.setProviderId(checkedPayment.getProviderId());
        payment.setBankCountry(checkedPayment.getBankCountry());
        payment.setPartyId(checkedPayment.getPartyId());
        payment.setShopId(checkedPayment.getShopId());
        payment.setAmount(checkedPayment.getAmount());
        payment.setCurrency(checkedPayment.getCurrency());
        payment.setPaymentStatus(checkedPayment.getPaymentStatus());
        payment.setErrorCode(checkedPayment.getErrorCode());
        payment.setErrorReason(checkedPayment.getErrorReason());
        payment.setPayerType(checkedPayment.getPayerType());
        payment.setTokenProvider(checkedPayment.getTokenProvider());
        payment.setCheckedTemplate(checkedPayment.getCheckedTemplate());
        payment.setCheckedRule(checkedPayment.getCheckedRule());
        payment.setResultStatus(checkedPayment.getResultStatus());
        payment.setCheckedResultsJson(checkedPayment.getCheckedResultsJson());
        payment.setMobile(checkedPayment.isMobile());
        payment.setRecurrent(checkedPayment.isRecurrent());
        return payment;
    }

}
