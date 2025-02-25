package com.yaksh.train_ticket.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HelperFunctions {
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm a");

    // Convert LocalDateTime to String (Custom Format)
    public static String formatLocalDateTimeToString(LocalDateTime dateTime) {
        return dateTime.format(DEFAULT_FORMATTER);
    }

}
