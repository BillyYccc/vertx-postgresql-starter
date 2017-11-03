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

package com.billyyccc.database.impl;

import com.billyyccc.database.BookDatabaseService;
import com.billyyccc.entity.Book;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;

/**
 * @author Billy Yuan <billy112487983@gmail.com>
 */

public class BookDatabaseServiceImpl implements BookDatabaseService {
  private static final Logger LOGGER = LoggerFactory.getLogger(BookDatabaseServiceImpl.class);

  private final SQLClient dbClient;

  public BookDatabaseServiceImpl(SQLClient dbClient, Handler<AsyncResult<BookDatabaseService>> resultHandler) {
    this.dbClient = dbClient;
    this.dbClient.getConnection(ar -> {
      if (ar.failed()) {
        LOGGER.error("Can not open a database connection", ar.cause());
        resultHandler.handle(Future.failedFuture(ar.cause()));
      } else {
        SQLConnection dbConnection = ar.result();
        dbConnection.close();
        resultHandler.handle(Future.succeededFuture(this));
      }
    });
  }

  @Override
  public BookDatabaseService addNewBook(Book book, Handler<AsyncResult<Void>> resultHandler) {
    return this;
  }

  @Override
  public BookDatabaseService deleteBookById(int id, Handler<AsyncResult<Void>> resultHandler) {
    return this;
  }

  @Override
  public BookDatabaseService getBookById(int id, Handler<AsyncResult<JsonObject>> resultHandler) {
    return this;
  }

  @Override
  public BookDatabaseService getBooks(Book book, Handler<AsyncResult<JsonArray>> resultHandler) {
    return this;
  }
}
