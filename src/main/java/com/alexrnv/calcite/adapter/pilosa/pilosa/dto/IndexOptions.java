package com.alexrnv.calcite.adapter.pilosa.pilosa.dto;

import java.util.Objects;

public class IndexOptions {

    private boolean keys;
    private boolean trackExistence;

    private IndexOptions() {
    }

    IndexOptions(boolean keys, boolean trackExistence) {
        this.keys = keys;
        this.trackExistence = trackExistence;
    }

    public boolean isKeys() {
        return keys;
    }

    public boolean isTrackExistence() {
        return trackExistence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IndexOptions that = (IndexOptions) o;
        return keys == that.keys &&
                trackExistence == that.trackExistence;
    }

    @Override
    public int hashCode() {
        return Objects.hash(keys, trackExistence);
    }

    @Override
    public String toString() {
        return "IndexOptions{" +
                "keys=" + keys +
                ", trackExistence=" + trackExistence +
                '}';
    }
}
