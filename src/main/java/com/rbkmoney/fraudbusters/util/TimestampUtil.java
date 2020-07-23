package com.rbkmoney.fraudbusters.util;

import com.rbkmoney.fraudbusters.domain.TimeProperties;
import org.springframework.lang.NonNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoUnit.HOURS;

public class TimestampUtil {

    public static Long generateTimestampWithParse(String time) {
        Instant instant = parseInstantFromString(time);
        return TimestampUtil.generateTimestampNowMillis(instant);
    }

    public static Instant parseInstantFromString(String time) {
        LocalDateTime date = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME);
        return date.toInstant(ZoneOffset.UTC);
    }

    public static Long generateTimestampNowMillis(Instant now) {
        return now.toEpochMilli();
    }

    public static Long generateTimestampMinusMinutesMillis(Instant now, Long minutes) {
        return minutes != null ? now.minusSeconds(minutes * 60).toEpochMilli() : now.toEpochMilli();
    }

    @NonNull
    public static TimeProperties generateTimeProperties() {
        return generateTimePropertiesByInstant(Instant.now());
    }

    @NonNull
    public static TimeProperties generateTimePropertiesByInstant(Instant instant) {
        TimeProperties timeProperties = new TimeProperties();
        LocalDateTime localDateTime = instant.atZone(UTC).toLocalDateTime();
        timeProperties.setTimestamp(localDateTime.toLocalDate());
        timeProperties.setEventTime(localDateTime.toEpochSecond(UTC));
        long eventTimeHour = instant.truncatedTo(HOURS).toEpochMilli();
        timeProperties.setEventTimeHour(eventTimeHour);
        return timeProperties;
    }

    @NonNull
    public static TimeProperties generateTimePropertiesByString(String time) {
        Instant instant = parseInstantFromString(time);
        return generateTimePropertiesByInstant(instant);
    }
}
