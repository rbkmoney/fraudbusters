package com.rbkmoney.fraudbusters.converter;

import com.rbkmoney.trusted.tokens.YearsOffset;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class IntegerToYearsOffsetConverter implements Converter<Integer, YearsOffset> {

    private static final String MAPPING_ERROR = "Value cannot be mapped to YearsOffset - ";

    @Override
    public YearsOffset convert(@NotNull Integer offset) {
        return switch (offset) {
            case 0 -> YearsOffset.current_year;
            case 1 -> YearsOffset.current_with_last_years;
            case 2 -> YearsOffset.current_with_two_last_years;
            default -> throw new IllegalStateException(MAPPING_ERROR + offset);
        };
    }

}
