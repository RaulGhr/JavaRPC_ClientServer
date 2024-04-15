package org.example;

import java.time.format.DateTimeFormatter;

public class DataFormatter {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public static DateTimeFormatter getFormatter() {
        return formatter;
    }
}
