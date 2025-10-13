package com.github.microwind.webdemo.model;

import java.time.LocalDateTime;

/**
 * 文章实体类
 */
public class Article {
    private Long id;
    private String title;
    private String content;
    private Long columnId;
    private String author;
    private LocalDateTime publishTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LocalDateTime getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(LocalDateTime publishTime) {
        this.publishTime = publishTime;
    }

    @Override
    public String toString() {
        return "Article{id=" + id + ", title='" + title + "', author='" + author
               + "', columnId=" + columnId + ", publishTime=" + publishTime + "}";
    }
}
