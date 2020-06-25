package com.alexrnv.calcite.adapter.pilosa.pilosa.query;

import org.apache.commons.lang3.RandomStringUtils;

public class PilosaQuery {

    private final String id;
    private final String indexName;
    private final QueryType type;
    private final String queryString;
    private final boolean isBulk;

    public static PilosaQuery fromString(String tableName, String queryString) {
        return new PilosaQueryParser(tableName).fromString(queryString);
    }

    PilosaQuery(String indexName, QueryType type, String queryString, boolean isBulk) {
        this.indexName = indexName;
        this.type = type;
        this.queryString = queryString;
        this.isBulk = isBulk;
        this.id = RandomStringUtils.randomAlphanumeric(8);
    }

    public String getId() {
        return id;
    }

    public QueryType getType() {
        return type;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getIndexName() {
        return indexName;
    }

    public boolean isBulk() {
        return isBulk;
    }

    @Override
    public String toString() {
        return "PilosaQuery{" +
                "id='" + id + '\'' +
                ", indexName='" + indexName + '\'' +
                ", type=" + type +
                ", queryString='" + queryString + '\'' +
                ", isBulk=" + isBulk +
                '}';
    }
}
