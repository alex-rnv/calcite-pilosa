package com.alexrnv.calcite.adapter.pilosa.pilosa;

public class PilosaAPIError extends RuntimeException {
    public enum ErrorCode {
        INVALID_REQUEST,
        INVALID_RESPONSE,
        SERVER_UNREACHABLE,
        SERVER_ERROR,
        CLIENT_ERROR,
        ERROR_UNKNOWN,
    }

    private final ErrorCode code;

    public PilosaAPIError(ErrorCode code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public PilosaAPIError(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public ErrorCode getCode() {
        return code;
    }
}
