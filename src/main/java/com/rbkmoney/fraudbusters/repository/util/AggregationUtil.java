package com.rbkmoney.fraudbusters.repository.util;

import com.rbkmoney.fraudbusters.fraud.model.FieldModel;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.time.ZoneOffset.UTC;

public class AggregationUtil {

    public static StringBuilder appendGroupingFields(List<FieldModel> fieldModels, StringBuilder sql, StringBuilder sqlGroupBy) {
        if (fieldModels != null) {
            for (FieldModel fieldModel : fieldModels) {
                sql.append(" and ").append(fieldModel.getName()).append("=? ");
                sqlGroupBy.append(", ").append(fieldModel.getName());
            }
        }
        return sql.append(sqlGroupBy.toString());
    }

    public static ArrayList<Object> generateParams(Long from, Long to, List<FieldModel> fieldModels, String value) {
        return generateParams(from, to, fieldModels, value, null);
    }

    public static ArrayList<Object> generateParams(Long from, Long to, List<FieldModel> fieldModels, String value, String status) {
        return generateParams(from, to, fieldModels, value, status, null);
    }

    public static ArrayList<Object> generateParams(Long from, Long to, List<FieldModel> fieldModels, String value, String status, String errorCode) {
        Instant instantFrom = Instant.ofEpochMilli(from);
        LocalDate dateFrom = instantFrom.atZone(UTC).toLocalDate();
        Instant instantTo = Instant.ofEpochMilli(to);
        LocalDate dateTo = instantTo.atZone(UTC).toLocalDate();
        return initParams(fieldModels, dateFrom, dateTo, instantFrom.getEpochSecond(), instantTo.getEpochSecond(), value, status, errorCode);
    }

    public static ArrayList<Object> generateParams(Long from, Long to, String value) {
        return generateParams(from, to, null, value);
    }

    public static ArrayList<Object> generateStatusParams(Long from, Long to, String value, String status) {
        return generateParams(from, to, null, value, status);
    }

    @NotNull
    private static ArrayList<Object> initParams(List<FieldModel> lastParams, Object... args) {
        ArrayList<Object> objects = new ArrayList<>();
        if (args != null) {
            Arrays.stream(args)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(() -> objects));
        }
        if (lastParams != null) {
            lastParams.stream()
                    .map(FieldModel::getValue)
                    .collect(Collectors.toCollection(() -> objects));
        }
        return objects;
    }
}
