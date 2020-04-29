package com.rbkmoney.fraudbusters.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TimeParamUtils {

    public static List<Object> generateTimeParams(LocalDateTime from,
                                                  LocalDateTime to) {
        long fromMillisHours = from.toInstant(ZoneOffset.UTC).truncatedTo(ChronoUnit.HOURS).toEpochMilli();
        long fromSeconds = from.toEpochSecond(ZoneOffset.UTC);
        long toMillisHours = to.toInstant(ZoneOffset.UTC).truncatedTo(ChronoUnit.HOURS).toEpochMilli();
        long toSeconds = to.toEpochSecond(ZoneOffset.UTC);
        return new ArrayList<>(Arrays.asList(from.toLocalDate(), to.toLocalDate(), fromMillisHours, toMillisHours, fromSeconds, toSeconds));
    }

}
