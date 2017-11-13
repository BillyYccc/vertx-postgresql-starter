package com.billyyccc.http.handler;

import com.billyyccc.http.exception.BadRequestException;
import com.billyyccc.http.exception.ResourceNotFoundException;
import io.vertx.core.Handler;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.validation.ValidationException;
import io.vertx.reactivex.ext.web.RoutingContext;

import static com.billyyccc.http.utils.RestApiUtil.*;

/**
 * This class is handler for handling an exception.
 *
 * @author Billy Yuan <billy112487983@gmail.com>
 */

public class FailureHandler implements Handler<RoutingContext> {
  @Override
  public void handle(RoutingContext routingContext) {
    Throwable failure = routingContext.failure();
    if (failure instanceof ValidationException) {
      restResponse(routingContext, 400, errorMessageToErrorBody("Validation failed"));
    } else if (failure instanceof BadRequestException) {
      restResponse(routingContext, 400, errorMessageToErrorBody("Invalid Request"));
    } else if (failure instanceof ResourceNotFoundException) {
      restResponse(routingContext, 404, errorMessageToErrorBody(failure.getMessage()));
    } else if (failure instanceof DecodeException) {
      restResponse(routingContext, 400, errorMessageToErrorBody("Problems parsing JSON"));
    } else {
      restResponse(routingContext, 500, errorMessageToErrorBody(failure.getMessage()));
    }
  }

  private String errorMessageToErrorBody(String message) {
    return new JsonObject().put("message", message).toString();
  }
}
