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
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * This class is handler for deleting a existing book.
 *
 * @author Billy Yuan <billy112487983@gmail.com>
 */

public class DeleteBookHandler implements Handler<RoutingContext> {
  private BookDatabaseService bookDatabaseService;

  public DeleteBookHandler(BookDatabaseService bookDatabaseService) {
    this.bookDatabaseService = bookDatabaseService;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    //TODO need some Validation and error Handling
    int bookId = Integer.valueOf(routingContext.pathParam("bookid"));

    routingContext.response().putHeader("content-type", "application/json; charset=UTF-8");

    bookDatabaseService.deleteBookById(bookId, res -> {
      if (res.succeeded()) {
        routingContext.response().setStatusCode(200)
          .end();
      } else {
        routingContext.response().setStatusCode(400)
          .end();
      }
    });
  }
}
