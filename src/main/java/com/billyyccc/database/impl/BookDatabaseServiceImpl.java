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

import java.util.Optional;

/**
 * @author Billy Yuan <billy112487983@gmail.com>
 */

public class BookDatabaseServiceImpl implements BookDatabaseService {
  private static final String SQL_ADD_NEW_BOOK = "INSERT INTO BOOK VALUES (?, ?, ?, ?)";
  private static final String SQL_DELETE_BOOK_BY_ID = "DELETE FROM BOOK WHERE ID = ?";
  private static final String SQL_FIND_BOOK_BY_ID = "SELECT * FROM BOOK WHERE ID = ?";
  private static final String SQL_FIND_ALL_BOOKS = "SELECT * FROM BOOK WHERE TRUE";
  private static final String SQL_UPSERT_BOOK_BY_ID = "INSERT INTO BOOK VALUES(?, ?, ?, ?) " +
    "ON CONFLICT(ID) DO UPDATE SET TITLE = ?, CATEGORY = ?, PUBLICATIONDATE = ?";
  private static final String SQL_CONDITION_BY_TITLE = " AND TITLE = ?";
  private static final String SQL_CONDITION_BY_CATEGORY = " AND CATEGORY = ?";
  private static final String SQL_CONDITION_BY_PUBLICATIONDATE = " AND PUBLICATIONDATE = ?";

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
    JsonArray params = new JsonArray().add(book.getId())
      .add(book.getTitle())
      .add(book.getCategory())
      .add(book.getPublicationDate());
    dbClient.updateWithParams(SQL_ADD_NEW_BOOK, params, ar -> {
      if (ar.failed()) {
        LOGGER.error("Failed to add a new book into database", ar.cause());
        resultHandler.handle(Future.failedFuture(ar.cause()));
      } else {
        resultHandler.handle(Future.succeededFuture());
      }
    });
    return this;
  }

  @Override
  public BookDatabaseService deleteBookById(int id, Handler<AsyncResult<Void>> resultHandler) {
    JsonArray params = new JsonArray().add(id);
    dbClient.updateWithParams(SQL_DELETE_BOOK_BY_ID, params, ar -> {
      if (ar.failed()) {
        LOGGER.error("Failed to delete the book by id " + id, ar.cause());
        resultHandler.handle(Future.failedFuture(ar.cause()));
      } else {
        resultHandler.handle(Future.succeededFuture());
      }
    });
    return this;
  }

  @Override
  public BookDatabaseService getBookById(int id, Handler<AsyncResult<JsonObject>> resultHandler) {
    JsonArray params = new JsonArray().add(id);
    dbClient.queryWithParams(SQL_FIND_BOOK_BY_ID, params, ar -> {
      if (ar.failed()) {
        LOGGER.error("Failed to get the book by id " + id, ar.cause());
        resultHandler.handle(Future.failedFuture(ar.cause()));
      } else {
        JsonObject resultRow = ar.result().getRows().get(0);
        resultHandler.handle(Future.succeededFuture(resultRow));
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
    String dynamicSql = SQL_FIND_ALL_BOOKS;
    JsonArray params = new JsonArray();
    if (title.isPresent()) {
      dynamicSql += SQL_CONDITION_BY_TITLE;
      params.add(title.get());
    }
    if (category.isPresent()) {
      dynamicSql += SQL_CONDITION_BY_CATEGORY;
      params.add(category.get());
    }
    if (publicationDate.isPresent()) {
      dynamicSql += SQL_CONDITION_BY_PUBLICATIONDATE;
      params.add(publicationDate.get());
    }
    dbClient.queryWithParams(dynamicSql, params, ar -> {
      if (ar.failed()) {
        LOGGER.error("Failed to get the filtered books by the following conditions"
          + params.toString(), ar.cause());
        resultHandler.handle(Future.failedFuture(ar.cause()));
      } else {
        JsonArray books = new JsonArray(ar.result().getRows());
        resultHandler.handle(Future.succeededFuture(books));
      }
    });
    return this;
  }

  @Override
  public BookDatabaseService upsertBookById(Book book, Handler<AsyncResult<Void>> resultHandler) {
    JsonArray params = new JsonArray()
      .add(book.getId())
      .add(book.getTitle())
      .add(book.getCategory())
      .add(book.getPublicationDate())
      .add(book.getTitle())
      .add(book.getCategory())
      .add(book.getPublicationDate());
    dbClient.updateWithParams(SQL_UPSERT_BOOK_BY_ID, params, ar -> {
      if (ar.failed()) {
        LOGGER.error("Failed to upsert the book by id " + book.getId(), ar.cause());
        resultHandler.handle(Future.failedFuture(ar.cause()));
      } else {
        resultHandler.handle(Future.succeededFuture());
      }
    });
    return this;
  }
}
