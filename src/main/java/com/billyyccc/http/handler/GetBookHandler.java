package com.billyyccc.http.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * This class is handler for getting the specified book by bookId.
 *
 * @author Billy Yuan <billy112487983@gmail.com>
 */

public class GetBookHandler implements Handler<RoutingContext> {
  @Override
  public void handle(RoutingContext routingContext) {
    int bookId = Integer.valueOf(routingContext.pathParam("bookid"));

    //TODO need database service
    //Book book = dbService.getBookById(bookId);

    routingContext.response().setStatusCode(200)
      .putHeader("content-type", "application/json; charset=UTF-8")
      .end();

  }
}
