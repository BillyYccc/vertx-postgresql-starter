package com.billyyccc.http.handler;

import com.billyyccc.database.BookDatabaseService;
import com.billyyccc.entity.Book;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

/**
 * @author Billy Yuan <billy112487983@gmail.com>
 */

public class UpdateBookHandler implements Handler<RoutingContext> {
  private BookDatabaseService bookDatabaseService;

  public UpdateBookHandler(BookDatabaseService bookDatabaseService) {
    this.bookDatabaseService = bookDatabaseService;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    //TODO need some Validation and error Handling
    Book book = Json.decodeValue(routingContext.getBodyAsString("UTF-8"), Book.class);
    int bookId = Integer.valueOf(routingContext.pathParam("bookid"));

    routingContext.response().putHeader("content-type", "application/json; charset=UTF-8");

    bookDatabaseService.upsertBookById(bookId, book, res -> {
      if (res.succeeded()) {
        routingContext.response().setStatusCode(200)
          .end(new JsonObject()
            .put("id",bookId)
            .put("title", book.getTitle())
            .put("category", book.getCategory())
            .put("publicationdate", book.getPublicationDate())
            .toString());
      } else {
        routingContext.response().setStatusCode(400)
          .end();
      }
    });
  }
}
