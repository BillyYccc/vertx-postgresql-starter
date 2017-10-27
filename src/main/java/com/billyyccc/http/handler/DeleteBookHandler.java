package com.billyyccc.http.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * This class is handler for deleting a existing book.
 *
 * @author Billy Yuan <billy112487983@gmail.com>
 */

public class DeleteBookHandler implements Handler<RoutingContext> {
  @Override
  public void handle(RoutingContext routingContext) {
    int bookId = Integer.valueOf(routingContext.pathParam("bookid"));

    //TODO need database service
    //dbService.deleteBookById(bookId);

    routingContext.response().setStatusCode(200)
      .putHeader("content-type", "application/json; charset=UTF-8")
      .end();
  }
}
