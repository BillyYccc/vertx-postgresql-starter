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

import com.billyyccc.entity.Book;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;

import java.time.LocalDate;
import java.util.Optional;

/**
 * This class is handler for getting all books or some books by conditions.
 *
 * @author Billy Yuan <billy112487983@gmail.com>
 */

public class GetBooksHandler implements Handler<RoutingContext> {
  @Override
  public void handle(RoutingContext routingContext) {
    // Get all the query parameters to an object
    Book book = new Book();
    book.setTitle(routingContext.queryParams().get("title"));
    book.setCategory(routingContext.queryParams().get("category"));
    book.setPublicationDate(routingContext.queryParams().get("publicationdate"));

    // TODO need database service
    // List<Book> booksList = dbService.getBooksByConditions(book);

    routingContext.response().setStatusCode(200)
      .putHeader("content-type", "application/json; charset=UTF-8")
      .end();
//    JsonArray jsonArray = new JsonArray();
  }
}
