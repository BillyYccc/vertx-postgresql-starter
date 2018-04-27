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

package com.billyyccc.api.book;

import com.billyyccc.api.EndPoints;
import com.billyyccc.api.RestApiTestBase;
import com.billyyccc.api.handler.BookApis;
import com.billyyccc.database.reactivex.BookDatabaseService;
import com.billyyccc.entity.Book;
import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.codec.BodyCodec;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static io.vertx.core.http.HttpMethod.*;

/**
 * This test Class is to perform unit tests for GetBooksHandler of books.
 *
 * @author Billy Yuan <billy112487983@gmail.com>
 */

@RunWith(VertxUnitRunner.class)
public class GetBooksHandlerTest extends RestApiTestBase {
  @Before
  public void setUp(TestContext testContext) {
    BookDatabaseService mockBookDatabaseService = Mockito.mock(BookDatabaseService.class);

    JsonArray mockDbResponse = new JsonArray()
      .add(new JsonObject()
        .put("id", 1)
        .put("title", "Effective Java")
        .put("category", "java")
        .put("publicationDate", "2009-01-01"))
      .add(new JsonObject()
        .put("id", 2)
        .put("title", "Thinking in Java")
        .put("category", "java")
        .put("publicationDate", "2006-02-20"));


    Mockito.when(mockBookDatabaseService.rxGetBooks(new Book())).thenReturn(Single.just(mockDbResponse));

    mockServer(1234, GET, EndPoints.GET_BOOKS, BookApis.getBooksHandler(mockBookDatabaseService), testContext);

    webClient = WebClient.create(vertx);
  }

  @Test
  public void restApiTest(TestContext testContext) {
    expectedResponseStatusCode = 200;

    JsonArray expectedResponseBody = new JsonArray()
      .add(new JsonObject()
        .put("id", 1)
        .put("title", "Effective Java")
        .put("category", "java")
        .put("publicationDate", "2009-01-01"))
      .add(new JsonObject()
        .put("id", 2)
        .put("title", "Thinking in Java")
        .put("category", "java")
        .put("publicationDate", "2006-02-20"));

    webClient.request(GET, 1234, "localhost", "/books")
      .putHeader("Content-Type", "application/json; charset=utf-8")
      .as(BodyCodec.jsonArray())
      .send(testContext.asyncAssertSuccess(resp -> {
        testContext.assertEquals(expectedResponseStatusCode, resp.statusCode());
        testContext.assertEquals(expectedResponseBody, resp.body());
      }));
  }

}
