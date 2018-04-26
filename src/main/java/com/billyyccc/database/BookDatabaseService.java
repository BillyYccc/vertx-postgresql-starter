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

package com.billyyccc.database;

import com.billyyccc.database.impl.BookDatabaseServiceImpl;
import com.billyyccc.entity.Book;
import io.reactiverse.pgclient.PgPool;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * @author Billy Yuan <billy112487983@gmail.com>
 */

@ProxyGen
@VertxGen
public interface BookDatabaseService {

  @GenIgnore
  static BookDatabaseService create(PgPool pgPool, Handler<AsyncResult<BookDatabaseService>> resultHandler) {
    return new BookDatabaseServiceImpl(pgPool, resultHandler);
  }

  @GenIgnore
  static com.billyyccc.database.reactivex.BookDatabaseService createProxy(Vertx vertx, String address) {
    return new com.billyyccc.database.reactivex.BookDatabaseService(new BookDatabaseServiceVertxEBProxy(vertx, address));
  }

  @Fluent
  BookDatabaseService addNewBook(Book book, Handler<AsyncResult<Void>> resultHandler);

  @Fluent
  BookDatabaseService deleteBookById(int id, Handler<AsyncResult<Void>> resultHandler);

  @Fluent
  BookDatabaseService getBookById(int id, Handler<AsyncResult<JsonObject>> resultHandler);

  @Fluent
  BookDatabaseService getBooks(Book book, Handler<AsyncResult<JsonArray>> resultHandler);

  @Fluent
  BookDatabaseService upsertBookById(int id, Book book, Handler<AsyncResult<Void>> resultHandler);
}
