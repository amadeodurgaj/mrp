package org.mrp.exception;

public class ForbiddenAccessException extends ApiException {
    public ForbiddenAccessException() {
        super(403, "Forbidden: cannot access another user's profile");
    }
}
