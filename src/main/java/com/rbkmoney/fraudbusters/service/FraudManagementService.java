package com.rbkmoney.fraudbusters.service;

import com.rbkmoney.fraudbusters.client.FraudManagementClient;
import com.rbkmoney.fraudbusters.config.properties.DefaultTemplateProperties;
import com.rbkmoney.fraudbusters.repository.impl.FraudResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class FraudManagementService {

    private final FraudResultRepository repository;
    private final FraudManagementClient fraudManagementClient;
    private final DefaultTemplateProperties properties;

    public boolean isNewShop(String shopId) {
        Long to = Instant.now().toEpochMilli();
        Long from = Instant.now().minus(properties.getCountToCheckDays(), ChronoUnit.DAYS).toEpochMilli();
        return repository.countOperationByField("shopId", shopId, from, to) == 0;
    }

    public void createDefaultReference(String partyId, String shopId) {
        try {
            if (!fraudManagementClient.isExistReference(partyId, shopId)) {
                log.info("Got a new shop ('{}', '{}'). We should try to create a default template.", partyId, shopId);
                String refId = fraudManagementClient.createDefaultReference(partyId, shopId);
                log.info("Default template with id {} successfully created for new shop ('{}', '{}')", refId, partyId, shopId);
            }
        } catch (Exception e) {
            log.error("Could not interact with fraudbusters-management", e);
        }
    }
}
