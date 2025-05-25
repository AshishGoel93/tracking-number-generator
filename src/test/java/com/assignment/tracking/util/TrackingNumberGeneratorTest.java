package com.assignment.tracking.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrackingNumberGeneratorTest {

    private final TrackingNumberGenerator generator = new TrackingNumberGenerator();

    @Test
    void testGenerateTrackingNumber() {
        String result = generator.generate("US", "IN", "20250525", 1234);
        assertEquals(16, result.length());
        assertTrue(result.startsWith("USIN20250525"));
    }

    @Test
    void testFormatPaddingAndBase36Encoding() {
        String result = generator.generate("U", "IN", "20250101", 35);
        assertEquals("UXIN20250101000Z", result);
    }

    @Test
    void testNullInputs() {
        String result = generator.generate(null, null, null, 1);
        assertEquals("XXXXXXXXXXXX0001", result);
    }
}