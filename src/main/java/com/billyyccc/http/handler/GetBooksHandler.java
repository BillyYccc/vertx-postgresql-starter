package com.billyyccc.http.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * This class is handler for getting all books or some books by conditions.
 * @author Billy Yuan <billy112487983@gmail.com>
 */

public class GetBooksHandler implements Handler<RoutingContext> {
  @Override
  public void handle(RoutingContext routingContext) {
    //TODO need database service
    String authorName = routingContext.queryParams().get("authorname");
    routingContext.response()
                  .setStatusCode(200).putHeader("content-type", "application/json; charset=utf-8")
                  .end("[\n"
                       + "    {\n"
                       + "        \"bookid\": 1,\n"
                       + "        \"title\": \"Book 1\",\n"
                       + "        \"category\": \"science\",\n"
                       + "        \"publicationdate\": \"2016-01-01\",\n"
                       + "        \"authors\": [\n"
                       + "            \"Author1\",\n"
                       + "            \"Author2\"\n"
                       + "        ]\n"
                       + "    },\n"
                       + "    {\n"
                       + "        \"bookid\": 2,\n"
                       + "        \"title\": \"Book 2\",\n"
                       + "        \"category\": \"literature\",\n"
                       + "        \"publicationdate\": \"2017-02-01\",\n"
                       + "        \"authors\": \"Author3\"\n"
                       + "    }\n"
                       + "]");
  }
}
