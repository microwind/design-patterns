package com.github.microwind.webdemo.model;

import java.time.LocalDateTime;

/**
 * 栏目实体类
 */
public class Column {
    private Long id;
    private String name;
    private String description;
    private Integer sort;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Column{id=" + id + ", name='" + name + "', description='" + description
               + "', sort=" + sort + ", createTime=" + createTime + "}";
    }
}
