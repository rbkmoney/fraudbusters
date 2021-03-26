package com.rbkmoney.fraudbusters.fraud.pool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.concurrent.ThreadSafe;

import java.util.List;
import java.util.TreeSet;

@Slf4j
@ThreadSafe
@Component
public class CardTokenPoolImpl implements CardTokenPool {

    private TreeSet<String> cardTokens;

    @Override
    public void reinit(List<String> cardTokens) {
        log.info("Reinit cardTokens pool: {}", cardTokens.size());
        this.cardTokens = new TreeSet<>(cardTokens);
        log.info("Reinit cardTokens success");
    }

    @Override
    public void clear() {
        this.cardTokens.clear();
    }

    @Override
    public boolean isExist(String cardToken) {
        if (CollectionUtils.isEmpty(this.cardTokens)) {
            return false;
        }
        return this.cardTokens.contains(cardToken);
    }

    @Override
    public boolean isEmpty() {
        return CollectionUtils.isEmpty(this.cardTokens);
    }

}
