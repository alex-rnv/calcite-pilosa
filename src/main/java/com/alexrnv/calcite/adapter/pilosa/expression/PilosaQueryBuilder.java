package com.alexrnv.calcite.adapter.pilosa.expression;

import com.alexrnv.calcite.adapter.pilosa.model.rels.PilosaRel;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * Helper class for translation of relational tree to pilosa query expression.
 * It works in 2 passes.
 * 1. First {@link PilosaRel} nodes convert themselves to corresponding pilosa expressions and add to the queue according
 * to reverse polish notation (RPN). It implies there is a contract, and relational nodes are responsible for
 * pushing {@link PilosaExpression}s in the right order: first push operand(s), then operator.
 * 2. When the queue is full, we iterate over RPN queue and build resulting composite expression.
 *
 * Check {@link PilosaExpression} for detailed expressions description.
 */
public class PilosaQueryBuilder {

    private static final String EMPTY_ROW = "Row()";

    private final Queue<PilosaExpression> queue = new LinkedList<>();
    private final Stack<PilosaExpression> stack = new Stack<>();

    public void enqueueInReversePolishNotation(PilosaExpression expression) {
        this.queue.add(expression);
    }

    public String build() {
        if (queue.isEmpty()) {
            return EMPTY_ROW;
        }

        processQueue();
        return stack.pop().stringValue();
    }

    private void processQueue() {
        while (!queue.isEmpty()) {
            PilosaExpression expression = queue.poll();
            expression.applyToExecutionStack(stack);
        }
    }

}
