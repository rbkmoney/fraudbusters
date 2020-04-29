package com.rbkmoney.fraudbusters.repository.util;

import com.rbkmoney.fraudbusters.fraud.model.FieldModel;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ParamsInitiator {

    @NotNull
    public static ArrayList<Object> initParams(List<FieldModel> lastParams, Object... args) {
        ArrayList<Object> objects = new ArrayList<>();
        objects.addAll(Arrays.asList(args));
        if (lastParams != null) {
            List<String> collect = lastParams.stream()
                    .map(FieldModel::getValue)
                    .collect(Collectors.toList());
            objects.addAll(collect);
        }
        return objects;
    }

}
