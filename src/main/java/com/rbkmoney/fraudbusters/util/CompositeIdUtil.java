package com.rbkmoney.fraudbusters.util;

import com.google.common.base.Splitter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompositeIdUtil {

    public static final String ID_SPLITTER = "|";

    public static String create(String head, String tail) {
        return head + ID_SPLITTER + tail;
    }

    public static List<String> extract(String compositeId) {
        return Splitter.on(ID_SPLITTER)
                .splitToList(compositeId);
    }
}
