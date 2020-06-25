package com.alexrnv.calcite.adapter.pilosa.expression;

import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PilosaSetOpExpression extends PilosaExpression {

    private final int argumentsCount;

    public PilosaSetOpExpression(SetOperation operation, int argumentsCount) {
        this.operation = operation;
        this.argumentsCount = argumentsCount;
    }

    public enum SetOperation {
        INTERSECT("Intersect"),
        DIFFERENCE("Difference");

        private final String value;

        SetOperation(String value) {
            this.value = value;
        }
    }

    private final SetOperation operation;

    @Override
    public void applyToExecutionStack(Stack<PilosaExpression> stack) {
        String argumentsString = IntStream.range(0, argumentsCount)
                .mapToObj(i -> stack.pop().stringValue())
                .collect(Collectors.joining(","));
        String row = operation.value + "(" + argumentsString + ")";
        setStringValue(row);
        stack.push(new PilosaRowExpression(row));
    }
}
