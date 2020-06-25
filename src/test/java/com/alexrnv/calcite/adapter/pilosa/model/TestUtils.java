package com.alexrnv.calcite.adapter.pilosa.model;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import static org.junit.Assert.*;

class TestUtils {

    static void assertSingleRowSingleValue(ResultSet resultSet) throws SQLException {
        assertTrue(resultSet.next());
        ResultSetMetaData metaData = resultSet.getMetaData();
        assertEquals(1, metaData.getColumnCount());
        assertEquals("VOLUME", metaData.getColumnLabel(1));
        assertEquals(StubPilosaAdapter.ULTIMATE_UNIVERSAL_RESPONSE_LONG, resultSet.getLong(1));
        assertFalse(resultSet.next());
    }
}
