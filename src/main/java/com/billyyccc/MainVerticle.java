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

import com.billyyccc.database.BookDatabaseVerticle;
import com.billyyccc.api.HttpServerVerticle;
import io.reactivex.Single;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.reactivex.core.AbstractVerticle;

/**
 * MainVerticle class to deploy the verticles needed.
 *
 * @author Billy Yuan <billy112487983@gmail.com>
 */

public class MainVerticle extends AbstractVerticle {
  private static final String HTTP_SERVER_VERTICLE_IDENTIFIER = HttpServerVerticle.class.getName();
  private static final String PG_DATABASE_VERTICLE_IDENTIFIER = BookDatabaseVerticle.class.getName();


  @Override
  public void start(Future<Void> startFuture) throws Exception {
    Single<String> dbVerticleDeployment = vertx.rxDeployVerticle(PG_DATABASE_VERTICLE_IDENTIFIER,
      new DeploymentOptions().setConfig(config().getJsonObject("postgresql.config")));

    dbVerticleDeployment
      .doOnError(startFuture::fail)
      .flatMap(deploymentId -> vertx.rxDeployVerticle(HTTP_SERVER_VERTICLE_IDENTIFIER,
        new DeploymentOptions().setConfig(config().getJsonObject("http.server.config"))))
      .subscribe(deploymentId -> startFuture.complete(),
        startFuture::fail);
  }
}

