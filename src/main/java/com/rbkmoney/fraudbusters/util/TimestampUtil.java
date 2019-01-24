package com.rbkmoney.fraudbusters.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class TimestampUtil {

    public static Long generateTimestampNow(LocalDateTime now) {
        return now.toEpochSecond(ZoneOffset.UTC);
    }

    public static Long generateTimestampMinusMinutes(LocalDateTime now, Long minutes) {
        return now.minusMinutes(minutes).toEpochSecond(ZoneOffset.UTC);
    }

}
