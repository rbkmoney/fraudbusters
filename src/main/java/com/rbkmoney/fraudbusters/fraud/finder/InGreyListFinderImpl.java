package com.rbkmoney.fraudbusters.fraud.finder;

import com.rbkmoney.damsel.wb_list.ListType;
import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.damsel.wb_list.RowInfo;
import com.rbkmoney.damsel.wb_list.WbListServiceSrv;
import com.rbkmoney.fraudbusters.constant.EventField;
import com.rbkmoney.fraudbusters.exception.RuleFunctionException;
import com.rbkmoney.fraudbusters.fraud.resolver.FieldResolver;
import com.rbkmoney.fraudbusters.repository.EventRepository;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import com.rbkmoney.fraudo.constant.CheckedField;
import com.rbkmoney.fraudo.finder.InListFinder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class InGreyListFinderImpl implements InListFinder {

    private final WbListServiceSrv.Iface wbListServiceSrv;
    private final ListType listType;
    private final EventRepository eventRepository;
    private final FieldResolver fieldResolver;
    private static final int CURRENT_ONE = 1;

    @Override
    public Boolean findInList(String partyId, String shopId, CheckedField field, String value) {
        try {
            Row row = createRow(partyId, shopId, field, value);
            RowInfo rowInfo = wbListServiceSrv.getRowInfo(row);
            if (rowInfo.isSetCountInfo()) {
                String startCountTime = rowInfo.getCountInfo().getStartCountTime();
                String ttl = rowInfo.getCountInfo().getTimeToLive();
                EventField resolve = fieldResolver.resolve(field);
                Long to = TimestampUtil.generateTimestampWithParse(ttl);
                Long from = TimestampUtil.generateTimestampWithParse(startCountTime);
                if (Instant.now().getEpochSecond() > to || from >= to) {
                    return false;
                }
                int currentCount = eventRepository.countOperationByField(resolve, value, from, to);
                return currentCount + CURRENT_ONE <= rowInfo.getCountInfo().getCount();
            }
            return false;
        } catch (Exception e) {
            log.warn("InListFinderImpl error when findInList e: ", e);
            throw new RuleFunctionException(e);
        }
    }

    private Row createRow(String partyId, String shopId, CheckedField field, String value) {
        return new Row()
                .setPartyId(partyId)
                .setShopId(shopId)
                .setListType(listType)
                .setListName(field.name())
                .setValue(value);
    }

    @Override
    public Boolean findInList(String partyId, String shopId, List<CheckedField> fields, List<String> value) {
        try {
            for (int i = 0; i < fields.size(); i++) {
                if (findInList(partyId, shopId, fields.get(i), value.get(i))) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            log.warn("InListFinderImpl error when findInList e: ", e);
            throw new RuleFunctionException(e);
        }
    }

}
