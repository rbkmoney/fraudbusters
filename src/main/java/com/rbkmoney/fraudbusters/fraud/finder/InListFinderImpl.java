package com.rbkmoney.fraudbusters.fraud.finder;

import com.rbkmoney.damsel.wb_list.WbListServiceSrv;
import com.rbkmoney.fraudbusters.constant.ListType;
import com.rbkmoney.fraudo.constant.CheckedField;
import com.rbkmoney.fraudo.finder.InListFinder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class InListFinderImpl implements InListFinder {

    private final WbListServiceSrv.Iface wbListServiceSrv;
    private final ListType listType;

    @Override
    public Boolean findInList(String partyId, String shopId, CheckedField field, String value) {
        try {
            return wbListServiceSrv.isExist(partyId, shopId, listType.getPrefix() + field.name(), value);
        } catch (Exception e) {
            log.error("Error when findInList e: ", e);
        }
        return false;
    }

}
