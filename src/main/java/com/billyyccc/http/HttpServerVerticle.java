package com.billyyccc.http;

import com.billyyccc.http.handler.AddBookHandler;
import com.billyyccc.http.handler.DeleteBookHandler;
import com.billyyccc.http.handler.GetBookHandler;
import com.billyyccc.http.handler.GetBooksHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * HttpServer Verticle deployed to provide REST services.
 *
 * @author Billy Yuan <billy112487983@gmail.com>
 */

public class HttpServerVerticle extends AbstractVerticle {
  public static final String CONFIG_HTTP_SERVER_PORT = "http.server.port";

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    HttpServer httpServer = vertx.createHttpServer();

    Router router = Router.router(vertx);

    router.route().handler(BodyHandler.create());
    router.get(ApiRoutes.GET_BOOKS).handler(new GetBooksHandler());
    router.post(ApiRoutes.ADD_NEW_BOOK).handler(new AddBookHandler());
    router.delete(ApiRoutes.DELETE_BOOK).handler(new DeleteBookHandler());
    router.get(ApiRoutes.GET_BOOK).handler(new GetBookHandler());

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
