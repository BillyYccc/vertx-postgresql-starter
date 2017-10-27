package com.billyyccc.http.handler;

import com.billyyccc.entity.Book;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

/**
 * This class is handler for adding a new book.
 *
 * @author Billy Yuan <billy112487983@gmail.com>
 */

public class AddBookHandler implements Handler<RoutingContext> {
  @Override
  public void handle(RoutingContext routingContext) {

    Book book = Json.decodeValue(routingContext.getBodyAsString("UTF-8"), Book.class);

    //TODO need database service
    //dbService.addNewBook(book);


    routingContext.response().setStatusCode(200)
      .putHeader("content-type", "application/json; charset=UTF-8")
      .end(book.toString());

  }
}
