package com.rbkmoney.fraudbusters.service.dto;

import lombok.Data;

@Data
public class CascadingTemplateDto {

    private String template;
    private String partyId;
    private String shopId;
    private Long timestamp;

}
