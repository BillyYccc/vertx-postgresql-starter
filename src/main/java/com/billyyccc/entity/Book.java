package com.billyyccc.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import java.time.LocalDate;

/**
 * @author Billy Yuan <billy112487983@gmail.com>
 */

@JsonPropertyOrder({"bookid", "title", "category", "publicationDate"})
public class Book {
  @JsonProperty("bookid")
  private int bookId;
  @JsonProperty("title")
  private String title;
  @JsonProperty("category")
  private String category;
  @JsonProperty("publicationdate")
  @JsonFormat(pattern = "yyyy-MM-dd")
  @JsonSerialize(using = LocalDateSerializer.class)
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate publicationDate;

  public Book() {
    //default constructor for jackson
  }

  public Book(int bookId, String title, String category, LocalDate publicationDate) {
    this.bookId = bookId;
    this.title = title;
    this.category = category;
    this.publicationDate = publicationDate;
  }

  public int getBookId() {
    return bookId;
  }

  public void setBookId(int bookId) {
    this.bookId = bookId;
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


  public LocalDate getPublicationDate() {
    return publicationDate;
  }

  public void setPublicationDate(LocalDate publicationDate) {
    this.publicationDate = publicationDate;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Book)) return false;
    Book book = (Book) obj;
    return this.bookId == book.bookId &&
      this.title.equals(book.title) &&
      this.category.equals(book.category) &&
      this.publicationDate.equals(book.publicationDate);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + bookId;
    result = 31 * result + (title != null ? title.hashCode() : 0);
    result = 31 * result + (category != null ? category.hashCode() : 0);
    result = 31 * result + (publicationDate != null ? publicationDate.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "{\n" +
      "    \"bookid\": " + this.bookId + ",\n" +
      "    \"title\": \"" + this.title + "\",\n" +
      "    \"category\": \"" + this.category + "\",\n" +
      "    \"publicationdate\": \"" + this.publicationDate + "\"\n" +
      "}";
  }
}
