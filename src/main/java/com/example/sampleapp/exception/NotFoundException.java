package com.example.sampleapp.exception;

public class NotFoundException extends RuntimeException {
  private static final long serialVersionUID = 4155543840473792898L;

  public NotFoundException(final String message) {
    super(message);
  }
}
