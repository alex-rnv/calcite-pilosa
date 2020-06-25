package com.alexrnv.calcite.adapter.pilosa.model;

import org.apache.calcite.schema.Table;
import org.apache.calcite.schema.impl.AbstractSchema;

import java.util.Collections;
import java.util.Map;

public class PilosaSchema extends AbstractSchema {

    private final Map<String, Table> tableMap;

    PilosaSchema(Map<String, Table> tableMap) {
        this.tableMap = tableMap;
    }

    @Override
    protected Map<String, Table> getTableMap() {
        return Collections.unmodifiableMap(this.tableMap);
    }
}
