package com.alexrnv.calcite.adapter.pilosa.model;

import java.sql.*;

abstract class DriverTest {

    abstract Connection initDBConnection() throws Exception;

    public void testExecuteStatement() {
        try (Connection connection = initDBConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(TestFixtures.SAMPLE_QUERY))
        {
            TestUtils.assertSingleRowSingleValue(resultSet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void testPrepareAndExecuteStatement() {
        try (Connection connection = initDBConnection();
             PreparedStatement statement = connection.prepareStatement(TestFixtures.SAMPLE_QUERY);
             ResultSet resultSet =  statement.executeQuery())
        {
            TestUtils.assertSingleRowSingleValue(resultSet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
