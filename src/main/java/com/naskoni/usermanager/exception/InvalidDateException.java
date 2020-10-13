package com.naskoni.usermanager.exception;

public class InvalidDateException extends RuntimeException {

  private static final long serialVersionUID = 1025537995195594035L;

  public InvalidDateException(String message) {
    super(message);
  }
}
