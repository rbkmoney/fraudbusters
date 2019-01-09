package com.rbkmoney.fraudbusters.fraud.aggragator;

import com.rbkmoney.fraudo.aggregator.SumAggregator;
import com.rbkmoney.fraudo.constant.CheckedField;
import com.rbkmoney.fraudo.model.FraudModel;

public class SumAggregatorImpl implements SumAggregator {

    @Override
    public Double sum(CheckedField checkedField, FraudModel fraudModel, Long aLong) {
        return null;
    }

    @Override
    public Double sumSuccess(CheckedField checkedField, FraudModel fraudModel, Long aLong) {
        return null;
    }

    @Override
    public Double sumError(CheckedField checkedField, FraudModel fraudModel, Long aLong, String s) {
        return null;
    }
}
