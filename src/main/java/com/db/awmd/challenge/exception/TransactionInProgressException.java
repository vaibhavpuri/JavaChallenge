package com.db.awmd.challenge.exception;

public class TransactionInProgressException extends RuntimeException{
  private static final long serialVersionUID = 4183121667083498917L;

  public TransactionInProgressException(String message) {
    super(message);
  }
}
