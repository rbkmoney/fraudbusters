package com.rbkmoney.fraudbusters.template.pool.time;

import com.rbkmoney.fraudbusters.template.pool.TimePoolImpl;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.time.Instant;

class TimePoolImplTest {

    public static final String TEST = "Test";
    public static final String ID_REFERENCE = "id_reference-";
    TimePoolImpl<String> timePool = new TimePoolImpl<>("time-pool");

    @Test
    void add() throws InterruptedException {
        int count = 0;
        while (count < 40) {
            timePool.add(TEST, Instant.now().toEpochMilli(), ID_REFERENCE + count++);
            Thread.sleep(100L);
        }

        String result = timePool.get(TEST, Instant.now().toEpochMilli() - 1000L);
        Assert.assertEquals(ID_REFERENCE + 30, result);

        result = timePool.get(TEST, Instant.now().toEpochMilli());
        Assert.assertEquals(ID_REFERENCE + 39, result);
    }
}