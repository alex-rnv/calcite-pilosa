package com.alexrnv.calcite.adapter.pilosa.pilosa.query;

public enum QueryType {
    COUNT("Count"),
    GROUP_BY("GroupBy"),
    UNKNOWN("");

    private final String stringValue;

    QueryType(String value) {
        stringValue = value;
    }

    public String getStringValue() {
        return stringValue;
    }

    public static QueryType fromRawQuery(String query) {
        if (query == null) {
            return UNKNOWN;
        }
        query = query.toLowerCase();
        for (QueryType type : values()) {
            if (query.startsWith(type.stringValue.toLowerCase())) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
