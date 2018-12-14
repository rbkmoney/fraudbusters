package com.rbkmoney.fraudbusters.domain;

import com.rbkmoney.fraudbusters.constant.Level;
import lombok.Data;

@Data
public class RuleTemplate {

    private String globalId;
    private String localId;
    private Level lvl;
    private String template;

}
