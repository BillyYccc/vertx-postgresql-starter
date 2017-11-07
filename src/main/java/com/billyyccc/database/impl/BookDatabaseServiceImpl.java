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
import com.julienviet.pgclient.PgClient;
import com.julienviet.pgclient.PgConnection;
import com.julienviet.pgclient.PgPool;
import com.julienviet.pgclient.PgPoolOptions;
import com.julienviet.pgclient.PgPreparedStatement;
import com.julienviet.pgclient.PgQuery;
import com.julienviet.pgclient.PgUpdate;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.Arrays;
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
    "ON CONFLICT(ID) DO UPDATE SET TITLE = $5, CATEGORY = $6, PUBLICATIONDATE = $7";
  private static final String SQL_FIND_ALL_BOOKS = "SELECT * FROM BOOK WHERE TRUE";
  private static final String SQL_FIND_BOOKS_CONDITION_BY_TITLE = " AND TITLE = $";
  private static final String SQL_FIND_BOOKS_CONDITION_BY_CATEGORY = " AND CATEGORY = $";
  private static final String SQL_FIND_BOOKS_CONDITION_BY_PUBLICATIONDATE = " AND PUBLICATIONDATE = $";

  private static final Logger LOGGER = LoggerFactory.getLogger(BookDatabaseServiceImpl.class);

  private final PgPool pgConnectionPool;

  public BookDatabaseServiceImpl(PgClient pgClient, Handler<AsyncResult<BookDatabaseService>> resultHandler) {
    pgConnectionPool = pgClient.createPool(new PgPoolOptions()
      .setMaxSize(20));
    this.pgConnectionPool.getConnection(ar -> {
      if (ar.failed()) {
        LOGGER.error("Can not open a database connection", ar.cause());
        resultHandler.handle(Future.failedFuture(ar.cause()));
      } else {
        resultHandler.handle(Future.succeededFuture(this));
      }
    });
  }

  @Override
  public BookDatabaseService addNewBook(Book book, Handler<AsyncResult<Void>> resultHandler) {
    pgConnectionPool.getConnection(res -> {
      if (res.succeeded()) {
        PgConnection pgConnection = res.result();
        PgPreparedStatement preparedStatement = pgConnection.prepare(SQL_ADD_NEW_BOOK);
        PgUpdate update = preparedStatement.update(Arrays.asList(book.getId(), book.getTitle(), book.getCategory(), book.getPublicationDate()));
        update.execute(ar -> {
          if (ar.failed()) {
            LOGGER.error("Failed to add a new book into database", ar.cause());
            resultHandler.handle(Future.failedFuture(ar.cause()));
          } else {
            resultHandler.handle(Future.succeededFuture());
          }
          pgConnection.close();
        });
      } else {
        LOGGER.error("Failed to get a connection to database", res.cause());
        resultHandler.handle(Future.failedFuture(res.cause()));
      }
    });
    return this;
  }

  @Override
  public BookDatabaseService deleteBookById(int id, Handler<AsyncResult<Void>> resultHandler) {
    pgConnectionPool.getConnection(res -> {
      if (res.succeeded()) {
        PgConnection pgConnection = res.result();
        PgPreparedStatement preparedStatement = pgConnection.prepare(SQL_DELETE_BOOK_BY_ID);
        PgUpdate update = preparedStatement.update(id);
        update.execute(ar -> {
          if (ar.failed()) {
            LOGGER.error("Failed to delete the book by id " + id, ar.cause());
            resultHandler.handle(Future.failedFuture(ar.cause()));
          } else {
            resultHandler.handle(Future.succeededFuture());
          }
          pgConnection.close();
        });
      } else {
        LOGGER.error("Failed to get a connection to database", res.cause());
        resultHandler.handle(Future.failedFuture(res.cause()));
      }
    });
    return this;
  }

  @Override
  public BookDatabaseService getBookById(int id, Handler<AsyncResult<JsonObject>> resultHandler) {
    pgConnectionPool.getConnection(res -> {
      if (res.succeeded()) {
        PgConnection pgConnection = res.result();
        PgPreparedStatement preparedStatement = pgConnection.prepare(SQL_FIND_BOOK_BY_ID);
        PgQuery query = preparedStatement.query(id);
        query.execute(ar -> {
          if (ar.failed()) {
            LOGGER.error("Failed to get the book by id " + id, ar.cause());
            resultHandler.handle(Future.failedFuture(ar.cause()));
          } else {
            JsonObject resultRow = ar.result().getRows().get(0);
            resultHandler.handle(Future.succeededFuture(resultRow));
          }
          pgConnection.close();
        });
      } else {
        LOGGER.error("Failed to get a connection to database", res.cause());
        resultHandler.handle(Future.failedFuture(res.cause()));
      }
    });
    return this;
  }

  @Override
  public BookDatabaseService getBooks(Book book, Handler<AsyncResult<JsonArray>> resultHandler) {
    Optional<String> title = Optional.ofNullable(book.getTitle());
    Optional<String> category = Optional.ofNullable(book.getCategory());
    Optional<String> publicationDate = Optional.ofNullable(book.getPublicationDate());

    // Concat the SQL by conditions
    // TODO the logic is not so good
    int condition_count = 1;
    String dynamicSql = SQL_FIND_ALL_BOOKS;
    List<Object> params = new LinkedList<>();
    if (title.isPresent()) {
      dynamicSql += SQL_FIND_BOOKS_CONDITION_BY_TITLE;
      dynamicSql += condition_count;
      condition_count++;
      params.add(title.get());
    }
    if (category.isPresent()) {
      dynamicSql += SQL_FIND_BOOKS_CONDITION_BY_CATEGORY;
      dynamicSql += condition_count;
      condition_count++;
      params.add(category.get());
    }
    if (publicationDate.isPresent()) {
      dynamicSql += SQL_FIND_BOOKS_CONDITION_BY_PUBLICATIONDATE;
      dynamicSql += condition_count;
      params.add(publicationDate.get());
    }
    final String finalDynamicSql = dynamicSql;
    pgConnectionPool.getConnection(res -> {
      if (res.succeeded()) {
        PgConnection pgConnection = res.result();
        PgPreparedStatement preparedStatement = pgConnection.prepare(finalDynamicSql);
        PgQuery query = preparedStatement.query(params);
        query.execute(ar -> {
          if (ar.failed()) {
            LOGGER.error("Failed to get the filtered books by the following conditions"
              + params.toString(), ar.cause());
            resultHandler.handle(Future.failedFuture(ar.cause()));
          } else {
            JsonArray books = new JsonArray(ar.result().getRows());
            resultHandler.handle(Future.succeededFuture(books));
          }
          pgConnection.close();
        });
      } else {
        LOGGER.error("Failed to get a connection to database", res.cause());
        resultHandler.handle(Future.failedFuture(res.cause()));
      }
    });
    return this;
  }

  @Override
  public BookDatabaseService upsertBookById(int id, Book book, Handler<AsyncResult<Void>> resultHandler) {
    List<Object> params = new LinkedList<>();
    params.add(id);
    params.add(book.getTitle());
    params.add(book.getCategory());
    params.add(book.getPublicationDate());
    params.add(book.getTitle());
    params.add(book.getCategory());
    params.add(book.getPublicationDate());

    pgConnectionPool.getConnection(res -> {
      if (res.succeeded()) {
        PgConnection pgConnection = res.result();
        PgPreparedStatement preparedStatement = pgConnection.prepare(SQL_UPSERT_BOOK_BY_ID);
        PgUpdate update = preparedStatement.update(params);
        update.execute(ar -> {
          if (ar.failed()) {
            LOGGER.error("Failed to upsert the book by id " + book.getId(), ar.cause());
            resultHandler.handle(Future.failedFuture(ar.cause()));
          } else {
            resultHandler.handle(Future.succeededFuture());
          }
          pgConnection.close();
        });
      } else {
        LOGGER.error("Failed to get a connection to database", res.cause());
        resultHandler.handle(Future.failedFuture(res.cause()));
      }
    });
    return this;
  }
}
