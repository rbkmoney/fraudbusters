package com.rbkmoney.fraudbusters.service;

import com.rbkmoney.fraudbusters.util.KeyGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsumerGroupIdService {

    public static final String DELIMETER = "-";
    @Value("${kafka.consumer.prefix}")
    private String prefix;

    public String generateRandomGroupId(String group) {
        String groupId = KeyGenerator.generateKey(generateGroupId(group) + DELIMETER);
        log.info("generateRandomGroupId groupId: {}", groupId);
        return groupId;
    }

    public String generateGroupId(String group) {
        String groupId = prefix + DELIMETER + group;
        log.info("generateGroupId groupId: {}", groupId);
        return groupId;
    }

}
