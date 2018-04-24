package com.billyyccc.api.exception;

/**
 * This class is for defining exception of bad request.
 *
 * @author Billy Yuan <billy112487983@gmail.com>
 */

public class BadRequestException extends RuntimeException {
  public BadRequestException(Throwable throwable) {
    super(throwable);
  }

  public BadRequestException(String message) {
    super(message);
  }

  public BadRequestException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
