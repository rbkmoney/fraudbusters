package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.damsel.domain.BankCard;
import com.rbkmoney.damsel.domain.Cash;
import com.rbkmoney.damsel.domain.CurrencyRef;
import com.rbkmoney.damsel.domain.PaymentTool;
import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.fraudbusters.domain.Event;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import com.rbkmoney.fraudo.constant.ResultStatus;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventToHistoricalTransactionCheckConverter implements Converter<Event, HistoricalTransactionCheck> {

    public static final String EMPTY_FILED = "-";
    private final ResultStatusConverter resultStatusConverter;

    @Override
    public HistoricalTransactionCheck convert(Event event) {
        HistoricalTransactionCheck historicalTransactionCheck = new HistoricalTransactionCheck();
        Payment payment = convertPayment(event);
        historicalTransactionCheck.setTransaction(payment);
        CheckResult checkResult = convertCheckResult(event);
        historicalTransactionCheck.setCheckResult(checkResult);
        return historicalTransactionCheck;
    }

    @NotNull
    private CheckResult convertCheckResult(Event event) {
        CheckResult checkResult = new CheckResult();
        checkResult.setCheckedTemplate(event.getCheckedTemplate());
        ConcreteCheckResult concreteCheckResult = new ConcreteCheckResult();
        concreteCheckResult.setRuleChecked(event.getCheckedRule());
        ResultStatus resultStatus = ResultStatus.valueOf(event.getResultStatus());
        concreteCheckResult.setResultStatus(resultStatusConverter.convert(resultStatus));
        checkResult.setConcreteCheckResult(concreteCheckResult);
        return checkResult;
    }

    private Payment convertPayment(Event event) {
        ReferenceInfo referenceInfo = new ReferenceInfo();
        referenceInfo.setMerchantInfo(
                new MerchantInfo()
                        .setPartyId(event.getPartyId())
                        .setShopId(event.getShopId()));
        PaymentTool paymentTool = new PaymentTool();
        BankCard bankCard = new BankCard();
        paymentTool.setBankCard(bankCard);
        bankCard.setToken(event.getCardToken());
        bankCard.setBankName(event.getBankName());
        bankCard.setBin(event.getBin());
        bankCard.setLastDigits(event.getMaskedPan());
        return new Payment()
                .setId(event.getPaymentId())
                .setEventTime(TimestampUtil.getStringDate(event.getEventTime()))
                .setClientInfo(new ClientInfo()
                        .setFingerprint(event.getFingerprint())
                        .setIp(event.getIp())
                        .setEmail(event.getEmail()))
                .setReferenceInfo(referenceInfo)
                .setCost(new Cash()
                        .setAmount(event.getAmount())
                        .setCurrency(new CurrencyRef()
                                .setSymbolicCode(event.getCurrency())))
                .setPaymentTool(paymentTool)
                .setMobile(event.isMobile())
                .setRecurrent(event.isRecurrent())
                .setProviderInfo(new ProviderInfo()
                        .setProviderId(EMPTY_FILED)
                        .setCountry(EMPTY_FILED)
                        .setTerminalId(EMPTY_FILED))
                .setStatus(null);
    }
}
