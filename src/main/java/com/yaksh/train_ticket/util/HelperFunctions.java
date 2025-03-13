package com.yaksh.train_ticket.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HelperFunctions {
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm a");

    // Convert LocalDateTime to String (Custom Format)
    public static String formatLocalDateTimeToString(LocalDateTime dateTime) {
        return dateTime.format(DEFAULT_FORMATTER);
    }

    // check if date provided is in the past
    public static boolean isDateInThePast(LocalDate date){
        return date.isBefore(LocalDate.now());
    }

}
