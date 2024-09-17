package com.vw.tide_eye.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeConverter {
    public static String parseDateTime(String dateTimeString) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm");

        // Parse the input string to a LocalDateTime object
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, inputFormatter);
        return dateTime.format(outputFormatter);
    }
}
