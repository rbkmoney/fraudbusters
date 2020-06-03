package com.rbkmoney.fraudbusters.service;

import java.util.List;

public interface FraudManagementService {
    String createDefaultReference(String partyId, String shopId);
    List<PaymentReferenceModel> getReferences(String partyId, String shopId);
}
