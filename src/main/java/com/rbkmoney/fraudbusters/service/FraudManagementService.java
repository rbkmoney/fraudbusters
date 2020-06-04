package com.rbkmoney.fraudbusters.service;

import com.rbkmoney.fraudbusters.client.FraudManagementClient;
import com.rbkmoney.fraudbusters.repository.impl.FraudResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class FraudManagementService {

    private final FraudResultRepository repository;
    private final FraudManagementClient fraudManagementClient;

    public boolean isNewShop(String partyId, String shopId) {
        Long now = Instant.now().toEpochMilli();
        Long dayAgo = Instant.now().minus(1, ChronoUnit.DAYS).toEpochMilli();
        return repository.countOperationByField("shopId", shopId, dayAgo, now) == 0;
    }

    public void createDefaultReference(String partyId, String shopId) {
        if (!CollectionUtils.isEmpty(fraudManagementClient.getReferences(partyId, shopId))) {
            log.info("Got a new shop ('{}', '{}'). We should try to create a default template.", partyId, shopId);
            String refId = fraudManagementClient.createDefaultReference(partyId, shopId);
            log.info("Default template with id {} successfully created for new shop ('{}', '{}')", refId, partyId, shopId);

        }
    }
}
