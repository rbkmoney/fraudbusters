package com.rbkmoney.fraudbusters.service;

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
public class ShopManagementService {

    private final FraudResultRepository repository;
    private final DefaultTemplateProperties properties;

    public boolean isNewShop(String shopId) {
        Long to = Instant.now().toEpochMilli();
        Long from = Instant.now().minus(properties.getCountToCheckDays(), ChronoUnit.DAYS).toEpochMilli();
        return repository.countOperationByField("shopId", shopId, from, to) == 0;
    }

}
