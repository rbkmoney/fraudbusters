package com.rbkmoney.fraudbusters.fraud.finder;

import com.rbkmoney.damsel.wb_list.*;
import com.rbkmoney.fraudbusters.aspect.BasicMetric;
import com.rbkmoney.fraudbusters.exception.RuleFunctionException;
import com.rbkmoney.fraudbusters.fraud.constant.P2PCheckedField;
import com.rbkmoney.fraudbusters.fraud.model.P2PModel;
import com.rbkmoney.fraudbusters.fraud.resolver.DbP2pFieldResolver;
import com.rbkmoney.fraudbusters.repository.EventP2PRepository;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import com.rbkmoney.fraudo.finder.InListFinder;
import com.rbkmoney.fraudo.model.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class P2pInListFinderImpl implements InListFinder<P2PModel, P2PCheckedField> {

    private final WbListServiceSrv.Iface wbListServiceSrv;
    private final DbP2pFieldResolver dbP2pFieldResolver;
    private final EventP2PRepository eventP2PRepository;

    private static final int CURRENT_ONE = 1;

    @Override
    @BasicMetric("findInBlackList")
    public Boolean findInBlackList(List<Pair<P2PCheckedField, String>> fields, P2PModel model) {
        return checkInList(fields, model, ListType.black);
    }

    @Override
    @BasicMetric("findInWhiteList")
    public Boolean findInWhiteList(List<Pair<P2PCheckedField, String>> fields, P2PModel model) {
        return checkInList(fields, model, ListType.white);
    }

    @Override
    @BasicMetric("findInGreyList")
    public Boolean findInGreyList(List<Pair<P2PCheckedField, String>> fields, P2PModel model) {
        try {
            return fields.stream()
                    .anyMatch(entry ->
                            !StringUtils.isEmpty(entry.getSecond())
                                    && findInList(model.getIdentityId(), entry.getFirst(), entry.getSecond()));
        } catch (Exception e) {
            log.warn("InListFinderImpl error when findInList e: ", e);
            throw new RuleFunctionException(e);
        }
    }

    public Boolean findInList(String identityId, P2PCheckedField field, String value) {
        try {
            if (!StringUtils.isEmpty(value)) {
                Row row = createRow(ListType.grey, identityId, null, field, value);
                Result result = wbListServiceSrv.getRowInfo(row);
                if (result.getRowInfo() != null && result.getRowInfo().isSetCountInfo()) {
                    RowInfo rowInfo = result.getRowInfo();
                    String startCountTime = rowInfo.getCountInfo().getStartCountTime();
                    String ttl = rowInfo.getCountInfo().getTimeToLive();
                    String resolveField = dbP2pFieldResolver.resolve(field);
                    Long to = TimestampUtil.generateTimestampWithParse(ttl);
                    Long from = TimestampUtil.generateTimestampWithParse(startCountTime);
                    if (Instant.now().getEpochSecond() > to || from >= to) {
                        return false;
                    }
                    int currentCount = eventP2PRepository.countOperationByField(resolveField, value, from, to);
                    return currentCount + CURRENT_ONE <= rowInfo.getCountInfo().getCount();
                }
            }
            return false;
        } catch (Exception e) {
            log.warn("InListFinderImpl error when findInList e: ", e);
            throw new RuleFunctionException(e);
        }
    }

    @Override
    @BasicMetric("findInNamingList")
    public Boolean findInList(String name, List<Pair<P2PCheckedField, String>> fields, P2PModel model) {
        // TODO
        return null;
    }

    @NotNull
    private Boolean checkInList(List<Pair<P2PCheckedField, String>> fields, P2PModel model, ListType white) {
        try {
            String identityId = model.getIdentityId();
            List<Row> rows = fields.stream()
                    .map(entry -> createRow(white, identityId, null, entry.getFirst(), entry.getSecond()))
                    .collect(Collectors.toList());
            return wbListServiceSrv.isAnyExist(rows);
        } catch (Exception e) {
            log.warn("InListFinderImpl error when findInList e: ", e);
            throw new RuleFunctionException(e);
        }
    }

    private Row createRow(ListType listType, String partyId, String shopId, P2PCheckedField field, String value) {
        return new Row()
                .setPartyId(partyId)
                .setShopId(shopId)
                .setListType(listType)
                .setListName(field.name())
                .setValue(value);
    }
}
