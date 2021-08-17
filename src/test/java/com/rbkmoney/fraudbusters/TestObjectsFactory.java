package com.rbkmoney.fraudbusters;

import com.rbkmoney.damsel.base.TimestampInterval;
import com.rbkmoney.damsel.base.TimestampIntervalBound;
import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.fraudbusters.ClientInfo;
import com.rbkmoney.damsel.fraudbusters.Error;
import com.rbkmoney.damsel.fraudbusters.*;
import com.rbkmoney.fraudbusters.constant.PaymentStatus;
import com.rbkmoney.fraudbusters.domain.CheckedPayment;
import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import com.rbkmoney.fraudbusters.domain.ConcreteResultModel;
import com.rbkmoney.fraudbusters.domain.Event;
import com.rbkmoney.fraudbusters.domain.FraudPaymentRow;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.service.dto.CascadingTemplateDto;
import com.rbkmoney.fraudbusters.util.BeanUtil;
import com.rbkmoney.fraudo.constant.ResultStatus;
import com.rbkmoney.fraudo.model.ResultModel;
import com.rbkmoney.fraudo.model.RuleResult;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.rbkmoney.fraudbusters.util.BeanUtil.PARTY_ID;
import static com.rbkmoney.fraudbusters.util.BeanUtil.SHOP_ID;

public abstract class TestObjectsFactory {

    public static CheckedPayment testCheckedPayment() {
        CheckedPayment checkedPayment = new CheckedPayment();
        checkedPayment.setAmount(randomLong());
        checkedPayment.setEmail(randomString());
        checkedPayment.setPaymentSystem(randomString());
        checkedPayment.setCurrency(randomString());
        checkedPayment.setPartyId(randomString());
        checkedPayment.setFingerprint(randomString());
        checkedPayment.setBankCountry(randomString());
        checkedPayment.setCardToken(randomString());
        checkedPayment.setIp(randomString());
        checkedPayment.setId(randomString());
        checkedPayment.setShopId(randomString());
        checkedPayment.setPaymentTool(randomString());
        checkedPayment.setPaymentCountry(randomString());
        checkedPayment.setTerminal(randomString());
        checkedPayment.setEventTime(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        checkedPayment.setPaymentStatus(PaymentStatus.processed.toString());
        checkedPayment.setProviderId(randomString());
        return checkedPayment;
    }

    public static List<CheckedPayment> testCheckedPayments(int n) {
        return IntStream.rangeClosed(1, n)
                .mapToObj(value -> testCheckedPayment())
                .collect(Collectors.toList());
    }

    public static Long randomLong() {
        return ThreadLocalRandom.current().nextLong(1000);
    }

    public static String randomString() {
        return UUID.randomUUID().toString();
    }

    public static Filter testFilter() {
        Filter filter = new Filter();
        String email = randomString();
        String cardToken = randomString();
        String status = randomString();
        String shopId = randomString();
        String partyId = randomString();
        String providerCountry = randomString();
        String fingerPrint = randomString();
        String terminal = randomString();
        String id = randomString();
        String maskedPan = randomString();
        String invoiceId = randomString();
        filter.setPartyId(partyId);
        filter.setEmail(email);
        filter.setCardToken(cardToken);
        filter.setFingerprint(fingerPrint);
        filter.setShopId(shopId);
        filter.setStatus(status);
        filter.setTerminal(terminal);
        filter.setProviderCountry(providerCountry);
        filter.setPaymentId(id);
        filter.setMaskedPan(maskedPan);
        filter.setInvoiceId(invoiceId);
        TimestampInterval timestampInterval = new TimestampInterval();
        TimestampIntervalBound lowerBound = new TimestampIntervalBound();
        String lowerBoundTime = LocalDateTime.now().toString();
        lowerBound.setBoundTime(lowerBoundTime);
        TimestampIntervalBound upperBound = new TimestampIntervalBound();
        String upperBoundTime = LocalDateTime.now().toString();
        upperBound.setBoundTime(upperBoundTime);
        timestampInterval.setLowerBound(lowerBound);
        timestampInterval.setUpperBound(upperBound);
        filter.setInterval(timestampInterval);
        return filter;
    }

    public static Sort testSort() {
        Sort sort = new Sort();
        sort.setField(randomString());
        sort.setOrder(SortOrder.DESC);
        return sort;
    }

    public static Page testPage() {
        Page page = new Page();
        String continuationId = randomString();
        Long size = randomLong();
        page.setSize(size);
        page.setContinuationId(continuationId);
        return page;
    }

    public static Refund testRefund() {
        ReferenceInfo referenceInfo = new ReferenceInfo();
        referenceInfo.setMerchantInfo(
                new MerchantInfo()
                        .setPartyId(TestObjectsFactory.randomString())
                        .setShopId(TestObjectsFactory.randomString()));
        PaymentTool paymentTool = new PaymentTool();
        BankCard bankCard = new BankCard();
        paymentTool.setBankCard(bankCard);
        bankCard.setToken(TestObjectsFactory.randomString());
        bankCard.setBin(TestObjectsFactory.randomString());
        bankCard.setLastDigits(TestObjectsFactory.randomString());
        bankCard.setPaymentSystem(new PaymentSystemRef().setId("visa"));
        return new Refund()
                .setId(TestObjectsFactory.randomString())
                .setPaymentId(TestObjectsFactory.randomString())
                .setEventTime(LocalDateTime.now().toString())
                .setClientInfo(new ClientInfo()
                        .setFingerprint(TestObjectsFactory.randomString())
                        .setIp(TestObjectsFactory.randomString())
                        .setEmail(TestObjectsFactory.randomString()))
                .setReferenceInfo(referenceInfo)
                .setError(new Error()
                        .setErrorCode(TestObjectsFactory.randomString())
                        .setErrorReason(TestObjectsFactory.randomString()))
                .setCost(new Cash()
                        .setAmount(TestObjectsFactory.randomLong())
                        .setCurrency(new CurrencyRef()
                                .setSymbolicCode(TestObjectsFactory.randomString())))
                .setStatus(RefundStatus.succeeded)
                .setPaymentTool(paymentTool)
                .setProviderInfo(new ProviderInfo()
                        .setProviderId(TestObjectsFactory.randomString())
                        .setCountry(TestObjectsFactory.randomString())
                        .setTerminalId(TestObjectsFactory.randomString()));
    }

    public static List<Refund> testRefunds(int n) {
        return IntStream.rangeClosed(1, n)
                .mapToObj(value -> testRefund())
                .collect(Collectors.toList());
    }

    public static Chargeback testChargeback() {
        ReferenceInfo referenceInfo = new ReferenceInfo();
        referenceInfo.setMerchantInfo(
                new MerchantInfo()
                        .setPartyId(TestObjectsFactory.randomString())
                        .setShopId(TestObjectsFactory.randomString()));
        PaymentTool paymentTool = new PaymentTool();
        BankCard bankCard = new BankCard();
        paymentTool.setBankCard(bankCard);
        bankCard.setToken(TestObjectsFactory.randomString());
        bankCard.setBin(TestObjectsFactory.randomString());
        bankCard.setLastDigits(TestObjectsFactory.randomString());
        bankCard.setPaymentSystem(new PaymentSystemRef().setId("visa"));
        return new Chargeback()
                .setId(TestObjectsFactory.randomString())
                .setPaymentId(TestObjectsFactory.randomString())
                .setEventTime(LocalDateTime.now().toString())
                .setClientInfo(new ClientInfo()
                        .setFingerprint(TestObjectsFactory.randomString())
                        .setIp(TestObjectsFactory.randomString())
                        .setEmail(TestObjectsFactory.randomString()))
                .setReferenceInfo(referenceInfo)
                .setCost(new Cash()
                        .setAmount(TestObjectsFactory.randomLong())
                        .setCurrency(new CurrencyRef()
                                .setSymbolicCode(TestObjectsFactory.randomString())))
                .setStatus(ChargebackStatus.accepted)
                .setPaymentTool(paymentTool)
                .setProviderInfo(new ProviderInfo()
                        .setProviderId(TestObjectsFactory.randomString())
                        .setCountry(TestObjectsFactory.randomString())
                        .setTerminalId(TestObjectsFactory.randomString()))
                .setChargebackCode(TestObjectsFactory.randomString())
                .setCategory(ChargebackCategory.fraud);
    }

    public static List<Chargeback> testChargebacks(int n) {
        return IntStream.rangeClosed(1, n)
                .mapToObj(value -> testChargeback())
                .collect(Collectors.toList());
    }

    public static Event testEvent() {
        Event event = new Event();
        event.setAmount(randomLong());
        event.setEmail(randomString());
        event.setMaskedPan(randomString());
        event.setCurrency(randomString());
        event.setPartyId(randomString());
        event.setFingerprint(randomString());
        event.setBankCountry(randomString());
        event.setCardToken(randomString());
        event.setIp(randomString());
        event.setBin(randomString());
        event.setShopId(randomString());
        event.setBankName(randomString());
        event.setResultStatus(ResultStatus.ACCEPT.name());
        event.setCheckedTemplate(randomString());
        event.setEventTime(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        event.setCheckedRule(randomString());
        event.setInvoiceId(randomString());
        event.setPaymentId(randomString());
        return event;
    }

    public static List<Event> testEvents(int n) {
        return IntStream.rangeClosed(1, n)
                .mapToObj(value -> testEvent())
                .collect(Collectors.toList());
    }

    public static FraudPaymentRow testFraudPaymentRow() {
        FraudPaymentRow fraudPaymentRow = new FraudPaymentRow();
        fraudPaymentRow.setAmount(randomLong());
        fraudPaymentRow.setEmail(randomString());
        fraudPaymentRow.setMaskedPan(randomString());
        fraudPaymentRow.setCurrency(randomString());
        fraudPaymentRow.setPartyId(randomString());
        fraudPaymentRow.setFingerprint(randomString());
        fraudPaymentRow.setBankCountry(randomString());
        fraudPaymentRow.setCardToken(randomString());
        fraudPaymentRow.setIp(randomString());
        fraudPaymentRow.setBin(randomString());
        fraudPaymentRow.setShopId(randomString());
        fraudPaymentRow.setPaymentStatus(PaymentStatus.processed.name());
        fraudPaymentRow.setEventTime(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        fraudPaymentRow.setComment(randomString());
        fraudPaymentRow.setType(randomString());
        fraudPaymentRow.setProviderId(randomString());
        fraudPaymentRow.setTerminal(randomString());
        fraudPaymentRow.setPaymentSystem(randomString());
        fraudPaymentRow.setPaymentTool(randomString());
        fraudPaymentRow.setErrorReason(randomString());
        fraudPaymentRow.setErrorCode(randomString());
        fraudPaymentRow.setId(randomString());
        return fraudPaymentRow;
    }

    public static List<FraudPaymentRow> testFraudPaymentRows(int n) {
        return IntStream.rangeClosed(1, n)
                .mapToObj(value -> testFraudPaymentRow())
                .collect(Collectors.toList());
    }


    public static CascadingTemplateDto createCascadingTemplateDto(String template) {
        CascadingTemplateDto dto = new CascadingTemplateDto();
        dto.setTimestamp(Instant.now().toEpochMilli());
        dto.setTemplate(template);
        dto.setShopId(SHOP_ID);
        dto.setPartyId(PARTY_ID);
        return dto;
    }

    public static EmulationRuleApplyRequest createCascadingEmulationRuleApplyRequest(String templateString,
                                                                                     String templateId,
                                                                                     Payment... payments) {
        Template template = new Template();
        template.setId(templateId);
        template.setTemplate(templateString.getBytes(StandardCharsets.UTF_8));
        TemplateReference templateReference = new TemplateReference();
        templateReference.setTemplateId(templateId);
        templateReference.setPartyId(UUID.randomUUID().toString());
        templateReference.setShopId(UUID.randomUUID().toString());
        CascasdingTemplateEmulation cascasdingTemplateEmulation = new CascasdingTemplateEmulation();
        cascasdingTemplateEmulation.setTemplate(template);
        cascasdingTemplateEmulation.setRef(templateReference);
        EmulationRule emulationRule = new EmulationRule();
        emulationRule.setCascadingEmulation(cascasdingTemplateEmulation);
        EmulationRuleApplyRequest request = new EmulationRuleApplyRequest();
        request.setEmulationRule(emulationRule);

        LinkedHashSet<Payment> linkedPayments = new LinkedHashSet<>();
        for (Payment payment : payments) {
            linkedPayments.add(payment);
        }
        request.setTransactions(linkedPayments);

        return request;
    }

    public static EmulationRuleApplyRequest createEmulationRuleApplyRequest(String templateString, String templateId) {
        return createEmulationRuleApplyRequest(templateString, templateId, Set.of(createPayment(), createPayment()));
    }

    public static EmulationRuleApplyRequest createEmulationRuleApplyRequest(String templateString,
                                                                            String templateId,
                                                                            Payment... payments) {
        Set<Payment> transactions = new LinkedHashSet<>();
        for (Payment payment : payments) {
            transactions.add(payment);
        }
        return createEmulationRuleApplyRequest(templateString, templateId, transactions);
    }

    public static EmulationRuleApplyRequest createEmulationRuleApplyRequest(String templateString,
                                                                            String templateId,
                                                                            Set<Payment> transactions) {
        Template template = new Template();
        template.setId(templateId);
        template.setTemplate(templateString.getBytes(StandardCharsets.UTF_8));
        OnlyTemplateEmulation onlyTemplateEmulation = new OnlyTemplateEmulation();
        onlyTemplateEmulation.setTemplate(template);
        EmulationRule emulationRule = new EmulationRule();
        emulationRule.setTemplateEmulation(onlyTemplateEmulation);
        EmulationRuleApplyRequest request = new EmulationRuleApplyRequest();
        request.setEmulationRule(emulationRule);
        request.setTransactions(transactions);

        return request;
    }

    public static Payment createPayment() {
        return createPayment(ThreadLocalRandom.current().nextLong());
    }

    public static Payment createPayment(Long amount) {
        return new Payment()
                .setId(UUID.randomUUID().toString())
                .setCost(new Cash()
                        .setAmount(amount)
                        .setCurrency(new CurrencyRef("RUB"))
                );
    }

    public static PaymentModel createPaymentModel() {
        return createPaymentModel(ThreadLocalRandom.current().nextLong(), Instant.now().toEpochMilli());
    }

    public static PaymentModel createPaymentModel(Long amount) {
        return createPaymentModel(amount, Instant.now().toEpochMilli());
    }

    public static PaymentModel createPaymentModel(Long amount, Long timestamp) {
        PaymentModel paymentModel = BeanUtil.createPaymentModel();
        paymentModel.setAmount(amount);
        paymentModel.setTimestamp(timestamp);
        return paymentModel;
    }

    public static ResultModel createResultModel(List<RuleResult> ruleResults) {
        ResultModel resultModel = new ResultModel();
        resultModel.setRuleResults(ruleResults);

        return resultModel;
    }

    public static RuleResult createRuleResult(String ruleChecked, ResultStatus resultStatus) {
        RuleResult ruleResult = new RuleResult();
        ruleResult.setRuleChecked(ruleChecked);
        ruleResult.setResultStatus(resultStatus);
        return ruleResult;
    }

    public static CheckedResultModel createCheckedResultModel(String template, ResultStatus resultStatus) {
        return createCheckedResultModel(template, null, resultStatus);
    }

    public static CheckedResultModel createCheckedResultModel(String template,
                                                              String ruleChecked,
                                                              ResultStatus status) {
        ConcreteResultModel concreteResultModel = new ConcreteResultModel();
        concreteResultModel.setRuleChecked(ruleChecked);
        concreteResultModel.setResultStatus(status);
        CheckedResultModel checkedResultModel = new CheckedResultModel();
        checkedResultModel.setCheckedTemplate(template);
        checkedResultModel.setResultModel(concreteResultModel);

        return checkedResultModel;
    }

    public static HistoricalTransactionCheck createHistoricalTransactionCheck(
            String template,
            Payment payment,
            com.rbkmoney.damsel.fraudbusters.ResultStatus resultStatus
    ) {
        return new HistoricalTransactionCheck()
                .setTransaction(payment)
                .setCheckResult(new CheckResult()
                        .setCheckedTemplate(template)
                        .setConcreteCheckResult(new ConcreteCheckResult()
                                .setRuleChecked("0")
                                .setResultStatus(resultStatus)
                        )
                );
    }
}
