package com.rbkmoney.fraudbusters.util;

import java.time.Instant;
import java.time.ZoneId;

public class TimestampUtil {

    public static Long generateTimestampNow(Instant now){
        return now.atZone(ZoneId.systemDefault())
                .withSecond(0)
                .withNano(0).toInstant().toEpochMilli();
    }

    public static Long generateTimestampMinusMinutes(Instant now, Long minutes){
        return now.atZone(ZoneId.systemDefault())
                .minusMinutes(minutes)
                .withSecond(0)
                .withNano(0).toInstant().toEpochMilli();
    }

}
