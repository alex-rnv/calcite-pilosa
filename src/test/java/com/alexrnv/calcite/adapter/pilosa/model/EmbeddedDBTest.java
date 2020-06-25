package com.alexrnv.calcite.adapter.pilosa.model;

import org.junit.ClassRule;
import org.junit.Test;
import org.zapodot.junit.db.CompatibilityMode;
import org.zapodot.junit.db.EmbeddedDatabaseRule;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EmbeddedDBTest {

    public static final String CREATE_TEST_DDL = Optional.ofNullable(
            SqlPilosaJoinRdbmsQueryTest.class.getClassLoader().getResource("metadata.sql")
    ).orElseThrow(() -> new RuntimeException("ddl file not found")).getFile();

    @ClassRule
    public static final EmbeddedDatabaseRule embeddedDB = EmbeddedDatabaseRule
            .h2()
            .withMode(CompatibilityMode.PostgreSQL)
            .withInitialSqlFromResource(CREATE_TEST_DDL)
            .build();

    @Test
    public void testEnsureCountryList() throws Exception {
        ensureRowsCount("SELECT COUNT(*) FROM COUNTRIES", 15);
    }

    @Test
    public void testEnsureItemsList() throws Exception {
        ensureRowsCount("SELECT COUNT(*) FROM ITEMS", 7);
    }

    @Test
    public void testEnsureItemCategoriesList() throws Exception {
        ensureRowsCount("SELECT COUNT(*) FROM ITEM_CATEGORIES", 4);
    }

    @Test
    public void testEnsureShopsList() throws Exception {
        ensureRowsCount("SELECT COUNT(*) FROM SHOPS", 7);
    }

    private void ensureRowsCount(String query, int rowsCount) throws Exception {
        try(final Connection connection = DriverManager.getConnection(embeddedDB.getConnectionJdbcUrl())) {
            try(final Statement statement = connection.createStatement();
                final ResultSet resultSet = statement.executeQuery(query)
            ) {
                assertTrue(resultSet.next());
                assertEquals(rowsCount, resultSet.getInt(1));
            }
        }
    }



}
