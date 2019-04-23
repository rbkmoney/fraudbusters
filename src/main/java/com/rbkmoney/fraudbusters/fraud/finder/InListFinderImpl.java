package com.rbkmoney.fraudbusters.fraud.finder;

import com.rbkmoney.damsel.wb_list.ListType;
import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.damsel.wb_list.WbListServiceSrv;
import com.rbkmoney.fraudbusters.exception.RuleFunctionException;
import com.rbkmoney.fraudo.constant.CheckedField;
import com.rbkmoney.fraudo.finder.InListFinder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class InListFinderImpl implements InListFinder {

    private final WbListServiceSrv.Iface wbListServiceSrv;
    private final ListType listType;

    @Override
    public Boolean findInList(String partyId, String shopId, CheckedField field, String value) {
        try {
            Row row = createRow(partyId, shopId, field, value);
            return wbListServiceSrv.isExist(row);
        } catch (Exception e) {
            log.warn("InListFinderImpl error when findInList e: ", e);
            throw new RuleFunctionException(e);
        }
    }

    private Row createRow(String partyId, String shopId, CheckedField field, String value) {
        Row row = new Row();
        row.setPartyId(partyId);
        row.setShopId(shopId);
        row.setListType(listType);
        row.setListName(field.name());
        row.setValue(value);
        return row;
    }

    @Override
    public Boolean findInList(String partyId, String shopId, List<CheckedField> fields, List<String> value) {
        try {
            for (int i = 0; i < fields.size(); i++) {
                Boolean inList = findInList(partyId, shopId, fields.get(i), value.get(i));
                if (inList) {
                    return inList;
                }
            }
        } catch (Exception e) {
            log.warn("InListFinderImpl error when findInList e: ", e);
            throw new RuleFunctionException(e);
        }
        return false;
    }

}
