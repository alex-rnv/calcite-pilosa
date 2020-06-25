package com.alexrnv.calcite.adapter.pilosa.model;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class LocalDriverTest extends DriverTest {

    @Test
    public void testExecuteStatement() {
        super.testExecuteStatement();
    }

    @Test
    public void testPrepareAndExecuteStatement() {
        super.testPrepareAndExecuteStatement();
    }

    Connection initDBConnection() throws ClassNotFoundException, SQLException {
        Properties info = new Properties();
        info.put("model", TestFixtures.STUB_PILOSA_MODEL_URI_INLINE);
        info.put("caseSensitive", "false");

        Class.forName("org.apache.calcite.jdbc.Driver");
        return DriverManager.getConnection("jdbc:calcite:", info);
    }
}
