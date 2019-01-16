package com.rbkmoney.fraudbusters.constant;

public enum ListType {

    BLACK("black_"),
    WHITE("white_");

    private String prefix;

    ListType(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}
