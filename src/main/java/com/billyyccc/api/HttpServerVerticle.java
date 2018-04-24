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

package com.billyyccc.api;

import com.billyyccc.api.handler.BookApis;
import com.billyyccc.api.handler.FailureHandler;
import com.billyyccc.database.reactivex.BookDatabaseService;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.api.validation.HTTPRequestValidationHandler;
import io.vertx.reactivex.ext.web.handler.BodyHandler;

import static com.billyyccc.api.EndPoints.*;
import static com.billyyccc.api.handler.HttpRequestValidator.*;
import static com.billyyccc.database.BookDatabaseService.*;

/**
 * HttpServer Verticle deployed to provide REST services.
 *
 * @author Billy Yuan <billy112487983@gmail.com>
 */

public class HttpServerVerticle extends AbstractVerticle {
  private static final String CONFIG_HTTP_SERVER_PORT = "http.server.port";
  private static final String CONFIG_DB_EB_QUEUE = "library.db.eb.address";

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    HttpServer httpServer = vertx.createHttpServer();

    BookDatabaseService bookDatabaseService = createProxy(vertx.getDelegate(), config().getString(CONFIG_DB_EB_QUEUE));

    Router router = Router.router(vertx);

    router.route().handler(BodyHandler.create());

    router.route().handler(HTTPRequestValidationHandler.create().addExpectedContentType("application/json"));

    router.get(GET_BOOKS).handler(addBookValidationHandler())
      .handler(BookApis.getBooksHandler(bookDatabaseService));

    router.post(ADD_NEW_BOOK).handler(BookApis.addBookHandler(bookDatabaseService));

    router.delete(DELETE_BOOK_BY_ID).handler(deleteBookByIdValidationHandler())
      .handler(BookApis.deleteBookByIdHandler(bookDatabaseService));

    router.get(GET_BOOK_BY_ID).handler(getBookByIdValidationHandler())
      .handler(BookApis.getBookByIdHandler(bookDatabaseService));

    router.put(UPDATE_BOOK_BY_ID).handler(upsertBookByIdValidationHandler())
      .handler(BookApis.upsertBookByIdHandler(bookDatabaseService));

    router.route().failureHandler(new FailureHandler());

    int httpServerPort = config().getInteger(CONFIG_HTTP_SERVER_PORT, 8080);
    httpServer.requestHandler(router::accept)
      .rxListen(httpServerPort)
      .subscribe(server -> {
          LOGGER.info("HTTP server is running on port " + httpServerPort);
          startFuture.complete();
        },
        throwable -> {
          LOGGER.error("Fail to start a HTTP server ", throwable);
          startFuture.fail(throwable);
        });
  }
}
