package com.billyyccc.http;

/**
 * This class is for defining routes for REST APIs.
 * @author Billy Yuan <billy112487983@gmail.com>
 */

final class ApiRoutes {
  static final String GET_BOOKS = "/books";
  static final String ADD_NEW_BOOK = "/books";
  static final String GET_BOOK = "/book/:bookid";
  static final String DELETE_BOOK = "/book/:bookid";

  private ApiRoutes() {
    // No instance of this class allowed
  }
}
