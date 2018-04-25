/*
 * MIT License
 *
 * Copyright (c) 2018 Billy Yuan
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

package com.billyyccc.api.handler;

import com.billyyccc.api.exception.BadRequestException;
import com.billyyccc.api.exception.ResourceNotFoundException;
import com.billyyccc.database.reactivex.BookDatabaseService;
import com.billyyccc.entity.Book;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;

import static com.billyyccc.api.utils.RestApiUtil.*;

/**
 * @author Billy Yuan
 */
public class BookApis {

  /**
   * This handler is for adding a new book.
   */
  public static Handler<RoutingContext> addBookHandler(BookDatabaseService bookDatabaseService) {
    return routingContext -> {
      Book book = decodeBodyToObject(routingContext, Book.class);

      bookDatabaseService.rxAddNewBook(book)
        .subscribe(
          () -> restResponse(routingContext, 200, book.toString()),
          throwable -> routingContext.fail(new BadRequestException(throwable)));
    };
  }

  /**
   * This handler is for deleting an existing book by id.
   */
  public static Handler<RoutingContext> deleteBookByIdHandler(BookDatabaseService bookDatabaseService) {
    return routingContext -> {
      int bookId = Integer.valueOf(routingContext.pathParam("id"));

      bookDatabaseService.rxDeleteBookById(bookId)
        .subscribe(
          () -> restResponse(routingContext, 200),
          throwable -> routingContext.fail(new BadRequestException(throwable)));
    };
  }

  /**
   * This handler is for getting an existing book by id.
   */
  public static Handler<RoutingContext> getBookByIdHandler(BookDatabaseService bookDatabaseService) {
    return routingContext -> {
      int bookId = Integer.valueOf(routingContext.pathParam("id"));

      bookDatabaseService.rxGetBookById(bookId)
        .subscribe(
          dbResponse -> {
            if (dbResponse.isEmpty()) {
              routingContext.fail(new ResourceNotFoundException("The book with id " + bookId + " can not be found"));
            } else {
              restResponse(routingContext, 200, dbResponse.toString());
            }
          },
          throwable -> routingContext.fail(new BadRequestException(throwable))
        );
    };
  }

  /**
   * This handler is for getting all books or some books by conditions.
   */
  public static Handler<RoutingContext> getBooksHandler(BookDatabaseService bookDatabaseService) {
    return routingContext -> {
      // Get all the query parameters to an object
      Book book = new Book();
      book.setTitle(routingContext.queryParams().get("title"));
      book.setCategory(routingContext.queryParams().get("category"));
      book.setPublicationDate(routingContext.queryParams().get("publicationDate"));

      bookDatabaseService.rxGetBooks(book)
        .subscribe(
          dbResponse -> {
            switch (dbResponse.size()) {
              case 0:
                routingContext.fail(new ResourceNotFoundException("The books have not been found"));
                break;
              case 1:
                restResponse(routingContext, 200, dbResponse.getJsonObject(0).toString());
                break;
              default:
                restResponse(routingContext, 200, dbResponse.toString());
                break;
            }
          },
          throwable -> routingContext.fail(new BadRequestException(throwable))
        );
    };
  }

  /**
   * This handler is for upserting a book by id.
   */
  public static Handler<RoutingContext> upsertBookByIdHandler(BookDatabaseService bookDatabaseService) {
    return routingContext -> {
      Book book = decodeBodyToObject(routingContext, Book.class);
      int bookId = Integer.valueOf(routingContext.pathParam("id"));

      bookDatabaseService.rxUpsertBookById(bookId, book)
        .subscribe(
          () -> restResponse(routingContext, 200, new JsonObject()
            .put("id", bookId)
            .put("title", book.getTitle())
            .put("category", book.getCategory())
            .put("publicationDate", book.getPublicationDate())
            .toString()),
          throwable -> routingContext.fail(new BadRequestException(throwable))
        );
    };
  }
}
