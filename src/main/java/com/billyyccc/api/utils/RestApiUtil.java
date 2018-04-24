package com.billyyccc.api.utils;

import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;

/**
 * This util class is set for common rest APIs.
 *
 * @author Billy Yuan <billy112487983@gmail.com>
 */

public class RestApiUtil {
  public static void restResponse(RoutingContext routingContext, int statusCode, String body) {
    routingContext.response().putHeader("Content-Type", "application/json; charset=utf-8");
    routingContext.response().setStatusCode(statusCode)
      .end(body);
  }

  public static void restResponse(RoutingContext routingContext, int statusCode) {
    restResponse(routingContext, statusCode, "");
  }

  public static JsonObject dBJsonToRestJson(JsonObject dbJsonObject) {
    return new JsonObject(dbJsonObject.encodePrettily()
      .replace("publication_date", "publicationDate"));
  }

  public static JsonArray dBJsonToRestJson(JsonArray dbJsonArray) {
    return new JsonArray(dbJsonArray.encodePrettily()
      .replace("publication_date", "publicationDate"));
  }

  public static <T> T decodeBodyToObject(RoutingContext routingContext, Class<T> clazz) {
    try {
      return Json.decodeValue(routingContext.getBodyAsString("UTF-8"), clazz);
    } catch (DecodeException exception) {
      routingContext.fail(exception);
      return null;
    }
  }
}
