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

package com.billyyccc.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * @author Billy Yuan <billy112487983@gmail.com>
 */

@DataObject(generateConverter = true)
@JsonPropertyOrder({"id", "title", "category", "publicationDate"})
public class Book {
  @JsonProperty("id")
  private int id;
  @JsonProperty("title")
  private String title;
  @JsonProperty("category")
  private String category;
  @JsonProperty("publicationDate")
  private String publicationDate;

  public Book() {
    //default constructor for jackson
  }

  public Book(Book other) {
    this.id = other.id;
    this.title = other.title;
    this.category = other.category;
    this.publicationDate = other.publicationDate;
  }

  public Book(JsonObject jsonObject) {
    BookConverter.fromJson(jsonObject, this);
  }

  public Book(int id, String title, String category, String publicationDate) {
    this.id = id;
    this.title = title;
    this.category = category;
    this.publicationDate = publicationDate;
  }

  public JsonObject toJson() {
    JsonObject jsonObject = new JsonObject();
    BookConverter.toJson(this, jsonObject);
    return jsonObject;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getPublicationDate() {
    return publicationDate;
  }

  public void setPublicationDate(String publicationDate) {
    this.publicationDate = publicationDate;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof Book)) return false;
    Book book = (Book) obj;
    return this.id == book.id &&
      (this.title != null ? this.title.equals(book.title) : book.title == null) &&
      (this.category != null ? this.category.equals(book.category) : book.category == null) &&
      (this.publicationDate != null ? this.publicationDate.equals(book.publicationDate) : book.publicationDate == null);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + id;
    result = 31 * result + (title != null ? title.hashCode() : 0);
    result = 31 * result + (category != null ? category.hashCode() : 0);
    result = 31 * result + (publicationDate != null ? publicationDate.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "{\n" +
      "    \"id\": " + this.id + ",\n" +
      "    \"title\": \"" + this.title + "\",\n" +
      "    \"category\": \"" + this.category + "\",\n" +
      "    \"publicationDate\": \"" + this.publicationDate + "\"\n" +
      "}";
  }
}
