package com.alexrnv.calcite.adapter.pilosa.model;

public class PilosaCalciteModelError extends RuntimeException {

    public enum ErrorCode {
        PILOSA_LIMITATION,
        INTERNAL_ERROR
    }

    private final ErrorCode errorCode;

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public PilosaCalciteModelError(ErrorCode code, String message) {
        super(message);
        this.errorCode = code;
    }
}
