package com.yaksh.train_ticket.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HelperFunctions {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public static String localDateToString(LocalDate date) {
        return date.format(FORMATTER);
    }

    // check if date provided is in the past
    public static boolean isDateInThePast(LocalDate date){
        return date.isBefore(LocalDate.now());
    }

}
