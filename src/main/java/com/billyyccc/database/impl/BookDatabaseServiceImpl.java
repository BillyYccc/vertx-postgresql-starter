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
import com.julienviet.reactivex.pgclient.PgPool;
import com.julienviet.reactivex.pgclient.PgResult;
import com.julienviet.reactivex.pgclient.Tuple;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.time.LocalDate;

import static com.billyyccc.database.utils.BookDatabaseServiceUtils.*;

/**
 * @author Billy Yuan <billy112487983@gmail.com>
 */

public class BookDatabaseServiceImpl implements BookDatabaseService {
  private static final String SQL_ADD_NEW_BOOK = "INSERT INTO book VALUES ($1, $2, $3, $4)";
  private static final String SQL_DELETE_BOOK_BY_ID = "DELETE FROM book WHERE id = $1";
  private static final String SQL_FIND_BOOK_BY_ID = "SELECT * FROM book WHERE id = $1";
  private static final String SQL_UPSERT_BOOK_BY_ID = "INSERT INTO book VALUES($1, $2, $3, $4) " +
    "ON CONFLICT(id) DO UPDATE SET title = $2, category = $3, publication_date = $4";
  private static final String SQL_FIND_ALL_BOOKS = "SELECT * FROM book WHERE TRUE";

  private static final Logger LOGGER = LoggerFactory.getLogger(BookDatabaseServiceImpl.class);

  private final PgPool pgConnectionPool;

  public BookDatabaseServiceImpl(com.julienviet.pgclient.PgPool pgPool, Handler<AsyncResult<BookDatabaseService>> resultHandler) {
    pgConnectionPool = new PgPool(pgPool);
    pgConnectionPool.rxGetConnection()
      .flatMap(pgConnection -> pgConnection
        .rxQuery(SQL_FIND_ALL_BOOKS)
        .doAfterTerminate(pgConnection::close))
      .subscribe(result -> resultHandler.handle(Future.succeededFuture(this)),
        throwable -> {
          LOGGER.error("Can not open a database connection", throwable);
          resultHandler.handle(Future.failedFuture(throwable));
        });
  }

  @Override
  public BookDatabaseService addNewBook(Book book, Handler<AsyncResult<Void>> resultHandler) {
    pgConnectionPool.rxPreparedQuery(SQL_ADD_NEW_BOOK,
      Tuple.of(book.getId(), book.getTitle(), book.getCategory(), LocalDate.parse(book.getPublicationDate())))
      .subscribe(updateResult -> resultHandler.handle(Future.succeededFuture()),
        throwable -> {
          LOGGER.error("Failed to add a new book into database", throwable);
          resultHandler.handle(Future.failedFuture(throwable));
        });
    return this;
  }

  @Override
  public BookDatabaseService deleteBookById(int id, Handler<AsyncResult<Void>> resultHandler) {
    pgConnectionPool.rxPreparedQuery(SQL_DELETE_BOOK_BY_ID, Tuple.of(id))
      .subscribe(updateResult -> resultHandler.handle(Future.succeededFuture()),
        throwable -> {
          LOGGER.error("Failed to delete the book by id " + id, throwable);
          resultHandler.handle(Future.failedFuture(throwable));
        });
    return this;
  }

  @Override
  public BookDatabaseService getBookById(int id, Handler<AsyncResult<JsonObject>> resultHandler) {
    pgConnectionPool.rxPreparedQuery(SQL_FIND_BOOK_BY_ID, Tuple.of(id))
      .map(PgResult::getDelegate)
      .subscribe(pgResult -> {
        JsonArray jsonArray = transformPgResultToJson(pgResult);
        if (jsonArray.size() == 0) {
          resultHandler.handle(Future.succeededFuture(new JsonObject()));
        } else {
          JsonObject dbResponse = jsonArray.getJsonObject(0);
          resultHandler.handle(Future.succeededFuture(dbResponse));
        }
      }, throwable -> {
        LOGGER.error("Failed to get the book by id " + id, throwable);
        resultHandler.handle(Future.failedFuture(throwable));
      });
    return this;
  }

  @Override
  public BookDatabaseService getBooks(Book book, Handler<AsyncResult<JsonArray>> resultHandler) {
    DynamicQuery dynamicQuery = generateDynamicQuery(SQL_FIND_ALL_BOOKS, book);
    String preparedQuery = dynamicQuery.getPreparedQuery();
    Tuple params = dynamicQuery.getParams();

    pgConnectionPool.rxPreparedQuery(preparedQuery, params)
      .map(PgResult::getDelegate)
      .subscribe(
        pgResult -> {
          JsonArray jsonArray = transformPgResultToJson(pgResult);
          resultHandler.handle(Future.succeededFuture(jsonArray));
        },
        throwable -> {
          LOGGER.error("Failed to get the filtered books by the following conditions"
            + params.toString(), throwable);
          resultHandler.handle(Future.failedFuture(throwable));
        });
    return this;
  }

  @Override
  public BookDatabaseService upsertBookById(int id, Book book, Handler<AsyncResult<Void>> resultHandler) {
    pgConnectionPool.rxPreparedQuery(SQL_UPSERT_BOOK_BY_ID,
      Tuple.of(id, book.getTitle(), book.getCategory(), LocalDate.parse(book.getPublicationDate())))
      .subscribe(
        updateResult -> resultHandler.handle(Future.succeededFuture()),
        throwable -> {
          LOGGER.error("Failed to upsert the book by id " + book.getId(), throwable);
          resultHandler.handle(Future.failedFuture(throwable));
        }
      );
    return this;
  }
}
