package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.trusted.tokens.YearsOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IntegerToYearsOffsetConverterTest {

    private IntegerToYearsOffsetConverter converter;

    @BeforeEach
    void setUp() {
        converter = new IntegerToYearsOffsetConverter();
    }

    @Test
    void currentYearTest() {
        assertEquals(YearsOffset.current_year, converter.convert(0));
    }

    @Test
    void currentAndPreviousYearTest() {
        assertEquals(YearsOffset.current_with_last_years, converter.convert(1));
    }

    @Test
    void currentYearAndTwoPreviousYearsTest() {
        assertEquals(YearsOffset.current_with_two_last_years, converter.convert(2));
    }

    @Test
    void unknownValueTest() {
        IllegalStateException illegalStateException =
                assertThrows(IllegalStateException.class, () -> converter.convert(5));
        assertEquals("Value cannot be mapped to YearsOffset - 5", illegalStateException.getMessage());
    }
}
