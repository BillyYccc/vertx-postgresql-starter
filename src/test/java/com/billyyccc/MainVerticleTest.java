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

package com.billyyccc;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Billy Yuan <billy112487983@gmail.com>
 */

@RunWith(VertxUnitRunner.class)
public class MainVerticleTest {
  private Vertx vertx;

  @Before
  public void setUp(TestContext testContext) {
    vertx = Vertx.vertx();
    vertx.deployVerticle(MainVerticle.class.getName(), testContext.asyncAssertSuccess());
  }

  @After
  public void tearDown(TestContext testContext) {
    vertx.close(testContext.asyncAssertSuccess());
  }

  @Test
  public void testServer(TestContext testContext) {
    Async async = testContext.async();
    vertx.createHttpClient().getNow(8080, "localhost", "/books", res -> {
      testContext.assertEquals(res.statusCode(), 200);
      res.bodyHandler(body -> {
        testContext.assertTrue(body.length() >= 0);
        async.complete();
      });
    });
  }
}
