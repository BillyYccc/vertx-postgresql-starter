package com.billyyccc.http.handler;

import com.billyyccc.database.reactivex.BookDatabaseService;
import com.billyyccc.entity.Book;
import com.billyyccc.http.exception.BadRequestException;
import io.vertx.core.Handler;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;

import static com.billyyccc.http.utils.RestApiUtil.*;

/**
 * This class is handler for updating an existed book.
 * If the book does not exist, then create a new one.
 *
 * @author Billy Yuan <billy112487983@gmail.com>
 */

public class UpsertBookByIdHandler implements Handler<RoutingContext> {
  private BookDatabaseService bookDatabaseService;

  public UpsertBookByIdHandler(BookDatabaseService bookDatabaseService) {
    this.bookDatabaseService = bookDatabaseService;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    try {
      Book book = Json.decodeValue(routingContext.getBodyAsString("UTF-8"), Book.class);
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
    } catch (DecodeException exception) {
      routingContext.fail(exception);
      exception.printStackTrace();
    }

  }
}
