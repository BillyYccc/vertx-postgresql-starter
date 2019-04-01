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
import com.billyyccc.database.utils.RowCollectors;
import com.billyyccc.entity.Book;
import io.reactiverse.pgclient.Row;
import io.reactiverse.reactivex.pgclient.PgPool;
import io.reactiverse.reactivex.pgclient.Tuple;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collector;

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

  private final static Collector<Row, ?, JsonArray> BOOK_JSON_ARRAY_COLLECTOR = RowCollectors.jsonArrayCollector(
    row -> {
      JsonObject jsonObject = new JsonObject();
      jsonObject.put("id", row.getInteger("id"));
      jsonObject.put("title", row.getString("title"));
      jsonObject.put("category", row.getString("category"));
      LocalDate publicationDate = row.getLocalDate("publication_date");
      if (publicationDate != null) {
        jsonObject.put("publicationDate", publicationDate.format(DateTimeFormatter.ISO_DATE));
      } else {
        jsonObject.put("publicationDate", publicationDate);
      }
      return jsonObject;
    });

  private static final Logger LOGGER = LoggerFactory.getLogger(BookDatabaseServiceImpl.class);

  private final PgPool pgConnectionPool;

  public BookDatabaseServiceImpl(io.reactiverse.pgclient.PgPool pgPool, Handler<AsyncResult<BookDatabaseService>> resultHandler) {
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
    //TODO when Rxi-fied API supports wildcard type, then we can use Rxi-fied collector API OOTB.
    pgConnectionPool.getDelegate().preparedQuery(SQL_FIND_BOOK_BY_ID, io.reactiverse.pgclient.Tuple.of(id), BOOK_JSON_ARRAY_COLLECTOR, ar -> {
      if (ar.succeeded()) {
        JsonArray jsonArray = ar.result().value();
        if (jsonArray.size() == 0) {
          resultHandler.handle(Future.succeededFuture(emptyJsonObject()));
        } else {
          JsonObject dbResponse = jsonArray.getJsonObject(0);
          resultHandler.handle(Future.succeededFuture(dbResponse));
        }
      } else {
        LOGGER.error("Failed to get the book by id " + id, ar.cause());
        resultHandler.handle(Future.failedFuture(ar.cause()));
      }
    });
//    pgConnectionPool.rxPreparedQuery(SQL_FIND_BOOK_BY_ID, Tuple.of(id))
//      .map(PgRowSet::getDelegate)
//      .subscribe(pgRowSet -> {
//        JsonArray jsonArray = toJsonArray(pgRowSet);
//        if (jsonArray.size() == 0) {
//          resultHandler.handle(Future.succeededFuture(emptyJsonObject()));
//        } else {
//          JsonObject dbResponse = jsonArray.getJsonObject(0);
//          resultHandler.handle(Future.succeededFuture(dbResponse));
//        }
//      }, throwable -> {
//        LOGGER.error("Failed to get the book by id " + id, throwable);
//        resultHandler.handle(Future.failedFuture(throwable));
//      });
    return this;
  }

  @Override
  public BookDatabaseService getBooks(Book book, Handler<AsyncResult<JsonArray>> resultHandler) {
    DynamicQuery dynamicQuery = generateDynamicQuery(SQL_FIND_ALL_BOOKS, book);
    String preparedQuery = dynamicQuery.getPreparedQuery();
    Tuple params = dynamicQuery.getParams();

    //TODO when Rxi-fied API supports wildcard type, then we can use Rxi-fied collector API OOTB.
    pgConnectionPool.getDelegate().preparedQuery(preparedQuery, params.getDelegate(), BOOK_JSON_ARRAY_COLLECTOR, ar -> {
      if (ar.succeeded()) {
        JsonArray jsonArray = ar.result().value();
        resultHandler.handle(Future.succeededFuture(jsonArray));
      } else {
        LOGGER.error("Failed to get the filtered books by the following conditions"
          + params.toString(), ar.cause());
        resultHandler.handle(Future.failedFuture(ar.cause()));
      }
    });
//    pgConnectionPool.rxPreparedQuery(preparedQuery, params)
//      .map(PgRowSet::getDelegate)
//      .subscribe(
//        pgRowSet -> {
//          JsonArray jsonArray = toJsonArray(pgRowSet);
//          resultHandler.handle(Future.succeededFuture(jsonArray));
//        },
//        throwable -> {
//          LOGGER.error("Failed to get the filtered books by the following conditions"
//            + params.toString(), throwable);
//          resultHandler.handle(Future.failedFuture(throwable));
//        });
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
