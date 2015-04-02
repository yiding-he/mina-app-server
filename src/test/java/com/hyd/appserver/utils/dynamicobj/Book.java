package com.hyd.appserver.utils.dynamicobj;

import java.util.Arrays;

/**
 * (描述)
 *
 * @author 贺一丁
 */
public class Book {

    private String name;

    private String author;

    private Book[] references;

    public Book() {
    }

    public Book(String name, String author, Book[] references) {
        this.name = name;
        this.author = author;
        this.references = references;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Book[] getReferences() {
        return references;
    }

    public void setReferences(Book[] references) {
        this.references = references;
    }

    @Override
    public String toString() {
        return "Book{" +
                "name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", references=" + (references == null ? null : Arrays.asList(references)) +
                '}';
    }
}
