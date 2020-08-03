package com.rbkmoney.fraudbusters.pool;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.time.Instant;

class HistoricalPoolImplTest {

    public static final String TEST = "Test";
    public static final String ID_REFERENCE = "id_reference-";
    HistoricalPoolImpl<String> timePool = new HistoricalPoolImpl<>("time-pool");

    @Test
    void add() throws InterruptedException {
        initTimePool(40);

        String result = timePool.get(TEST, Instant.now().toEpochMilli() - 1000L);
        Assert.assertEquals(ID_REFERENCE + 30, result);

        result = timePool.get(TEST, Instant.now().toEpochMilli());
        Assert.assertEquals(ID_REFERENCE + 39, result);
    }

    private void initTimePool(int size) throws InterruptedException {
        int count = 0;
        while (count < size) {
            timePool.add(TEST, Instant.now().toEpochMilli(), ID_REFERENCE + count++);
            Thread.sleep(100L);
        }
    }

    @Test
    void cleanTimePool() throws InterruptedException {
        initTimePool(20);
        Assert.assertEquals(20, timePool.deepSize());

        timePool.cleanUntil(TEST, Instant.now().toEpochMilli() - 1000L);
        Assert.assertEquals(9, timePool.deepSize());
    }
}