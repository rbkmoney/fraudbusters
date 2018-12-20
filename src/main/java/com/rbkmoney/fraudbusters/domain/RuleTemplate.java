package com.rbkmoney.fraudbusters.domain;

import com.rbkmoney.fraudbusters.constant.TemplateLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RuleTemplate {

    private String globalId;
    private String localId;
    private TemplateLevel lvl;
    private String template;

}
