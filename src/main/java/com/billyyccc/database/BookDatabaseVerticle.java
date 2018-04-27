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

package com.billyyccc.database;


import io.reactiverse.pgclient.PgClient;
import io.reactiverse.pgclient.PgPool;
import io.reactiverse.pgclient.PgPoolOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.serviceproxy.ServiceBinder;

/**
 * @author Billy Yuan <billy112487983@gmail.com>
 */

public class BookDatabaseVerticle extends AbstractVerticle {
  private static final String CONFIG_PG_HOST = "postgresql.host";
  private static final String CONFIG_PG_PORT = "postgresql.port";
  private static final String CONFIG_PG_DATABASE = "postgresql.database";
  private static final String CONFIG_PG_USERNAME = "postgresql.username";
  private static final String CONFIG_PG_PASSWORD = "postgresql.password";
  private static final String CONFIG_PG_POOL_MAX_SIZE = "postgresql.pool.maxsize";

  private static final String CONFIG_DB_EB_QUEUE = "library.db.eb.address";

  private static final Logger LOGGER = LoggerFactory.getLogger(BookDatabaseVerticle.class);

  private PgPool pgPool;

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    PgPoolOptions pgPoolOptions = new PgPoolOptions()
      .setHost(config().getString(CONFIG_PG_HOST, "127.0.0.1"))
      .setPort(config().getInteger(CONFIG_PG_PORT, 5432))
      .setDatabase(config().getString(CONFIG_PG_DATABASE))
      .setUsername(config().getString(CONFIG_PG_USERNAME))
      .setPassword(config().getString(CONFIG_PG_PASSWORD))
      .setMaxSize(config().getInteger(CONFIG_PG_POOL_MAX_SIZE, 20));

    this.pgPool = PgClient.pool(vertx, pgPoolOptions);

    String databaseEbAddress = config().getString(CONFIG_DB_EB_QUEUE);

    BookDatabaseService.create(pgPool, result -> {
      if (result.succeeded()) {
        // register the database service
        new ServiceBinder(vertx)
          .setAddress(databaseEbAddress)
          .register(BookDatabaseService.class, result.result())
          .exceptionHandler(throwable -> {
            LOGGER.error("Failed to establish PostgreSQL database service", throwable);
            startFuture.fail(throwable);
          })
          .completionHandler(res -> {
            LOGGER.info("PostgreSQL database service is successfully established in \"" + databaseEbAddress + "\"");
            startFuture.complete();
          });
      } else {
        LOGGER.error("Failed to initiate the connection to database", result.cause());
        startFuture.fail(result.cause());
      }
    });
  }

  @Override
  public void stop() throws Exception {
    pgPool.close();
  }
}
