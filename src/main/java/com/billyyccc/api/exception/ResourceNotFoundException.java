package com.billyyccc.api.exception;

/**
 * This class is for defining exception of resource not found.
 *
 * @author Billy Yuan <billy112487983@gmail.com>
 */

public class ResourceNotFoundException extends RuntimeException {
  public ResourceNotFoundException(Throwable throwable) {
    super(throwable);
  }

  public ResourceNotFoundException(String message) {
    super(message);
  }

  public ResourceNotFoundException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
