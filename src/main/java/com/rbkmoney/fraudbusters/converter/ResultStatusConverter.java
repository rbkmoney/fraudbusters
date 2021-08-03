package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.damsel.fraudbusters.Accept;
import com.rbkmoney.damsel.fraudbusters.AcceptAndNotify;
import com.rbkmoney.damsel.fraudbusters.Decline;
import com.rbkmoney.damsel.fraudbusters.DeclineAndNotify;
import com.rbkmoney.damsel.fraudbusters.HighRisk;
import com.rbkmoney.damsel.fraudbusters.Normal;
import com.rbkmoney.damsel.fraudbusters.Notify;
import com.rbkmoney.damsel.fraudbusters.ThreeDs;
import com.rbkmoney.fraudo.constant.ResultStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ResultStatusConverter implements Converter<ResultStatus, com.rbkmoney.damsel.fraudbusters.ResultStatus> {

    private static final String UNKNOWN_VALUE = "Unknown ResultStatus";

    @Override
    public com.rbkmoney.damsel.fraudbusters.ResultStatus convert(ResultStatus resultStatus) {
        var status = new com.rbkmoney.damsel.fraudbusters.ResultStatus();
        switch (resultStatus) {
            case ACCEPT: {
                status.setAccept(new Accept());
                break;
            }
            case ACCEPT_AND_NOTIFY: {
                status.setAcceptAndNotify(new AcceptAndNotify());
                break;
            }
            case THREE_DS: {
                status.setThreeDs(new ThreeDs());
                break;
            }
            case DECLINE: {
                status.setDecline(new Decline());
                break;
            }
            case DECLINE_AND_NOTIFY: {
                status.setDeclineAndNotify(new DeclineAndNotify());
                break;
            }
            case HIGH_RISK: {
                status.setHighRisk(new HighRisk());
                break;
            }
            case NORMAL: {
                status.setNormal(new Normal());
                break;
            }
            case NOTIFY: {
                status.setNotify(new Notify());
                break;
            }
            default: {
                throw new IllegalArgumentException(UNKNOWN_VALUE);
            }
        }
        return status;
    }

}
