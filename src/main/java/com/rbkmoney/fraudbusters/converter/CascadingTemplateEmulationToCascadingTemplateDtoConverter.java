package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.damsel.fraudbusters.CascasdingTemplateEmulation;
import com.rbkmoney.fraudbusters.service.dto.CascadingTemplateDto;
import com.rbkmoney.fraudbusters.util.TimestampUtil;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class CascadingTemplateEmulationToCascadingTemplateDtoConverter
        implements Converter<CascasdingTemplateEmulation, CascadingTemplateDto> {

    @Override
    public CascadingTemplateDto convert(CascasdingTemplateEmulation cascasdingTemplateEmulation) {
        CascadingTemplateDto dto = new CascadingTemplateDto();
        if (cascasdingTemplateEmulation.isSetRuleSetTimestamp()) {
            dto.setTimestamp(
                    TimestampUtil.generateTimestampWithParse(cascasdingTemplateEmulation.getRuleSetTimestamp()));
        }
        if (cascasdingTemplateEmulation.isSetTemplate()) {
            dto.setTemplate(
                    new String(cascasdingTemplateEmulation.getTemplate().getTemplate(), StandardCharsets.UTF_8));
        }
        if (cascasdingTemplateEmulation.isSetRef()) {
            dto.setPartyId(cascasdingTemplateEmulation.getRef().getPartyId());
            dto.setShopId(cascasdingTemplateEmulation.getRef().getShopId());
        }
        return dto;
    }

}
