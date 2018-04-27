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

import io.reactiverse.pgclient.PgResult;
import io.reactiverse.pgclient.Row;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * A helper to transform {@code PgResult} to {@code JsonArray}.
 * Customize your handling for unsupported types of <b>Json</b> in method {@link PgResultTransformer#processType}
 *
 * @author Billy Yuan
 */
public class PgResultTransformer {
  public static JsonArray toJsonArray(PgResult<Row> pgResult) {
    JsonArray jsonArray = new JsonArray();
    List<String> columnNames = pgResult.columnsNames();

    pgResult.forEach(row -> {
      JsonObject currentRow = new JsonObject();
      for (String columnName : columnNames) {
        Object value = row.getValue(columnName);
        if (value == null) {
          continue;
        }
        value = processType(value);
        String key = coordinateKeys(columnName);
        currentRow.put(key, value);
      }
      jsonArray.add(currentRow);
    });
    return jsonArray;
  }

  /**
   * Customize your ways of handling types in JSON.
   */
  private static Object processType(Object value) {
    if (value instanceof LocalDate) {
      return processLocalDate((LocalDate) value);
    } else if (value instanceof LocalDateTime) {
      return processLocalDateTime((LocalDateTime) value);
    } else if (value instanceof UUID) {
      return processUUID((UUID) value);
    } else {
      // Make sure the type is supported by JSON if you don't handle it
      return value;
    }
  }

  private static String processLocalDate(LocalDate localDate) {
    return localDate.format(DateTimeFormatter.ISO_DATE);
  }

  private static String processLocalDateTime(LocalDateTime localDateTime) {
    return localDateTime.format(DateTimeFormatter.ISO_DATE_TIME);
  }

  private static String processUUID(UUID uuid) {
    return uuid.toString();
  }

  /**
   * Make the Json converted from row-column data unified with the Json defined in REST APIs.
   */
  private static String coordinateKeys(String columnName) {
    if ("publication_date".equals(columnName)) {
      return "publicationDate";
    }
    return columnName;
  }
}

