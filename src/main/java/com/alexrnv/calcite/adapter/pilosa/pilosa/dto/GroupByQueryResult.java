package com.alexrnv.calcite.adapter.pilosa.pilosa.dto;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class GroupByQueryResult implements PilosaQueryResult {

    private List<List<GroupByQueryResultItem>> results;

    GroupByQueryResult(List<List<GroupByQueryResultItem>> results){
        this.results = results;
    }

    private GroupByQueryResult() {}

    public List<List<GroupByQueryResultItem>> getResults() {
        return Collections.unmodifiableList(results);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupByQueryResult that = (GroupByQueryResult) o;
        return Objects.equals(results, that.results);
    }

    @Override
    public int hashCode() {
        return Objects.hash(results);
    }

    @Override
    public String toString() {
        return "GroupByQueryResult{" +
                "results=" + results +
                '}';
    }
}
