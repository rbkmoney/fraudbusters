package com.rbkmoney.fraudbusters.client;

public interface FraudManagementClient {
    String createDefaultReference(String partyId, String shopId);
    boolean isExistReference(String partyId, String shopId);
}
