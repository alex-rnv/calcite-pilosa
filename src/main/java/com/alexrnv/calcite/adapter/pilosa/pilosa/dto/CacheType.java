package com.alexrnv.calcite.adapter.pilosa.pilosa.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CacheType {
    RANKED("ranked"),
    LRU("lru");

    private final String value;

    CacheType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
