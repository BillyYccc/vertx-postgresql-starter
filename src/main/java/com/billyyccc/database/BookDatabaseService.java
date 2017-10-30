package com.billyyccc.database;

import com.billyyccc.database.impl.BookDatabaseServiceImpl;
import com.billyyccc.entity.Book;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLClient;

/**
 * @author Billy Yuan <billy112487983@gmail.com>
 */

@ProxyGen
@VertxGen
public interface BookDatabaseService {

  static BookDatabaseService create(SQLClient dbClient, Handler<AsyncResult<BookDatabaseService>> resultHandler) {
    return new BookDatabaseServiceImpl(dbClient, resultHandler);
  }

  static BookDatabaseService createProxy(Vertx vertx, String address) {
    return new BookDatabaseServiceVertxEBProxy(vertx, address);
  }

  @Fluent
  BookDatabaseService addNewBook(Book book, Handler<AsyncResult<Void>> resultHandler);

  @Fluent
  BookDatabaseService deleteBookById(int id, Handler<AsyncResult<Void>> resultHandler);

  @Fluent
  BookDatabaseService getBookById(int id, Handler<AsyncResult<JsonObject>> resultHandler);

  @Fluent
  BookDatabaseService getBooks(Book book, Handler<AsyncResult<JsonArray>> resultHandler);
}
