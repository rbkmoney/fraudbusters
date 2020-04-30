package com.rbkmoney.fraudbusters.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class TimestampUtil {

    public static Long generateTimestampWithParse(String time) {
        LocalDateTime date = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME);
        Instant instant = date.toInstant(ZoneOffset.UTC);
        return TimestampUtil.generateTimestampNowMillis(instant);
    }

    public static Long generateTimestampNowMillis(Instant now) {
        return now.toEpochMilli();
    }

    public static Long generateTimestampMinusMinutesMillis(Instant now, Long minutes) {
        return minutes != null ? now.minusSeconds(minutes * 60).toEpochMilli() : now.toEpochMilli();
    }

}
