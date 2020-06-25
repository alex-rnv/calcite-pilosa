package com.alexrnv.calcite.adapter.pilosa.avatica;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.*;

public class PilosaAvaticaHandlerTest {

    @Test
    public void learningTestAtomicLongOverflowPreIncrement() {
        AtomicLong atomicLong = new AtomicLong(Long.MAX_VALUE);
        assertEquals(Long.MIN_VALUE, atomicLong.incrementAndGet());
    }

    @Test
    public void learningTestAtomicLongOverflowPostIncrement() {
        AtomicLong atomicLong = new AtomicLong(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, atomicLong.getAndIncrement());
        assertEquals(Long.MIN_VALUE, atomicLong.get());
    }

}