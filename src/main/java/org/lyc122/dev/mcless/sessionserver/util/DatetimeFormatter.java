package org.lyc122.dev.mcless.sessionserver.util;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DatetimeFormatter {
    private static final DateTimeFormatter ISO_FORMATTER =
            DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC);

    public static String formatToIso(Date date) {
        return ISO_FORMATTER.format(date.toInstant());
    }

    public static Date parseFromIso(String isoString) {
        if (isoString == null || isoString.isEmpty()) {
            return null;
        }
        return Date.from(Instant.from(ISO_FORMATTER.parse(isoString)));
    }
}
