package com.rbkmoney.fraudbusters.fraud.payment.finder;

import com.rbkmoney.damsel.wb_list.*;
import com.rbkmoney.fraudbusters.aspect.BasicMetric;
import com.rbkmoney.fraudbusters.exception.RuleFunctionException;
import com.rbkmoney.fraudbusters.fraud.constant.PaymentCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import com.rbkmoney.fraudbusters.fraud.payment.resolver.DBPaymentFieldResolver;
import com.rbkmoney.fraudbusters.repository.impl.PaymentRepository;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import com.rbkmoney.fraudo.finder.InListFinder;
import com.rbkmoney.fraudo.model.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class PaymentInListFinderImpl implements InListFinder<PaymentModel, PaymentCheckedField> {

    private final WbListServiceSrv.Iface wbListServiceSrv;
    private final DBPaymentFieldResolver dbPaymentFieldResolver;
    private final PaymentRepository analyticRepository;

    private static final int CURRENT_ONE = 1;

    @Override
    @BasicMetric("findInBlackList")
    public Boolean findInBlackList(List<Pair<PaymentCheckedField, String>> fields, PaymentModel model) {
        return checkInList(fields, model, ListType.black);
    }

    @Override
    @BasicMetric("findInWhiteList")
    public Boolean findInWhiteList(List<Pair<PaymentCheckedField, String>> fields, PaymentModel model) {
        return checkInList(fields, model, ListType.white);
    }

    @Override
    @BasicMetric("findInGreyList")
    public Boolean findInGreyList(List<Pair<PaymentCheckedField, String>> fields, PaymentModel model) {
        try {
            return fields.stream()
                    .anyMatch(entry ->
                            !StringUtils.isEmpty(entry.getSecond())
                                    && findInList(model.getPartyId(), model.getShopId(), entry.getFirst(), entry.getSecond()));
        } catch (Exception e) {
            log.warn("InListFinderImpl error when findInList e: ", e);
            throw new RuleFunctionException(e);
        }
    }

    public Boolean findInList(String partyId, String shopId, PaymentCheckedField field, String value) {
        try {
            if (!StringUtils.isEmpty(value)) {
                Row row = createRow(ListType.grey, partyId, shopId, field, value);
                Result result = wbListServiceSrv.getRowInfo(row);
                if (result.getRowInfo() != null && result.getRowInfo().isSetCountInfo()) {
                    String resolveField = dbPaymentFieldResolver.resolve(field);
                    return сountLessThanWbList(value, result, resolveField);
                }
            }
            return false;
        } catch (Exception e) {
            log.warn("InListFinderImpl error when findInList e: ", e);
            throw new RuleFunctionException(e);
        }
    }

    @NotNull
    private Boolean сountLessThanWbList(String value, Result result, String resolveField) {
        RowInfo rowInfo = result.getRowInfo();
        String startCountTime = rowInfo.getCountInfo().getStartCountTime();
        String ttl = rowInfo.getCountInfo().getTimeToLive();
        Long to = TimestampUtil.generateTimestampWithParse(ttl);
        Long from = TimestampUtil.generateTimestampWithParse(startCountTime);
        if (Instant.now().getEpochSecond() > to || from >= to) {
            return false;
        }
        int currentCount = analyticRepository.countOperationByField(resolveField, value, from, to);
        return currentCount + CURRENT_ONE <= rowInfo.getCountInfo().getCount();
    }

    @Override
    @BasicMetric("findInNamingList")
    public Boolean findInList(String name, List<Pair<PaymentCheckedField, String>> fields, PaymentModel model) {
        return checkInList(fields, model, ListType.naming);
    }

    @NotNull
    private Boolean checkInList(List<Pair<PaymentCheckedField, String>> fields, PaymentModel model, ListType white) {
        try {
            String partyId = model.getPartyId();
            String shopId = model.getShopId();
            List<Row> rows = fields.stream()
                    .filter(entry -> entry.getFirst() != null && !StringUtils.isEmpty(entry.getSecond()))
                    .map(entry -> createRow(white, partyId, shopId, entry.getFirst(), entry.getSecond()))
                    .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(rows)) {
                return wbListServiceSrv.isAnyExist(rows);
            }
            return false;
        } catch (Exception e) {
            log.warn("InListFinderImpl error when findInList e: ", e);
            throw new RuleFunctionException(e);
        }
    }

    private Row createRow(ListType listType, String partyId, String shopId, PaymentCheckedField field, String value) {
        return new Row()
                .setId(IdInfo.payment_id(new PaymentId()
                        .setPartyId(partyId)
                        .setShopId(shopId)))
                .setListType(listType)
                .setListName(field.name())
                .setValue(value);
    }
}
