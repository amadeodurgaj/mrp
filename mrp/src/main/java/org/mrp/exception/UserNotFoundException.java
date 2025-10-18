package org.mrp.exception;

public class UserNotFoundException extends ApiException {
    public UserNotFoundException(String username) {
        super(404, "User '" + username + "' not found");
    }}
