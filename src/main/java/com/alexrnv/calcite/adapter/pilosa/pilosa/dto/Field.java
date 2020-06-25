package com.alexrnv.calcite.adapter.pilosa.pilosa.dto;

import java.util.Objects;

public class Field {

    private String name;
    private FieldOptions options;

    Field() {
    }

    public String getName() {
        return name;
    }

    public FieldOptions getOptions() {
        return options;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Field field = (Field) o;
        return Objects.equals(name, field.name) &&
                Objects.equals(options, field.options);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, options);
    }

    @Override
    public String toString() {
        return "Field{" +
                "name='" + name + '\'' +
                ", options=" + options +
                '}';
    }
}
