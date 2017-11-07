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

package com.billyyccc.http.handler;

import com.billyyccc.database.BookDatabaseService;
import com.billyyccc.entity.Book;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * This class is handler for getting all books or some books by conditions.
 *
 * @author Billy Yuan <billy112487983@gmail.com>
 */

public class GetBooksHandler implements Handler<RoutingContext> {
  private BookDatabaseService bookDatabaseService;

  public GetBooksHandler(BookDatabaseService bookDatabaseService) {
    this.bookDatabaseService = bookDatabaseService;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    //TODO need some Validation and error Handling
    // Get all the query parameters to an object
    Book book = new Book();
    book.setTitle(routingContext.queryParams().get("title"));
    book.setCategory(routingContext.queryParams().get("category"));
    book.setPublicationDate(routingContext.queryParams().get("publicationdate"));

    routingContext.response().putHeader("content-type", "application/json; charset=UTF-8");

    bookDatabaseService.getBooks(book, res -> {
      if (res.succeeded()) {
        switch (res.result().size()) {
          case 0:
            routingContext.response().setStatusCode(404)
              .end();
            break;
          case 1:
            routingContext.response().setStatusCode(200)
              .end(res.result().getJsonObject(0).toString());
            break;
          default:
            routingContext.response().setStatusCode(200)
              .end(res.result().toString());
            break;
        }
      } else {
        routingContext.response().setStatusCode(400)
          .end();
      }
    });
  }
}
