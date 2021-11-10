package com.rbkmoney.fraudbusters.dgraph.aggregates.data;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DgraphPredicatesTestData {

    public static List<String> TOKENS = List.of("token1", "token2", "token3", "token4", "token5");
    public static List<String> EMAILS = List.of("e1@test.ru", "e2@test.ru", "e3@test.ru");
    public static List<String> BINS = List.of("000000", "000101");
    public static List<String> IPS = List.of("localhost", "127.0.0.1", "0:0:0:0");

}
