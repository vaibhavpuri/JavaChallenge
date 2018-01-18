package com.db.awmd.challenge.exception;

public class NegativeBalanceException extends RuntimeException {
  
  private static final long serialVersionUID = 1255345888695399625L;

  public NegativeBalanceException(String message) {
    super(message);
  }

}
