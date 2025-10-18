package org.mrp.exception;

public class InternalServerException extends ApiException {
    public InternalServerException(Throwable cause) {
        super(500, "Internal server error: " + cause.getMessage());
        initCause(cause);
    }
}
