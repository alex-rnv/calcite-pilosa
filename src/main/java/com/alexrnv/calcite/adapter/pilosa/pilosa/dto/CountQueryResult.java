package com.alexrnv.calcite.adapter.pilosa.pilosa.dto;

import com.alexrnv.calcite.adapter.pilosa.pilosa.query.PilosaQuery;

import java.util.List;
import java.util.Objects;

/**
 * Should be used in conjunction with {@link PilosaQuery},
 * results order corresponds to expression order in {@link PilosaQuery}
 */
public class CountQueryResult implements PilosaQueryResult {

    private List<Long> results;

    CountQueryResult(List<Long> results) {
        this.results = results;
    }

    private CountQueryResult() {}

    public List<Long> getResults() {
        return results;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CountQueryResult result = (CountQueryResult) o;
        return Objects.equals(results, result.results);
    }

    @Override
    public int hashCode() {
        return Objects.hash(results);
    }

    @Override
    public String toString() {
        return "CountQueryResult{" +
                "results=" + results +
                '}';
    }
}
