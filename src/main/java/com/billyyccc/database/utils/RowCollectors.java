package com.billyyccc.database.utils;

import io.reactiverse.pgclient.Row;
import io.vertx.core.json.JsonArray;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Collectors for transforming SQL rows.
 */
public final class RowCollectors {
  /**
   * Build a collector for transforming rows to a {@link io.vertx.core.json.JsonArray} with specific types.
   *
   * @param rowMapper mapper to transform a row
   * @param <T>       the target type
   * @return the collector
   */
  public static <T> Collector<Row, ?, JsonArray> jsonArrayCollector(Function<Row, T> rowMapper) {
    return Collector.of(JsonArray::new,
      (jsonArray, row) -> jsonArray.add(rowMapper.apply(row)),
      (left, right) -> {
        left.addAll(right);
        return left;
      }, Collector.Characteristics.IDENTITY_FINISH);
  }

  /**
   * Build a collector for transforming rows to a {@link java.util.List} with specific types.
   *
   * @param rowMapper mapper to transform a row
   * @param <T>       the target type
   * @return the collector
   */
  public static <T> Collector<Row, ?, List<T>> listCollector(Function<Row, T> rowMapper) {
    return listCollector(rowMapper, ArrayList::new);
  }

  /**
   * Build a collector for transforming rows to a {@link java.util.List} with specific types.
   *
   * @param rowMapper    mapper to transform a row
   * @param listSupplier factory for the result list
   * @param <T>          the target type
   * @return the collector
   */
  public static <T> Collector<Row, ?, List<T>> listCollector(Function<Row, T> rowMapper, Supplier<List<T>> listSupplier) {
    return Collector.of(listSupplier,
      (list, row) -> list.add(rowMapper.apply(row)),
      (left, right) -> {
        left.addAll(right);
        return left;
      }, Collector.Characteristics.IDENTITY_FINISH);
  }

}
