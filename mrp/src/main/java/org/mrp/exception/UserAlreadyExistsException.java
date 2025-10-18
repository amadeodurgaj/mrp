package org.mrp.exception;

public class UserAlreadyExistsException extends ApiException {

    public UserAlreadyExistsException(String username) {
        super(409, "User '" + username + "' already exists");
    }

}
