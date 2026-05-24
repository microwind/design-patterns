package com.github.microwind.springboot4ddd.domain.page;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 分页请求（领域层）。
 *
 * <p>{@code pageNumber} 从 1 开始计数，符合本仓库代码与对外契约的既有约定。
 * 零框架依赖，作为 application ↔ domain ↔ infrastructure 的分页边界类型。
 *
 * @author jarry
 * @since 1.0.0
 */
public final class PageRequest {

    private final int pageNumber;
    private final int pageSize;
    private final List<SortOrder> sorts;

    public PageRequest(int pageNumber, int pageSize, List<SortOrder> sorts) {
        if (pageNumber < 1) {
            throw new IllegalArgumentException("pageNumber 必须从 1 开始，当前值: " + pageNumber);
        }
        if (pageSize <= 0) {
            throw new IllegalArgumentException("pageSize 必须大于 0，当前值: " + pageSize);
        }
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.sorts = sorts == null ? Collections.emptyList() : List.copyOf(sorts);
    }

    public static PageRequest of(int pageNumber, int pageSize) {
        return new PageRequest(pageNumber, pageSize, Collections.emptyList());
    }

    public static PageRequest of(int pageNumber, int pageSize, List<SortOrder> sorts) {
        return new PageRequest(pageNumber, pageSize, sorts);
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    /** 数据库 OFFSET 值：基于 1-based pageNumber 计算 */
    public long getOffset() {
        return (long) (pageNumber - 1) * pageSize;
    }

    public List<SortOrder> getSorts() {
        return sorts;
    }

    public boolean hasSort() {
        return !sorts.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PageRequest)) return false;
        PageRequest that = (PageRequest) o;
        return pageNumber == that.pageNumber
                && pageSize == that.pageSize
                && Objects.equals(sorts, that.sorts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pageNumber, pageSize, sorts);
    }
}
