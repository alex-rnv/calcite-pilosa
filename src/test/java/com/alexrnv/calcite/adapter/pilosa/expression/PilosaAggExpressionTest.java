package com.alexrnv.calcite.adapter.pilosa.expression;

import com.alexrnv.calcite.adapter.pilosa.expression.PilosaAggExpression.AggOperation;
import org.apache.calcite.sql.SqlKind;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PilosaAggExpressionTest {

    private final List<SqlKind> validSqlKinds = Collections.singletonList(SqlKind.COUNT);

    @Test
    public void testValidAggOperation() {
        for (SqlKind kind : SqlKind.values()) {
            if (validSqlKinds.contains(kind)) {
                boolean valid = true;
                try {
                    AggOperation.fromSqlKind(kind);
                } catch (PilosaExpressionError e) {
                    valid = false;
                }
                assertTrue(valid);
            }
        }
    }

    @Test
    public void testInvalidAggOperation() {
        for (SqlKind kind : SqlKind.values()) {
            if (!validSqlKinds.contains(kind)) {
                boolean valid = true;
                try {
                    AggOperation.fromSqlKind(kind);
                } catch (PilosaExpressionError e) {
                    valid = false;
                }
                assertFalse(valid);
            }
        }
    }

}