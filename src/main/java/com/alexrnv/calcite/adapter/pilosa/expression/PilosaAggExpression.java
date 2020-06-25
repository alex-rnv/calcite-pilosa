package com.alexrnv.calcite.adapter.pilosa.expression;

import org.apache.calcite.sql.SqlKind;

import java.util.List;
import java.util.Stack;
import java.util.StringJoiner;

public class PilosaAggExpression extends PilosaExpression {

    public PilosaAggExpression(AggOperation operation, List<String> groupByFields) {
        this.operation = operation;
        this.groupByFields = groupByFields;
    }

    public enum AggOperation {
        COUNT("Count", SqlKind.COUNT);

        private final String value;
        private final SqlKind sqlKind;

        AggOperation(String value, SqlKind sqlKind) {
            this.value = value;
            this.sqlKind = sqlKind;
        }

        public static AggOperation fromSqlKind(SqlKind sqlKind) {
            for (AggOperation op : values()) {
                if (op.sqlKind.equals(sqlKind)) {
                    return op;
                }
            }
            throw new PilosaExpressionError("unexpected sql kind " + sqlKind);
        }
    }

    private final AggOperation operation;
    private final List<String> groupByFields;

    @Override
    void applyToExecutionStack(Stack<PilosaExpression> stack) {
        if (isGroupBy()) {
            applyGroupBy(stack);
        } else {
            applyAggregate(stack);
        }
    }

    private boolean isGroupBy() {
        return groupByFields.size() > 0;
    }

    private void applyGroupBy(Stack<PilosaExpression> stack) {
        String pilosaGroupByBase = preparePilosaGroupByBase(groupByFields);
        String row;
        if (!stack.empty()) {
            PilosaExpression op = stack.pop();
            String pilosaGroupByFilter = preparePilosaGroupByFilter(op.stringValue());
            row = "GroupBy(" + pilosaGroupByBase + "," + pilosaGroupByFilter + ")";
        } else {
            row = "GroupBy(" + pilosaGroupByBase + ")";
        }

        setStringValue(row);
        stack.push(new PilosaRowExpression(row));
    }

    private String preparePilosaGroupByBase(List<String> groupByCols) {
        StringJoiner joiner = new StringJoiner(",");
        for (String col : groupByCols) {
            joiner.add("Rows(" + col + ")");
        }
        return joiner.toString();
    }

    private String preparePilosaGroupByFilter(String expression) {
        return "filter=" + expression;
    }

    private void applyAggregate(Stack<PilosaExpression> stack) {
        PilosaExpression op = stack.pop();
        String row = operation.value + "(" + op.stringValue() + ")";
        setStringValue(row);
        stack.push(new PilosaRowExpression(row));
    }

}
