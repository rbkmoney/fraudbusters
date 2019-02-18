package com.rbkmoney.fraudbusters.stream;

import com.rbkmoney.fraudbusters.constant.TemplateLevel;
import com.rbkmoney.fraudbusters.template.pool.RuleTemplatePool;
import com.rbkmoney.fraudo.FraudoParser;
import com.rbkmoney.fraudo.aggregator.CountAggregator;
import com.rbkmoney.fraudo.aggregator.SumAggregator;
import com.rbkmoney.fraudo.aggregator.UniqueValueAggregator;
import com.rbkmoney.fraudo.constant.ResultStatus;
import com.rbkmoney.fraudo.factory.FastFraudVisitorFactory;
import com.rbkmoney.fraudo.factory.FraudVisitorFactory;
import com.rbkmoney.fraudo.finder.InListFinder;
import com.rbkmoney.fraudo.model.FraudModel;
import com.rbkmoney.fraudo.model.ResultModel;
import com.rbkmoney.fraudo.resolver.CountryResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TemplateVisitorImpl implements TemplateVisitor {

    public static final String SEPARATOR = "_";
    private final FraudVisitorFactory fraudVisitorFactory = new FastFraudVisitorFactory();
    private final CountAggregator countAggregator;
    private final SumAggregator sumAggregator;
    private final UniqueValueAggregator uniqueValueAggregator;
    private final CountryResolver countryResolver;
    private final InListFinder blackListFinder;
    private final InListFinder whiteListFinder;
    private final RuleTemplatePool templatePool;

    @Override
    public ResultModel visit(FraudModel fraudModel) {
        ResultModel resultModel = new ResultModel();
        resultModel.setResultStatus(ResultStatus.THREE_DS);
        return apply(fraudModel, TemplateLevel.GLOBAL.toString())
                .orElse(apply(fraudModel, fraudModel.getPartyId())
                        .orElse(apply(fraudModel, getShopId(fraudModel))
                                .orElse(resultModel)));
    }

    @NotNull
    private String getShopId(FraudModel fraudModel) {
        return fraudModel.getPartyId() + SEPARATOR + fraudModel.getShopId();
    }

    private Optional<ResultModel> apply(FraudModel fraudModel, String templateKey) {
        FraudoParser.ParseContext parseContext = templatePool.get(templateKey);
        if (parseContext != null) {
            ResultModel resultModel = (ResultModel) fraudVisitorFactory.createVisitor(fraudModel, countAggregator, sumAggregator,
                    uniqueValueAggregator, countryResolver, blackListFinder, whiteListFinder).visit(parseContext);
            if (!ResultStatus.NORMAL.equals(resultModel.getResultStatus())) {
                log.info("applyRules global resultModel: {}", resultModel);
                return Optional.of(resultModel);
            }
        }
        return Optional.empty();
    }

}
