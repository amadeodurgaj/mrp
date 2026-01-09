package org.mrp.exception;

public class ForbiddenAccessExceptionMedia extends ApiException {
    public ForbiddenAccessExceptionMedia() {
        super(403, "Forbidden: cannot access another user's media");
    }
}
