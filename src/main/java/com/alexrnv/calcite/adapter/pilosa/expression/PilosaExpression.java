package com.alexrnv.calcite.adapter.pilosa.expression;

import java.util.Stack;

public abstract class PilosaExpression {

    private String stringValue;

    abstract void applyToExecutionStack(Stack<PilosaExpression> stack);

    String stringValue() {
        return stringValue;
    }

    void setStringValue(String value) {
        this.stringValue = value;
    }

}
