package com.rbkmoney.fraudbusters.fraud.localstorage;

import com.rbkmoney.damsel.fraudbusters.PaymentStatus;
import com.rbkmoney.fraudbusters.domain.CheckedPayment;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.FieldModel;
import com.rbkmoney.fraudbusters.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocalResultStorageRepository implements PaymentRepository {

    private final LocalResultStorage localStorage;

    @Override
    public Integer countOperationByField(String fieldName, String value, Long from, Long to) {
        List<CheckedPayment> checkedPayments = localStorage.get();
        int count = (int) checkedPayments.stream()
                .filter(checkedPayment -> checkedPayment.getEventTime() >= from
                        && checkedPayment.getEventTime() <= to
                        && checkValueFields(fieldName, value, checkedPayment))
                .count();
        log.debug("LocalResultStorageRepository countOperationByField: {}", count);
        return count;
    }

    @Override
    public Integer countOperationByFieldWithGroupBy(String fieldName, String value, Long from, Long to, List<FieldModel> fieldModels) {
        List<CheckedPayment> checkedPayments = localStorage.get();
        int count = (int) checkedPayments.stream()
                .filter(checkedPayment -> checkValueFields(from, to, fieldModels, checkedPayment))
                .count();
        log.debug("LocalResultStorageRepository countOperationByFieldWithGroupBy: {}", count);
        return count;
    }

    @Override
    public Long sumOperationByFieldWithGroupBy(String fieldName, String value, Long from, Long to, List<FieldModel> fieldModels) {
        List<CheckedPayment> checkedPayments = localStorage.get();
        long sum = checkedPayments.stream()
                .filter(checkedPayment -> checkValueFields(from, to, fieldModels, checkedPayment))
                .mapToLong(CheckedPayment::getAmount)
                .sum();
        log.debug("LocalResultStorageRepository sumOperationByFieldWithGroupBy: {}", sum);
        return sum;
    }

    @Override
    public Integer uniqCountOperation(String fieldNameBy, String value, String fieldNameCount, Long from, Long to) {
        List<CheckedPayment> checkedPayments = localStorage.get();
        long count = checkedPayments.stream()
                .filter(checkedPayment -> checkedPayment.getEventTime() >= from
                        && checkedPayment.getEventTime() <= to
                        && checkValueFields(fieldNameBy, value, checkedPayment))
                .map(checkedPayment -> valueByName(fieldNameCount, checkedPayment))
                .distinct()
                .count();
        log.debug("LocalResultStorageRepository uniqCountOperation: {}", count);
        return (int) count;
    }

    @Override
    public Integer uniqCountOperationWithGroupBy(String fieldNameBy, String value, String fieldNameCount, Long from, Long to, List<FieldModel> fieldModels) {
        List<CheckedPayment> checkedPayments = localStorage.get();
        long count = checkedPayments.stream()
                .filter(checkedPayment -> checkValueFields(from, to, fieldModels, checkedPayment))
                .map(checkedPayment -> valueByName(fieldNameCount, checkedPayment))
                .distinct()
                .count();
        log.debug("LocalResultStorageRepository uniqCountOperation: {}", count);
        return (int) count;
    }

    private boolean checkValueFields(Long from, Long to, List<FieldModel> fieldModels, CheckedPayment checkedPayment) {
        return checkedPayment.getEventTime() >= from
                && checkedPayment.getEventTime() <= to
                && fieldModels.stream()
                .allMatch(fieldModel -> checkValueFields(fieldModel.getName(), fieldModel.getValue(), checkedPayment));
    }

    @Override
    public Integer countOperationSuccessWithGroupBy(String fieldName, String value, Long from, Long to, List<FieldModel> fieldModels) {
        List<CheckedPayment> checkedPayments = localStorage.get();
        return (int) checkedPayments.stream()
                .filter(checkedPayment -> filterByStatusAndFields(from, to, fieldModels, checkedPayment, PaymentStatus.captured))
                .count();
    }

    @Override
    public Integer countOperationErrorWithGroupBy(String fieldName, String value, Long from, Long to, List<FieldModel> fieldModels, String errorCode) {
        List<CheckedPayment> checkedPayments = localStorage.get();
        return (int) checkedPayments.stream()
                .filter(checkedPayment -> filterByStatusAndFields(from, to, fieldModels, checkedPayment, PaymentStatus.failed)
                        && errorCode.equals(checkedPayment.getErrorCode()))
                .count();
    }

    @Override
    public Long sumOperationSuccessWithGroupBy(String fieldName, String value, Long from, Long to, List<FieldModel> fieldModels) {
        List<CheckedPayment> checkedPayments = localStorage.get();
        return checkedPayments.stream()
                .filter(checkedPayment -> filterByStatusAndFields(from, to, fieldModels, checkedPayment, PaymentStatus.captured))
                .mapToLong(CheckedPayment::getAmount)
                .sum();
    }

    @Override
    public Long sumOperationErrorWithGroupBy(String fieldName, String value, Long from, Long to, List<FieldModel> fieldModels, String errorCode) {
        List<CheckedPayment> checkedPayments = localStorage.get();
        return checkedPayments.stream()
                .filter(checkedPayment -> filterByStatusAndFields(from, to, fieldModels, checkedPayment, PaymentStatus.failed)
                        && errorCode.equals(checkedPayment.getErrorCode()))
                .mapToLong(CheckedPayment::getAmount)
                .sum();
    }

    private boolean checkValueFields(String fieldName, String value, CheckedPayment checkedPayment) {
        if (value != null) {
            return value.equals(valueByName(fieldName, checkedPayment));
        }
        return false;
    }

    private String valueByName(String fieldName, CheckedPayment checkedPayment) {
        PaymentCheckedField byValue = PaymentCheckedField.valueOf(fieldName);
        switch (byValue) {
            case IP:
                return checkedPayment.getIp();
            case BIN:
                return checkedPayment.getBin();
            case CARD_TOKEN:
                return checkedPayment.getCardToken();
            case PARTY_ID:
                return checkedPayment.getPartyId();
            case EMAIL:
                return checkedPayment.getEmail();
            case PAN:
                return checkedPayment.getMaskedPan();
            case FINGERPRINT:
                return checkedPayment.getFingerprint();
            case SHOP_ID:
                return checkedPayment.getShopId();
            case COUNTRY_BANK:
                return checkedPayment.getBankCountry();
            case CURRENCY:
                return checkedPayment.getCurrency();
            case COUNTRY_IP:
                return checkedPayment.getPaymentCountry();
            default:
                return null;
        }
    }

    private boolean filterByStatusAndFields(Long from, Long to, List<FieldModel> fieldModels, CheckedPayment checkedPayment, PaymentStatus paymentStatus) {
        return checkedPayment.getEventTime() >= from
                && checkedPayment.getEventTime() <= to
                && fieldModels.stream()
                .allMatch(fieldModel -> checkValueFields(fieldModel.getName(), fieldModel.getValue(), checkedPayment))
                && paymentStatus.name().equals(checkedPayment.getPaymentStatus());
    }

}
