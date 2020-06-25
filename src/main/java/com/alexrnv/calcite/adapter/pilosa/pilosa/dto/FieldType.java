package com.alexrnv.calcite.adapter.pilosa.pilosa.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public enum FieldType {

    SET("set"),
    INT("int"),
    BOOL("bool"),
    TIME("time"),
    MUTEX("mutex");

    private final String value;

    FieldType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
