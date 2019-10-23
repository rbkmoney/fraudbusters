package com.rbkmoney.fraudbusters.fraud.finder;

import com.rbkmoney.damsel.wb_list.ListType;
import com.rbkmoney.damsel.wb_list.Row;
import com.rbkmoney.damsel.wb_list.WbListServiceSrv;
import com.rbkmoney.fraudbusters.exception.RuleFunctionException;
import com.rbkmoney.fraudbusters.service.MetricService;
import com.rbkmoney.fraudo.constant.CheckedField;
import com.rbkmoney.fraudo.finder.InListFinder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class InListFinderImpl implements InListFinder {

    private static final String IN_LIST = "inList";
    private static final String IS_ANY_EXIST = "isAnyExist";

    @Value("${metrics.count.functions.inList}")
    private boolean isEnableMetric;

    private final WbListServiceSrv.Iface wbListServiceSrv;
    private final ListType listType;
    private final MetricService metricService;

    @Override
    public Boolean findInList(String partyId, String shopId, CheckedField field, String value) {
        try {
            if (!StringUtils.isEmpty(value)) {
                Row row = createRow(partyId, shopId, field, value);
                return metricService.invokeWithMetrics(isEnableMetric, IN_LIST + listType.name(), wbListServiceSrv::isExist, row);
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
    public Boolean findInList(String partyId, String shopId, List<CheckedField> fields, List<String> values) {
        try {
            List<Row> rows = new ArrayList<>();
            for (int i = 0; i < fields.size(); i++) {
                if (!StringUtils.isEmpty(values.get(i))) {
                    Row row = createRow(partyId, shopId, fields.get(i), values.get(i));
                    rows.add(row);
                }
            }
            return metricService.invokeWithMetrics(isEnableMetric, IS_ANY_EXIST, wbListServiceSrv::isAnyExist, rows);
        } catch (Exception e) {
            log.warn("InListFinderImpl error when findInList e: ", e);
            throw new RuleFunctionException(e);
        }
    }

}
