package com.billyyccc.http.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * This class is handler for adding a new book.
 * @author Billy Yuan <billy112487983@gmail.com>
 */

public class AddBookHandler implements Handler<RoutingContext> {
  @Override
  public void handle(RoutingContext routingContext) {
    //TODO need database service
  }
}
