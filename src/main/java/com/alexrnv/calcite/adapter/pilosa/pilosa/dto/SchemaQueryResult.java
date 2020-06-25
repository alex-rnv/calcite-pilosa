package com.alexrnv.calcite.adapter.pilosa.pilosa.dto;

import java.util.List;
import java.util.Objects;

public class SchemaQueryResult {
    private List<Index> indexes;

    SchemaQueryResult() { }

    public List<Index> getIndexes() {
        return indexes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SchemaQueryResult that = (SchemaQueryResult) o;
        return Objects.equals(indexes, that.indexes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(indexes);
    }

    @Override
    public String toString() {
        return "IndexesQueryResult{" +
                "indexes=" + indexes +
                '}';
    }
}
