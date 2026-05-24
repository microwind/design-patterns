package com.github.microwind.springboot4ddd.domain.page;

import java.util.Objects;

/**
 * 排序条件（领域层），与 Spring Data 的 Sort.Order 解耦。
 *
 * @author jarry
 * @since 1.0.0
 */
public final class SortOrder {

    public enum Direction {
        ASC, DESC
    }

    private final String property;
    private final Direction direction;

    public SortOrder(String property, Direction direction) {
        this.property = Objects.requireNonNull(property, "property");
        this.direction = Objects.requireNonNull(direction, "direction");
    }

    public static SortOrder asc(String property) {
        return new SortOrder(property, Direction.ASC);
    }

    public static SortOrder desc(String property) {
        return new SortOrder(property, Direction.DESC);
    }

    public String getProperty() {
        return property;
    }

    public Direction getDirection() {
        return direction;
    }
}
