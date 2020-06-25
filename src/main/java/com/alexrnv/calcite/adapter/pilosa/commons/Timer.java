package com.alexrnv.calcite.adapter.pilosa.commons;

public class Timer {
    private final long startTime;

    private Timer(long startTime) {
        this.startTime = startTime;
    }

    public static Timer start() {
        return new Timer(System.currentTimeMillis());
    }

    public long elapsed() {
        return System.currentTimeMillis() - startTime;
    }
}
