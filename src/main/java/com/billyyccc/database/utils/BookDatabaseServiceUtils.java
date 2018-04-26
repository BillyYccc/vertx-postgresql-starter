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

package com.billyyccc.database.utils;

import com.billyyccc.entity.Book;
import io.reactiverse.reactivex.pgclient.Tuple;
import io.vertx.core.json.JsonObject;

import java.util.Optional;

/**
 * @author Billy Yuan <billy112487983@gmail.com>
 */

public class BookDatabaseServiceUtils {
  private static final String SQL_FIND_BOOKS_CONDITION_BY_TITLE = " AND title = $";
  private static final String SQL_FIND_BOOKS_CONDITION_BY_CATEGORY = " AND category = $";
  private static final String SQL_FIND_BOOKS_CONDITION_BY_PUBLICATION_DATE = " AND publication_date = $";

  public static JsonObject emptyJsonObject() {
    return new JsonObject();
  }

  // generate query with dynamic where clause in a manual way
  public static DynamicQuery generateDynamicQuery(String rawSql, Book book) {
    Optional<String> title = Optional.ofNullable(book.getTitle());
    Optional<String> category = Optional.ofNullable(book.getCategory());
    Optional<String> publicationDate = Optional.ofNullable(book.getPublicationDate());

    // Concat the SQL by conditions
    int count = 0;
    String dynamicSql = rawSql;
    Tuple params = Tuple.tuple();
    if (title.isPresent()) {
      count++;
      dynamicSql += SQL_FIND_BOOKS_CONDITION_BY_TITLE;
      dynamicSql += count;
      params.addString(title.get());
    }
    if (category.isPresent()) {
      count++;
      dynamicSql += SQL_FIND_BOOKS_CONDITION_BY_CATEGORY;
      dynamicSql += count;
      params.addString(category.get());
    }
    if (publicationDate.isPresent()) {
      count++;
      dynamicSql += SQL_FIND_BOOKS_CONDITION_BY_PUBLICATION_DATE;
      dynamicSql += count;
      params.addValue(publicationDate.get());
    }
    return new DynamicQuery(dynamicSql, params);
  }

  public static class DynamicQuery {
    private String preparedQuery;
    private Tuple params;

    public DynamicQuery(String preparedQuery, Tuple params) {
      this.preparedQuery = preparedQuery;
      this.params = params;
    }

    public String getPreparedQuery() {
      return preparedQuery;
    }

    public Tuple getParams() {
      return params;
    }
  }
}
