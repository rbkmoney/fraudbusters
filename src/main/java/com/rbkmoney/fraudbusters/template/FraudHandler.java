package com.rbkmoney.fraudbusters.template;

import com.rbkmoney.fraudo.FraudoParser;
import com.rbkmoney.fraudo.aggregator.CountAggregator;
import com.rbkmoney.fraudo.aggregator.SumAggregator;
import com.rbkmoney.fraudo.aggregator.UniqueValueAggregator;
import com.rbkmoney.fraudo.constant.ResultStatus;
import com.rbkmoney.fraudo.factory.FastFraudVisitorFactory;
import com.rbkmoney.fraudo.factory.FraudVisitorFactory;
import com.rbkmoney.fraudo.finder.InListFinder;
import com.rbkmoney.fraudo.model.FraudModel;
import com.rbkmoney.fraudo.resolver.CountryResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FraudHandler {

    private final FraudVisitorFactory fraudVisitorFactory = new FastFraudVisitorFactory();

    private final CountAggregator countAggregator;
    private final SumAggregator sumAggregator;
    private final UniqueValueAggregator uniqueValueAggregator;
    private final CountryResolver countryResolver;
    private final InListFinder blackListFinder;
    private final InListFinder whiteListFinder;

    public ResultStatus handle(FraudoParser.ParseContext parseContext, FraudModel fraudModel) {
        return (ResultStatus) fraudVisitorFactory.createVisitor(fraudModel, countAggregator, sumAggregator,
                uniqueValueAggregator, countryResolver, blackListFinder, whiteListFinder)
                .visit(parseContext);
    }

}
