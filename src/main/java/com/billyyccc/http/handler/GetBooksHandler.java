package com.billyyccc.http.handler;

import com.billyyccc.entity.Book;
import io.vertx.core.Handler;
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
    Optional<String> publicationDate = Optional.ofNullable(routingContext.queryParams().get("publicationdate"));
    publicationDate.ifPresent(dateString -> book.setPublicationDate(LocalDate.parse(dateString)));

    // TODO need database service
    // List<Book> booksList = dbService.getBooksByConditions(book);

    routingContext.response().setStatusCode(200)
      .putHeader("content-type", "application/json; charset=UTF-8")
      .end();
  }
}
