package com.rbkmoney.fraudbusters.util;

import com.rbkmoney.fraudbusters.domain.TimeProperties;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoUnit.HOURS;

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

    public static TimeProperties generateTimeProperties() {
        TimeProperties timeProperties = new TimeProperties();
        Instant instant = Instant.now();
        LocalDateTime localDateTime = instant.atZone(UTC).toLocalDateTime();
        timeProperties.setTimestamp(localDateTime.toLocalDate());
        timeProperties.setEventTime(localDateTime.toEpochSecond(UTC));
        long eventTimeHour = instant.truncatedTo(HOURS).toEpochMilli();
        timeProperties.setEventTimeHour(eventTimeHour);
        return timeProperties;
    }
}
