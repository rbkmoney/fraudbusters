package com.rbkmoney.fraudbusters.client;

import com.rbkmoney.fraudbusters.client.model.PaymentReferenceModel;

import java.util.List;

public interface FraudManagementClient {
    String createDefaultReference(String partyId, String shopId);
    List<PaymentReferenceModel> getReferences(String partyId, String shopId);
}
