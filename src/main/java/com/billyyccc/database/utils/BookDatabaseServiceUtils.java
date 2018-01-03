package com.billyyccc.database.utils;

import com.billyyccc.entity.Book;
import com.julienviet.pgclient.PgIterator;
import com.julienviet.pgclient.Row;
import com.julienviet.reactivex.pgclient.Tuple;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * @author Billy Yuan <billy112487983@gmail.com>
 */

public class BookDatabaseServiceUtils {
  private static final String SQL_FIND_BOOKS_CONDITION_BY_TITLE = " AND title = $";
  private static final String SQL_FIND_BOOKS_CONDITION_BY_CATEGORY = " AND category = $";
  private static final String SQL_FIND_BOOKS_CONDITION_BY_PUBLICATION_DATE = " AND publication_date = $";

  public static JsonArray transformPgResultToJson(com.julienviet.pgclient.PgResult<String> pgResult) {
    PgIterator pgIterator = pgResult.iterator();
    JsonArray jsonArray = new JsonArray();
    List<String> columnName = pgResult.columnsNames();
    while (pgIterator.hasNext()) {
      JsonObject row = new JsonObject();
      Row rowValue = (Row) pgIterator.next();
      Optional<String> title = Optional.ofNullable(rowValue.getString(1));
      Optional<String> category = Optional.ofNullable(rowValue.getString(2));
      Optional<LocalDate> publicationDate = Optional.ofNullable(rowValue.getLocalDate(3));
      row.put(columnName.get(0), rowValue.getInteger(0));
      title.ifPresent(v -> row.put(columnName.get(1), v));
      category.ifPresent(v -> row.put(columnName.get(2), v));
      publicationDate.ifPresent(v -> row.put(columnName.get(3), v.format(DateTimeFormatter.ISO_LOCAL_DATE)));
      jsonArray.add(row);
    }
    return jsonArray;
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
