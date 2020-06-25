package com.alexrnv.calcite.adapter.pilosa.expression;

public class PilosaExpressionError extends RuntimeException {

    public PilosaExpressionError(String message) {
        super(message);
    }
}
