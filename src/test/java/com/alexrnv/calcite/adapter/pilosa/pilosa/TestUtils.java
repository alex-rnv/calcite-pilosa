package com.alexrnv.calcite.adapter.pilosa.pilosa;

import org.junit.Assert;

import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

class TestUtils {

    static void assertListsOfArraysEqual(List<Object> expected, List<Object> actual) {
        Assert.assertTrue(expected != null && actual != null);
        assertEquals(expected.size(), actual.size());

        if (!expected.isEmpty()) {

            for (int k = 0; k < expected.size(); k++) {
                if (expected.get(k) instanceof Object[] && actual.get(k) instanceof Object[]) {
                    Object[] e = (Object[]) expected.get(k);
                    Object[] a = (Object[]) actual.get(k);
                    assertArrayEquals(e, a);
                } else {
                    assertEquals(expected, actual);
                }
            }

        }
    }
}
