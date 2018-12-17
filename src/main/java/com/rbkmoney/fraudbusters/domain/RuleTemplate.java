package com.rbkmoney.fraudbusters.domain;

import com.rbkmoney.fraudbusters.constant.Level;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RuleTemplate {

    private String globalId;
    private String localId;
    private Level lvl;
    private String template;

}
