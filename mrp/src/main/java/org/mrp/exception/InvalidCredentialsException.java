package org.mrp.exception;

public class InvalidCredentialsException extends ApiException {
  public InvalidCredentialsException() {
    super(401, "Invalid username or password");
  }
}
