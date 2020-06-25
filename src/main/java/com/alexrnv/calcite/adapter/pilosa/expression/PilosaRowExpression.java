package com.alexrnv.calcite.adapter.pilosa.expression;

import java.util.Stack;

public class PilosaRowExpression extends PilosaExpression {

    PilosaRowExpression(String stringValue) {
        this.setStringValue(stringValue);
    }

    @Override
    public void applyToExecutionStack(Stack<PilosaExpression> stack) {
        stack.push(this);
    }

}
