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
import io.reactivex.Completable;
import io.vertx.core.http.HttpMethod;
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
 * This test Class is to perform unit tests for AddBookHandler of books.
 *
 * @author Billy Yuan <billy112487983@gmail.com>
 */

@RunWith(VertxUnitRunner.class)
public class AddBookHandlerTest extends RestApiTestBase {
  @Before
  public void setUp(TestContext testContext) {
    BookDatabaseService mockBookDatabaseService = Mockito.mock(BookDatabaseService.class);

    Book book = new Book(3, "Design Patterns", "design", "1995-01-15");

    Mockito.when(mockBookDatabaseService.rxAddNewBook(book)).thenReturn(Completable.complete());

    mockServer(1234, POST, EndPoints.ADD_NEW_BOOK, BookApis.addBookHandler(mockBookDatabaseService), testContext);

    webClient = WebClient.create(vertx);
  }

  @Test
  public void restApiTest(TestContext testContext) {
    expectedResponseStatusCode = 200;

    JsonObject expectedResponseBody = new JsonObject()
      .put("id", 3)
      .put("title", "Design Patterns")
      .put("category", "design")
      .put("publicationDate", "1995-01-15");

    JsonObject requestBody = new JsonObject()
      .put("id", 3)
      .put("title", "Design Patterns")
      .put("category", "design")
      .put("publicationDate", "1995-01-15");

    webClient.request(HttpMethod.POST, 1234, "localhost", "/books")
      .putHeader("Content-Type", "application/json; charset=utf-8")
      .as(BodyCodec.jsonObject())
      .sendJsonObject(requestBody, testContext.asyncAssertSuccess(resp -> {
        testContext.assertEquals(expectedResponseStatusCode, resp.statusCode());
        testContext.assertEquals(expectedResponseBody, resp.body());
      }));
  }

}
