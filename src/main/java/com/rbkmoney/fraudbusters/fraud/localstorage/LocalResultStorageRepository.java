package com.rbkmoney.fraudbusters.fraud.localstorage;

import com.rbkmoney.fraudbusters.domain.CheckedPayment;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.FieldModel;
import com.rbkmoney.fraudbusters.repository.AggregationRepository;

import java.util.ArrayList;
import java.util.List;

public class LocalResultStorageRepository implements AggregationRepository {

    private final ThreadLocal<List<CheckedPayment>> localStorage = ThreadLocal.withInitial(ArrayList::new);

    @Override
    public Integer countOperationByField(String fieldName, String value, Long from, Long to) {
        List<CheckedPayment> checkedPayments = localStorage.get();
        return (int) checkedPayments.stream()
                .filter(checkedPayment -> checkedPayment.getEventTime() >= from
                        && checkedPayment.getEventTime() <= to
                        && filterByFields(fieldName, value, checkedPayment))
                .count();
    }

    @Override
    public Integer countOperationByFieldWithGroupBy(String fieldName, String value, Long from, Long to, List<FieldModel> fieldModels) {
        List<CheckedPayment> checkedPayments = localStorage.get();
        return (int) checkedPayments.stream()
                .filter(checkedPayment ->
                        checkedPayment.getEventTime() >= from
                                && checkedPayment.getEventTime() <= to
                                && fieldModels.stream()
                                .allMatch(fieldModel -> filterByFields(fieldModel.getName(), fieldModel.getValue(), checkedPayment)))
                .count();
    }

    @Override
    public Long sumOperationByFieldWithGroupBy(String fieldName, String value, Long from, Long to, List<FieldModel> fieldModels) {
        List<CheckedPayment> checkedPayments = localStorage.get();
        return checkedPayments.stream()
                .filter(checkedPayment ->
                        checkedPayment.getEventTime() >= from
                                && checkedPayment.getEventTime() <= to
                                && fieldModels.stream()
                                .allMatch(fieldModel -> filterByFields(fieldModel.getName(), fieldModel.getValue(), checkedPayment)))
                .mapToLong(CheckedPayment::getAmount)
                .sum();
    }

    @Override
    public Integer uniqCountOperation(String fieldNameBy, String value, String fieldNameCount, Long from, Long to) {
        List<CheckedPayment> checkedPayments = localStorage.get();
        return (int) checkedPayments.stream()
                .filter(checkedPayment -> checkedPayment.getEventTime() >= from
                        && checkedPayment.getEventTime() <= to
                        && filterByFields(fieldNameBy, value, checkedPayment))
                .map(checkedPayment -> valueByName(fieldNameCount, checkedPayment))
                .distinct()
                .count();
    }

    @Override
    public Integer uniqCountOperationWithGroupBy(String fieldNameBy, String value, String fieldNameCount, Long from, Long to, List<FieldModel> fieldModels) {
        List<CheckedPayment> checkedPayments = localStorage.get();
        return (int) checkedPayments.stream()
                .filter(checkedPayment -> checkedPayment.getEventTime() >= from
                        && checkedPayment.getEventTime() <= to
                        && fieldModels.stream()
                        .allMatch(fieldModel -> filterByFields(fieldModel.getName(), fieldModel.getValue(), checkedPayment)))
                .map(checkedPayment -> valueByName(fieldNameCount, checkedPayment))
                .distinct()
                .count();
    }

    private boolean filterByFields(String fieldName, String value, CheckedPayment checkedPayment) {
        if (value != null) {
            return value.equals(valueByName(fieldName, checkedPayment));
        }
        return false;
    }

    private String valueByName(String fieldName, CheckedPayment checkedPayment) {
        PaymentCheckedField byValue = PaymentCheckedField.getByValue(fieldName);
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

}
