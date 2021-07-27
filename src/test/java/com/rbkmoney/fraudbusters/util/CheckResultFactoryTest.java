package com.rbkmoney.fraudbusters.util;

import com.rbkmoney.damsel.fraudbusters.Accept;
import com.rbkmoney.damsel.fraudbusters.CheckResult;
import com.rbkmoney.fraudbusters.converter.ResultStatusConverter;
import com.rbkmoney.fraudo.constant.ResultStatus;
import com.rbkmoney.fraudo.model.ResultModel;
import com.rbkmoney.fraudo.model.RuleResult;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CheckResultFactoryTest {

    private final ResultStatusConverter resultStatusConverter = new ResultStatusConverter();
    private final CheckResultFactory factory = new CheckResultFactory(resultStatusConverter);

    private static final String TEMPLATE = "template string";
    private static final String RULE_CHECKED = "0";
    private static final String NOTIFICATION_RULE_CHECKED = "1";

    @Test
    void createCheckResultHasChecks() {
        RuleResult accept = createAcceptRuleResult();
        RuleResult notify = createNotificationRuleResult();
        ResultModel model = new ResultModel();
        model.setRuleResults(List.of(accept, notify));

        CheckResult actual = factory.createCheckResult(TEMPLATE, model);
        assertEquals(TEMPLATE, actual.getCheckedTemplate());
        assertEquals(
                com.rbkmoney.damsel.fraudbusters.ResultStatus.accept(new Accept()),
                actual.getConcreteCheckResult().getResultStatus()
        );
        assertEquals(RULE_CHECKED, actual.getConcreteCheckResult().getRuleChecked());
        assertEquals(
                Collections.singletonList(NOTIFICATION_RULE_CHECKED),
                actual.getConcreteCheckResult().getNotificationsRule()
        );
    }

    @Test
    void createCheckResultNoChecks() {
        ResultModel model = new ResultModel();
        model.setRuleResults(new ArrayList<>());

        CheckResult actual = factory.createCheckResult(TEMPLATE, model);
        assertEquals(TEMPLATE, actual.getCheckedTemplate());
        assertNull(actual.getConcreteCheckResult());
    }

    @Test
    void createCheckResultNotifyOnly() {
        RuleResult notify = createNotificationRuleResult();
        ResultModel model = new ResultModel();
        model.setRuleResults(Collections.singletonList(notify));

        CheckResult actual = factory.createCheckResult(TEMPLATE, model);
        assertEquals(TEMPLATE, actual.getCheckedTemplate());
        assertNull(actual.getConcreteCheckResult());
    }

    private RuleResult createAcceptRuleResult() {
        RuleResult accept = new RuleResult();
        accept.setResultStatus(ResultStatus.ACCEPT);
        accept.setRuleChecked(RULE_CHECKED);
        return accept;
    }

    private RuleResult createNotificationRuleResult() {
        RuleResult notify = new RuleResult();
        notify.setResultStatus(ResultStatus.NOTIFY);
        notify.setRuleChecked(NOTIFICATION_RULE_CHECKED);
        return notify;
    }
}
