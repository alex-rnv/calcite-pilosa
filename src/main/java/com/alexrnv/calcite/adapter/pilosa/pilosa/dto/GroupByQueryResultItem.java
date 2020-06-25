package com.alexrnv.calcite.adapter.pilosa.pilosa.dto;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class GroupByQueryResultItem {

    private long count;
    private List<FieldGroup> group;

    GroupByQueryResultItem(long count, List<FieldGroup> group) {
        this.count = count;
        this.group = group;
    }

    GroupByQueryResultItem() {}

    public long getCount() {
        return count;
    }

    public List<FieldGroup> getGroup() {
        return Collections.unmodifiableList(group);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupByQueryResultItem that = (GroupByQueryResultItem) o;
        return count == that.count &&
                Objects.equals(group, that.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(count, group);
    }

    @Override
    public String toString() {
        return "GroupByQueryResultItem{" +
                "count=" + count +
                ", group=" + group +
                '}';
    }
}
