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
import com.julienviet.pgclient.PgPoolOptions;
import com.julienviet.reactivex.pgclient.PgClient;
import com.julienviet.reactivex.pgclient.PgPool;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.sql.ResultSet;

import java.util.LinkedList;
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
    "ON CONFLICT(ID) DO UPDATE SET TITLE = $2, CATEGORY = $3, PUBLICATIONDATE = $4";
  private static final String SQL_FIND_ALL_BOOKS = "SELECT * FROM BOOK WHERE TRUE";
  private static final String SQL_FIND_BOOKS_CONDITION_BY_TITLE = " AND TITLE = $";
  private static final String SQL_FIND_BOOKS_CONDITION_BY_CATEGORY = " AND CATEGORY = $";
  private static final String SQL_FIND_BOOKS_CONDITION_BY_PUBLICATIONDATE = " AND PUBLICATIONDATE = $";

  private static final Logger LOGGER = LoggerFactory.getLogger(BookDatabaseServiceImpl.class);

  private final PgClient pgClient;
  private final PgPool pgConnectionPool;

  public BookDatabaseServiceImpl(com.julienviet.pgclient.PgClient pgClient, Handler<AsyncResult<BookDatabaseService>> resultHandler) {
    this.pgClient = new PgClient(pgClient);
    pgConnectionPool = this.pgClient.createPool(new PgPoolOptions().setMaxSize(20));
    this.pgConnectionPool.rxGetConnection()
      .flatMap(pgConnection -> pgConnection
        .rxQuery(SQL_FIND_ALL_BOOKS)
        .doAfterTerminate(pgConnection::close)
      ).subscribe(
      resultSet -> {
        resultHandler.handle(Future.succeededFuture(this));
      },
      throwable -> {
        LOGGER.error("Can not open a database connection", throwable);
        resultHandler.handle(Future.failedFuture(throwable));
      });
  }

  @Override
  public BookDatabaseService addNewBook(Book book, Handler<AsyncResult<Void>> resultHandler) {
    pgConnectionPool.rxUpdate(SQL_ADD_NEW_BOOK,
      book.getId(), book.getTitle(), book.getCategory(), book.getPublicationDate())
      .subscribe(
        updateResult -> {
          resultHandler.handle(Future.succeededFuture());
        },
        throwable -> {
          LOGGER.error("Failed to add a new book into database", throwable);
          resultHandler.handle(Future.failedFuture(throwable));
        });
    return this;
  }

  @Override
  public BookDatabaseService deleteBookById(int id, Handler<AsyncResult<Void>> resultHandler) {
    pgConnectionPool.rxUpdate(SQL_DELETE_BOOK_BY_ID, id)
      .subscribe(
        updateResult -> {
          resultHandler.handle(Future.succeededFuture());
        },
        throwable -> {
          LOGGER.error("Failed to delete the book by id " + id, throwable);
          resultHandler.handle(Future.failedFuture(throwable));
        });
    return this;
  }

  @Override
  public BookDatabaseService getBookById(int id, Handler<AsyncResult<JsonObject>> resultHandler) {
    pgConnectionPool.rxQuery(SQL_FIND_BOOK_BY_ID, id)
      .subscribe(
        resultSet -> {
          JsonObject resultRow = resultSet.getRows().get(0);
          resultHandler.handle(Future.succeededFuture(resultRow));
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
    // TODO the logic is not so good :(
    int condition_count = 0;
    String dynamicSql = SQL_FIND_ALL_BOOKS;
    List<Object> params = new LinkedList<>();
    if (title.isPresent()) {
      condition_count++;
      dynamicSql += SQL_FIND_BOOKS_CONDITION_BY_TITLE;
      dynamicSql += condition_count;
      params.add(title.get());
    }
    if (category.isPresent()) {
      condition_count++;
      dynamicSql += SQL_FIND_BOOKS_CONDITION_BY_CATEGORY;
      dynamicSql += condition_count;
      params.add(category.get());
    }
    if (publicationDate.isPresent()) {
      condition_count++;
      dynamicSql += SQL_FIND_BOOKS_CONDITION_BY_PUBLICATIONDATE;
      dynamicSql += condition_count;
      params.add(publicationDate.get());
    }

    SingleObserver<? super ResultSet> singleObserver = new SingleObserver<ResultSet>() {
      @Override
      public void onSubscribe(Disposable disposable) {

      }

      @Override
      public void onSuccess(ResultSet resultSet) {
        JsonArray books = new JsonArray(resultSet.getRows());
        resultHandler.handle(Future.succeededFuture(books));
      }

      @Override
      public void onError(Throwable throwable) {
        LOGGER.error("Failed to get the filtered books by the following conditions"
          + params.toString(), throwable);
        resultHandler.handle(Future.failedFuture(throwable));
      }
    };
    switch (condition_count) {
      case 0:
        pgConnectionPool.rxQuery(dynamicSql).subscribe(singleObserver);
        break;
      case 1:
        pgConnectionPool.rxQuery(dynamicSql, params.get(0)).subscribe(singleObserver);
        break;
      case 2:
        pgConnectionPool.rxQuery(dynamicSql, params.get(0), params.get(1)).subscribe(singleObserver);
        break;
      case 3:
        pgConnectionPool.rxQuery(dynamicSql, params.get(0), params.get(1), params.get(2)).subscribe(singleObserver);
        break;
    }
    return this;
  }

  @Override
  public BookDatabaseService upsertBookById(int id, Book book, Handler<AsyncResult<Void>> resultHandler) {
    pgConnectionPool.rxUpdate(SQL_UPSERT_BOOK_BY_ID, id, book.getTitle(), book.getCategory(), book.getPublicationDate())
      .subscribe(
        updateResult -> {
          resultHandler.handle(Future.succeededFuture());
        },
        throwable -> {
          LOGGER.error("Failed to upsert the book by id " + book.getId(), throwable);
          resultHandler.handle(Future.failedFuture(throwable));
        }
      );
    return this;
  }
}
