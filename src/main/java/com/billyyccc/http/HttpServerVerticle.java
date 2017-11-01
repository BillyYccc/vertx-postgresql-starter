/*
 * MIT License
 *
 * Copyright (c) 2017 Billy Yuan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.billyyccc.http;

import com.billyyccc.database.BookDatabaseService;
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

import static com.billyyccc.http.EndPoints.*;

/**
 * HttpServer Verticle deployed to provide REST services.
 *
 * @author Billy Yuan <billy112487983@gmail.com>
 */

public class HttpServerVerticle extends AbstractVerticle {
  private static final String CONFIG_HTTP_SERVER_PORT = "http.server.port";
  private static final String CONFIG_DB_EB_QUEUE = "library.db.queue";

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);

  private BookDatabaseService bookDatabaseService;

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    HttpServer httpServer = vertx.createHttpServer();

    bookDatabaseService = BookDatabaseService.createProxy(vertx, CONFIG_DB_EB_QUEUE);

    Router router = Router.router(vertx);

    router.route().handler(BodyHandler.create());
    router.get(GET_BOOKS).handler(new GetBooksHandler(bookDatabaseService));
    router.post(ADD_NEW_BOOK).handler(new AddBookHandler(bookDatabaseService));
    router.delete(DELETE_BOOK).handler(new DeleteBookHandler(bookDatabaseService));
    router.get(GET_BOOK_BY_ID).handler(new GetBookHandler(bookDatabaseService));

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
