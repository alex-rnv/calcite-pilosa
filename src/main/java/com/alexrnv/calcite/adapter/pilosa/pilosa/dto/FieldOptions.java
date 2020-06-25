package com.alexrnv.calcite.adapter.pilosa.pilosa.dto;

import java.util.Objects;

public class FieldOptions {

    private FieldType type;
    private boolean keys;
    private CacheType cacheType;
    private int cacheSize;
    private int min;
    private int max;
    private String timeQuantum;
    private boolean noStandardView;

    FieldOptions() {
    }

    public FieldType getType() {
        return type;
    }

    public boolean isKeys() {
        return keys;
    }

    public CacheType getCacheType() {
        return cacheType;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public String getTimeQuantum() {
        return timeQuantum;
    }

    public boolean isNoStandardView() {
        return noStandardView;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldOptions that = (FieldOptions) o;
        return keys == that.keys &&
                cacheSize == that.cacheSize &&
                min == that.min &&
                max == that.max &&
                noStandardView == that.noStandardView &&
                type == that.type &&
                cacheType == that.cacheType &&
                Objects.equals(timeQuantum, that.timeQuantum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, keys, cacheType, cacheSize, min, max, timeQuantum, noStandardView);
    }

    @Override
    public String toString() {
        return "FieldOptions{" +
                "type=" + type +
                ", keys=" + keys +
                ", cacheType=" + cacheType +
                ", cacheSize=" + cacheSize +
                ", min=" + min +
                ", max=" + max +
                ", timeQuantum='" + timeQuantum + '\'' +
                ", noStandardView=" + noStandardView +
                '}';
    }
}
