package com.billyyccc.http.utils;

import io.vertx.reactivex.ext.web.RoutingContext;

/**
 * This util class is set for common rest responses.
 *
 * @author Billy Yuan <billy112487983@gmail.com>
 */

public class RestResponseUtil {
  public static void restResponse(RoutingContext routingContext, int statusCode, String body) {
    routingContext.response().putHeader("Content-Type", "application/json; charset=utf-8");
    routingContext.response().setStatusCode(statusCode)
      .end(body);
  }

  public static void restResponse(RoutingContext routingContext, int statusCode) {
    restResponse(routingContext, statusCode, "");
  }
}
