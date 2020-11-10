package com.example.sampleapp.exception;

public class CorruptDataException extends RuntimeException {
  private static final long serialVersionUID = 5808988061924788923L;

  public CorruptDataException(final String message) {
    super(message);
  }
}
