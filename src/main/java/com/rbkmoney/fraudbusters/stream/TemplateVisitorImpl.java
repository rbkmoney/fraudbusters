package com.rbkmoney.fraudbusters.stream;

import com.rbkmoney.fraudbusters.constant.TemplateLevel;
import com.rbkmoney.fraudbusters.domain.CheckedResultModel;
import com.rbkmoney.fraudbusters.template.pool.Pool;
import com.rbkmoney.fraudbusters.util.ReferenceKeyGenerator;
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

    private static final String RULE_NOT_CHECKED = "RULE_NOT_CHECKED";
    private final FraudVisitorFactory fraudVisitorFactory = new FastFraudVisitorFactory();
    private final CountAggregator countAggregator;
    private final SumAggregator sumAggregator;
    private final UniqueValueAggregator uniqueValueAggregator;
    private final CountryResolver countryResolver;
    private final InListFinder blackListFinder;
    private final InListFinder whiteListFinder;
    private final InListFinder greyListFinder;
    private final Pool<FraudoParser.ParseContext> templatePool;
    private final Pool<String> referencePoolImpl;

    @Override
    public CheckedResultModel visit(FraudModel fraudModel) {
        return apply(fraudModel, referencePoolImpl.get(TemplateLevel.GLOBAL.name()))
                .orElse(apply(fraudModel, referencePoolImpl.get(fraudModel.getPartyId()))
                        .orElse(apply(fraudModel, referencePoolImpl.get(ReferenceKeyGenerator.generateTemplateKey(fraudModel.getPartyId(), fraudModel.getShopId())))
                                .orElse(createDefaultResult())));
    }

    @NotNull
    private CheckedResultModel createDefaultResult() {
        ResultModel resultModel = new ResultModel();
        resultModel.setResultStatus(ResultStatus.THREE_DS);
        CheckedResultModel checkedResultModel = new CheckedResultModel();
        checkedResultModel.setResultModel(resultModel);
        checkedResultModel.setCheckedTemplate(RULE_NOT_CHECKED);
        return checkedResultModel;
    }

    private Optional<CheckedResultModel> apply(FraudModel fraudModel, String templateKey) {
        FraudoParser.ParseContext parseContext = templatePool.get(templateKey);
        if (parseContext != null) {
            ResultModel resultModel = (ResultModel) fraudVisitorFactory.createVisitor(fraudModel, countAggregator, sumAggregator,
                    uniqueValueAggregator, countryResolver, blackListFinder, greyListFinder, whiteListFinder).visit(parseContext);
            if (!ResultStatus.NORMAL.equals(resultModel.getResultStatus())) {
                log.info("applyRules resultModel: {}", resultModel);
                CheckedResultModel checkedResultModel = new CheckedResultModel();
                checkedResultModel.setResultModel(resultModel);
                checkedResultModel.setCheckedTemplate(templateKey);
                return Optional.of(checkedResultModel);
            }
        }
        return Optional.empty();
    }

}
