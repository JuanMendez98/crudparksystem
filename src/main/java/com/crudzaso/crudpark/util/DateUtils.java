package com.crudzaso.crudpark.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utilities for handling dates and timestamps
 */
public class DateUtils {

    private static final DateTimeFormatter FORMATTER_DATE_TIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter FORMATTER_SHORT_DATE =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter FORMATTER_TIME =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    private static final DateTimeFormatter FORMATTER_TICKET =
            DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

    public static String formatDateTime(Timestamp timestamp) {
        if (timestamp == null) {
            return "";
        }
        return timestamp.toLocalDateTime().format(FORMATTER_DATE_TIME);
    }

    public static String formatShortDate(Timestamp timestamp) {
        if (timestamp == null) {
            return "";
        }
        return timestamp.toLocalDateTime().format(FORMATTER_SHORT_DATE);
    }

    public static String formatTime(Timestamp timestamp) {
        if (timestamp == null) {
            return "";
        }
        return timestamp.toLocalDateTime().format(FORMATTER_TIME);
    }

    public static String formatForTicket(Timestamp timestamp) {
        if (timestamp == null) {
            return "";
        }
        return timestamp.toLocalDateTime().format(FORMATTER_TICKET);
    }

    public static Timestamp now() {
        return Timestamp.valueOf(LocalDateTime.now());
    }

    public static String formatStayTime(int minutes) {
        if (minutes < 60) {
            return minutes + " minutos";
        }

        int hours = minutes / 60;
        int remainingMinutes = minutes % 60;

        if (remainingMinutes == 0) {
            return hours + (hours == 1 ? " hora" : " horas");
        }

        return hours + (hours == 1 ? " hora" : " horas") + " y " +
                remainingMinutes + " minutos";
    }
}
