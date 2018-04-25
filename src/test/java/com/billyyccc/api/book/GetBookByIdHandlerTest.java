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
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Router;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static io.vertx.core.http.HttpMethod.*;

/**
 * This test Class is to perform unit tests for GetBookByIdHandler of books.
 *
 * @author Billy Yuan <billy112487983@gmail.com>
 */

@RunWith(VertxUnitRunner.class)
public class GetBookByIdHandlerTest extends RestApiTestBase {
  @Before
  public void setUp(TestContext testContext) {
    vertx = new Vertx(rule.vertx());
    router = Router.router(vertx);

    BookDatabaseService mockBookDatabaseService = Mockito.mock(BookDatabaseService.class);

    JsonObject mockDbResponse = new JsonObject()
      .put("id", 1)
      .put("title", "Effective Java")
      .put("category", "java")
      .put("publicationDate", "2009-01-01");


    Mockito.when(mockBookDatabaseService.rxGetBookById(1)).thenReturn(Single.just(mockDbResponse));

    mockServer(GET, EndPoints.GET_BOOK_BY_ID, BookApis.getBookByIdHandler(mockBookDatabaseService), testContext);
  }

  @Test
  public void restApiTest(TestContext testContext) {
    Async async = testContext.async();
    vertx.createHttpClient().getNow(1234, "localhost", "/books/1", res -> {
      testContext.assertEquals(200, res.statusCode());
      res.bodyHandler(body -> {
        testContext.assertTrue(body.length() > 0);

        JsonObject expectedResponseBody = new JsonObject()
          .put("id", 1)
          .put("title", "Effective Java")
          .put("category", "java")
          .put("publicationDate", "2009-01-01");

        testContext.assertEquals(expectedResponseBody, body.toJsonObject());
        async.complete();
      });
    });
  }

}
