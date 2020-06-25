package com.alexrnv.calcite.adapter.pilosa.model.rules;

class WrongPlanError extends RuntimeException {
    public WrongPlanError() {
    }

    public WrongPlanError(String message) {
        super(message);
    }
}
