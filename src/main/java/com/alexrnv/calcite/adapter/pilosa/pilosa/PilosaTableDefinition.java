package com.alexrnv.calcite.adapter.pilosa.pilosa;

import java.util.Collections;
import java.util.List;

public class PilosaTableDefinition {
    final String name;
    final List<String> columnNames;
    final List<Class<?>> javaTypes;

    public PilosaTableDefinition(String name, List<String> columnNames, List<Class<?>> javaTypes) {
        this.name = name;
        this.columnNames = columnNames;
        this.javaTypes = javaTypes;
    }

    public String getName() {
        return name;
    }

    public List<String> getColumnNames() {
        return Collections.unmodifiableList(columnNames);
    }

    public List<Class<?>> getJavaTypes() {
        return Collections.unmodifiableList(javaTypes);
    }
}
