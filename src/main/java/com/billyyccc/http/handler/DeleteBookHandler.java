package com.billyyccc.http.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * This class is handler for deleting a existing book.
 * @author Billy Yuan <billy112487983@gmail.com>
 */

public class DeleteBookHandler implements Handler<RoutingContext> {
  @Override
  public void handle(RoutingContext routingContext) {
    //TODO need database service
  }
}
