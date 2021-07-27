package com.rbkmoney.fraudbusters.service;

import com.rbkmoney.damsel.fraudbusters.HistoricalTransactionCheck;
import com.rbkmoney.damsel.fraudbusters.Payment;
import com.rbkmoney.damsel.fraudbusters.Template;
import com.rbkmoney.fraudbusters.converter.PaymentToPaymentModelConverter;
import com.rbkmoney.fraudbusters.util.CheckResultFactory;
import com.rbkmoney.fraudbusters.domain.CheckedPayment;
import com.rbkmoney.fraudbusters.exception.InvalidTemplateException;
import com.rbkmoney.fraudbusters.fraud.FraudContextParser;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.fraud.payment.validator.PaymentTemplateValidator;
import com.rbkmoney.fraudbusters.repository.Repository;
import com.rbkmoney.fraudbusters.service.dto.FilterDto;
import com.rbkmoney.fraudbusters.service.dto.HistoricalPaymentsDto;
import com.rbkmoney.fraudbusters.util.CompositeIdUtil;
import com.rbkmoney.fraudo.FraudoPaymentParser;
import com.rbkmoney.fraudo.model.ResultModel;
import com.rbkmoney.fraudo.visitor.TemplateVisitor;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoricalDataServiceImpl implements HistoricalDataService {

    private final FraudContextParser<FraudoPaymentParser.ParseContext> paymentContextParser;
    private final PaymentTemplateValidator paymentTemplateValidator;
    private final TemplateVisitor<PaymentModel, ResultModel> paymentRuleVisitor;
    private final PaymentToPaymentModelConverter paymentModelConverter;
    private final CheckResultFactory checkResultFactory;
    private final Repository<CheckedPayment> paymentRepository;

    @Override
    public HistoricalPaymentsDto getPayments(FilterDto filter) {
        List<CheckedPayment> payments = paymentRepository.getByFilter(filter);
        String lastId = buildLastId(filter.getSize(), payments);
        return HistoricalPaymentsDto.builder()
                .payments(payments)
                .lastId(lastId)
                .build();
    }

    @Override
    public Set<HistoricalTransactionCheck> applySingleRule(Template template, Set<Payment> transactions) {
        final String templateString = new String(template.getTemplate(), StandardCharsets.UTF_8);
        validateTemplate(templateString);
        final FraudoPaymentParser.ParseContext parseContext = paymentContextParser.parse(templateString);
        return transactions.stream()
                .map(payment -> checkTransaction(payment, templateString, parseContext))
                .collect(Collectors.toSet());
    }

    private HistoricalTransactionCheck checkTransaction(
            Payment payment,
            String templateString,
            FraudoPaymentParser.ParseContext parseContext
    ) {
        final PaymentModel paymentModel = paymentModelConverter.convert(payment);
        final ResultModel resultModel = paymentRuleVisitor.visit(parseContext, paymentModel);
        final HistoricalTransactionCheck check = new HistoricalTransactionCheck();
        check.setTransaction(payment);
        check.setCheckResult(checkResultFactory.createCheckResult(templateString, resultModel));
        return check;
    }

    private void validateTemplate(String templateString) {
        List<String> validationErrors = paymentTemplateValidator.validate(templateString);
        if (!CollectionUtils.isEmpty(validationErrors)) {
            throw new InvalidTemplateException(templateString, validationErrors);
        }
    }

    @Nullable
    private String buildLastId(Long filterSize, List<CheckedPayment> payments) {
        if (payments.size() == filterSize) {
            CheckedPayment lastPayment = payments.get(payments.size() - 1);
            return CompositeIdUtil.create(lastPayment.getId(), lastPayment.getPaymentStatus());
        }
        return null;
    }
}
