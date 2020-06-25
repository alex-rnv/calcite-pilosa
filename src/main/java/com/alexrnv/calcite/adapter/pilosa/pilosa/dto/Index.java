package com.alexrnv.calcite.adapter.pilosa.pilosa.dto;

import java.util.List;
import java.util.Objects;

public class Index {

    private String name;
    private IndexOptions options;
    private int shardWidth;
    private List<Field> fields;

    Index() {
    }

    public String getName() {
        return name;
    }

    public IndexOptions getOptions() {
        return options;
    }

    public int getShardWidth() {
        return shardWidth;
    }

    public List<Field> getFields() {
        return fields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Index index = (Index) o;
        return shardWidth == index.shardWidth &&
                Objects.equals(name, index.name) &&
                Objects.equals(options, index.options) &&
                Objects.equals(fields, index.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, options, shardWidth, fields);
    }

    @Override
    public String toString() {
        return "Index{" +
                "name='" + name + '\'' +
                ", options=" + options +
                ", shardWidth=" + shardWidth +
                ", fields=" + fields +
                '}';
    }
}
