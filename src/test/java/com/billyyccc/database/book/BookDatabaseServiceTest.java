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

package com.billyyccc.database.book;

import com.billyyccc.database.BookDatabaseService;
import com.billyyccc.database.impl.BookDatabaseServiceImpl;
import com.billyyccc.entity.Book;
import com.julienviet.pgclient.PgClient;
import com.julienviet.pgclient.PgPool;
import com.julienviet.pgclient.PgPoolOptions;
import com.julienviet.pgclient.Row;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;

import java.time.LocalDate;

/**
 * This class is for testing {@link BookDatabaseServiceImpl} service implementation.
 *
 * @author Billy Yuan
 */

@RunWith(VertxUnitRunner.class)
public class BookDatabaseServiceTest {
  private static final String TEST_USER = "test";
  private static final String TEST_PASSWORD = "test-password";
  private static final String TEST_DB = "test-db";

  @ClassRule
  public static GenericContainer postgres = new GenericContainer("postgres:alpine")
    .withEnv("POSTGRES_USER", TEST_USER)
    .withEnv("POSTGRES_PASSWORD", TEST_PASSWORD)
    .withEnv("POSTGRES_DB", TEST_DB)
    .withClasspathResourceMapping("/init_testdb.sql", "/docker-entrypoint-initdb.d/v10init_testdb.sql", BindMode.READ_ONLY)
    .withExposedPorts(5432);

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  private Vertx vertx;
  private PgPool pgPool;
  private BookDatabaseService bookDatabaseService;

  @Before
  public void setup(TestContext testContext) {
    waitForContainer();
    int actualPort = postgres.getMappedPort(5432);
    vertx = rule.vertx();
    pgPool = PgClient.pool(vertx, new PgPoolOptions()
      .setPort(actualPort)
      .setUsername(TEST_USER)
      .setPassword(TEST_PASSWORD)
      .setDatabase(TEST_DB));
    bookDatabaseService = new BookDatabaseServiceImpl(pgPool, testContext.asyncAssertSuccess());
  }

  @After
  public void tearDown(TestContext testContext) {
    pgPool.close();
    vertx.close(testContext.asyncAssertSuccess());
  }

  @Test
  public void testAddNewBook(TestContext testContext) {
    Book book = new Book(3, "test", "test_category", "2000-01-01");

    bookDatabaseService.addNewBook(book, testContext.asyncAssertSuccess(res -> {
      pgPool.query("SELECT * FROM book WHERE id = 3", testContext.asyncAssertSuccess(pgResult -> {
        Row row = pgResult.iterator().next();
        checkBook(testContext, 3, "test", "test_category", LocalDate.of(2000, 1, 1), row);
      }));
    }));
  }

  @Test
  public void testGetBookById(TestContext testContext) {
    bookDatabaseService.getBookById(1, testContext.asyncAssertSuccess(res -> {
      JsonObject expectedJson = new JsonObject()
        .put("id", 1)
        .put("title", "Effective java")
        .put("category", "java")
        .put("publicationDate", "2009-01-01");
      testContext.assertEquals(expectedJson, res);
      pgPool.query("SELECT * FROM book WHERE id = 1", testContext.asyncAssertSuccess(pgResult -> {
        Row row = pgResult.iterator().next();
        checkBook(testContext, 1, "Effective java", "java", LocalDate.of(2009, 1, 1), row);
      }));
    }));

  }

  private void checkBook(TestContext testContext, int expectedId, String expectedTitle, String expectedCategory, LocalDate expectedPubDate, Row actualRow) {
    testContext.assertEquals(expectedId, actualRow.getInteger("id"));
    testContext.assertEquals(expectedTitle, actualRow.getString("title"));
    testContext.assertEquals(expectedCategory, actualRow.getString("category"));
    testContext.assertEquals(expectedPubDate, actualRow.getLocalDate("publication_date"));
  }

  // a tmp solution
  private void waitForContainer() {
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
