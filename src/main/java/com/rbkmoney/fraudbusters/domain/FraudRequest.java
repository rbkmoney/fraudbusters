package com.rbkmoney.fraudbusters.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.rbkmoney.fraudbusters.fraud.model.PaymentModel;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FraudRequest {

    private PaymentModel paymentModel;
    private Metadata metadata;

}
