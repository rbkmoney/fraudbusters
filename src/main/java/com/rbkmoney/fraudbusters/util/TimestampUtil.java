package com.rbkmoney.fraudbusters.util;

import java.time.Instant;

public class TimestampUtil {

    public static Long generateTimestampNow(Instant now) {
        return now.getEpochSecond();
    }

    public static Long generateTimestampMinusMinutes(Instant now, Long minutes) {
        return now.minusSeconds(minutes * 60).getEpochSecond();
    }

}
