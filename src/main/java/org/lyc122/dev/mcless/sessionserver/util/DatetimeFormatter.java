package org.lyc122.dev.mcless.sessionserver.util;

import java.time.ZoneOffset;
import java.util.Date;
import java.time.format.DateTimeFormatter;

public class DatetimeFormatter {
    private static final DateTimeFormatter ISO_FORMATTER =
            DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC);

    public static String formatToIso(Date date) {
        return ISO_FORMATTER.format(date.toInstant());
    }
}