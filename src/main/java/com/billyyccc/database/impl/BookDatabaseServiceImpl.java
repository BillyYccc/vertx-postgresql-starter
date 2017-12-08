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
import com.julienviet.pgclient.PgIterator;
import com.julienviet.pgclient.PgPoolOptions;
import com.julienviet.reactivex.pgclient.PgClient;
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

import java.util.List;
import java.util.Optional;

/**
 * @author Billy Yuan <billy112487983@gmail.com>
 */

public class BookDatabaseServiceImpl implements BookDatabaseService {
  private static final String SQL_ADD_NEW_BOOK = "INSERT INTO BOOK VALUES ($1, $2, $3, $4)";
  private static final String SQL_DELETE_BOOK_BY_ID = "DELETE FROM BOOK WHERE ID = $1";
  private static final String SQL_FIND_BOOK_BY_ID = "SELECT * FROM BOOK WHERE ID = $1";
  private static final String SQL_UPSERT_BOOK_BY_ID = "INSERT INTO BOOK VALUES($1, $2, $3, $4) " +
    "ON CONFLICT(ID) DO UPDATE SET TITLE = $2, CATEGORY = $3, PUBLICATION_DATE = $4";
  private static final String SQL_FIND_ALL_BOOKS = "SELECT * FROM BOOK WHERE TRUE";
  private static final String SQL_FIND_BOOKS_CONDITION_BY_TITLE = " AND TITLE = $";
  private static final String SQL_FIND_BOOKS_CONDITION_BY_CATEGORY = " AND CATEGORY = $";
  private static final String SQL_FIND_BOOKS_CONDITION_BY_PUBLICATION_DATE = " AND PUBLICATION_DATE = $";

  private static final Logger LOGGER = LoggerFactory.getLogger(BookDatabaseServiceImpl.class);

  private final PgPool pgConnectionPool;

  public BookDatabaseServiceImpl(com.julienviet.pgclient.PgClient pgClient, Handler<AsyncResult<BookDatabaseService>> resultHandler) {
    PgClient rxPgClient = new PgClient(pgClient);
    pgConnectionPool = rxPgClient.createPool(new PgPoolOptions().setMaxSize(20));
    pgConnectionPool.rxGetConnection()
      .flatMap(pgConnection -> pgConnection
        .createQuery(SQL_FIND_ALL_BOOKS)
        .rxExecute()
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
      Tuple.of(book.getId(), book.getTitle(), book.getCategory(), book.getPublicationDate()))
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
      .map(this::pgResultToJson)
      .subscribe(jsonArray -> {
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
    Optional<String> title = Optional.ofNullable(book.getTitle());
    Optional<String> category = Optional.ofNullable(book.getCategory());
    Optional<String> publicationDate = Optional.ofNullable(book.getPublicationDate());

    // Concat the SQL by conditions
    // TODO it bothers you when you build dynamic sql manually
    int count = 0;
    String dynamicSql = SQL_FIND_ALL_BOOKS;
    Tuple params = Tuple.tuple();
    if (title.isPresent()) {
      count++;
      dynamicSql += SQL_FIND_BOOKS_CONDITION_BY_TITLE;
      dynamicSql += count;
      params.addString(title.get());
    }
    if (category.isPresent()) {
      count++;
      dynamicSql += SQL_FIND_BOOKS_CONDITION_BY_CATEGORY;
      dynamicSql += count;
      params.addString(category.get());
    }
    if (publicationDate.isPresent()) {
      count++;
      dynamicSql += SQL_FIND_BOOKS_CONDITION_BY_PUBLICATION_DATE;
      dynamicSql += count;
      params.addString(publicationDate.get());
    }
    pgConnectionPool.rxPreparedQuery(dynamicSql, params)
      .map(PgResult::getDelegate)
      .map(this::pgResultToJson)
      .subscribe(
        jsonArray -> resultHandler.handle(Future.succeededFuture(jsonArray)),
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
      Tuple.of(id, book.getTitle(), book.getCategory(), book.getPublicationDate()))
      .subscribe(
        updateResult -> resultHandler.handle(Future.succeededFuture()),
        throwable -> {
          LOGGER.error("Failed to upsert the book by id " + book.getId(), throwable);
          resultHandler.handle(Future.failedFuture(throwable));
        }
      );
    return this;
  }

  // Transfer pgResult To json
  // TODO use JSONB in PG to get rid of data transfer(pgResult->DTO->JSON->REST API)
  private JsonArray pgResultToJson(com.julienviet.pgclient.PgResult pgResult) {
    PgIterator pgIterator = pgResult.iterator();
    JsonArray jsonArray = new JsonArray();
    List<String> columnName = pgResult.columnsNames();
    while (pgIterator.hasNext()) {
      JsonObject row = new JsonObject();
      com.julienviet.pgclient.Tuple rowValue = (com.julienviet.pgclient.Tuple) pgIterator.next();
      row.put(columnName.get(0), rowValue.getInteger(0));
      row.put(columnName.get(1), rowValue.getString(1));
      row.put(columnName.get(2), rowValue.getString(2));
      row.put(columnName.get(3), rowValue.getTemporal(3).toString());
      jsonArray.add(row);
    }
    return jsonArray;
  }
}
