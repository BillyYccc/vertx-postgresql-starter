package com.billyyccc.http;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * @author Billy Yuan <billy112487983@gmail.com>
 */

public class HttpServerVerticle extends AbstractVerticle {
  public static final String CONFIG_HTTP_SERVER_PORT = "http.server.port";

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    HttpServer httpServer = vertx.createHttpServer();

    Router router = Router.router(vertx);

    router.get("/").handler((RoutingContext routingContext) ->
                                routingContext.response()
                                              .setStatusCode(200)
                                              .putHeader("content-type", "application/json; charset=utf-8")
                                              .end("{\n"
                                                   + "    \"book_id\": 1,\n"
                                                   + "    \"book_name\": \"Vertx\",\n"
                                                   + "    \"book_author\": \"billy\"\n"
                                                   + "}"));

    int httpServerPort = config().getInteger(CONFIG_HTTP_SERVER_PORT, 8080);
    httpServer.requestHandler(router::accept)
              .listen(httpServerPort, ar -> {
                if (ar.succeeded()) {
                  LOGGER.info("HTTP server is running on port " + httpServerPort);
                  startFuture.complete();
                } else {
                  LOGGER.error("Fail to start a HTTP server ", ar.cause());
                  startFuture.fail(ar.cause());
                }
              });

  }
}
