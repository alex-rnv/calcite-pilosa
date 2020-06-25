package com.alexrnv.calcite.adapter.pilosa.pilosa.dto;

import java.util.Objects;

public class FieldGroup {

    private String field;
    private String rowID;

    FieldGroup(String field, String rowID) {
        this.field = field;
        this.rowID = rowID;
    }

    private FieldGroup(){}

    public String getField() {
        return field;
    }

    public String getRowID() {
        return rowID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldGroup that = (FieldGroup) o;
        return Objects.equals(field, that.field) &&
                Objects.equals(rowID, that.rowID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, rowID);
    }

    @Override
    public String toString() {
        return "FieldGroup{" +
                "field='" + field + '\'' +
                ", rowID='" + rowID + '\'' +
                '}';
    }
}
