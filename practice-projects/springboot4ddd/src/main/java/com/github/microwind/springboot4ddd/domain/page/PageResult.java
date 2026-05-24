package com.github.microwind.springboot4ddd.domain.page;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 分页结果（领域层）。
 *
 * <p>不可变；零框架依赖；字段命名与 Spring Page 不完全一致，
 * 而是采用更直观的 {@code pageNumber / pageSize}。
 *
 * @author jarry
 * @since 1.0.0
 */
public final class PageResult<T> {

    private final List<T> content;
    private final long totalElements;
    private final int pageNumber;
    private final int pageSize;

    public PageResult(List<T> content, long totalElements, int pageNumber, int pageSize) {
        this.content = content == null ? Collections.emptyList() : List.copyOf(content);
        this.totalElements = Math.max(totalElements, 0);
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public static <T> PageResult<T> of(List<T> content, long totalElements, PageRequest pageRequest) {
        return new PageResult<>(content, totalElements, pageRequest.getPageNumber(), pageRequest.getPageSize());
    }

    public static <T> PageResult<T> empty(PageRequest pageRequest) {
        return new PageResult<>(Collections.emptyList(), 0, pageRequest.getPageNumber(), pageRequest.getPageSize());
    }

    public List<T> getContent() {
        return content;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalPages() {
        if (pageSize <= 0) {
            return 0;
        }
        return (int) ((totalElements + pageSize - 1) / pageSize);
    }

    public int getNumberOfElements() {
        return content.size();
    }

    public boolean isFirst() {
        return pageNumber == 1;
    }

    public boolean isLast() {
        return pageNumber >= getTotalPages();
    }

    public boolean isEmpty() {
        return content.isEmpty();
    }

    /**
     * 元素类型转换，保留分页元信息。
     */
    public <R> PageResult<R> map(Function<? super T, ? extends R> mapper) {
        List<R> mapped = content.stream().map(mapper).collect(Collectors.toList());
        return new PageResult<>(mapped, totalElements, pageNumber, pageSize);
    }
}
