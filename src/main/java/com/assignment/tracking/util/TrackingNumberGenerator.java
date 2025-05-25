package com.assignment.tracking.util;

import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class TrackingNumberGenerator {

    public String generate(String origin, String destination, String date, long counter) {
        return formatFixed(origin, 2)
                + formatFixed(destination, 2)
                + formatFixed(date, 8)
                + encodeBase36(counter, 4);
    }

    private String formatFixed(String input, int length) {
        if (input == null) return "X".repeat(length);
        String cleaned = input.toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]", "");
        return String.format("%-" + length + "s", cleaned).replace(' ', 'X').substring(0, length);
    }

    private String encodeBase36(long value, int length) {
        String base36 = Long.toString(value, 36).toUpperCase(Locale.ROOT);
        return String.format("%" + length + "s", base36).replace(' ', '0');
    }
}
