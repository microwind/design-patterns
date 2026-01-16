package com.github.microwind.userdemo.utils;

import java.util.List;

/**
 * 分页结果封装类
 * 用于统一封装分页数据，便于扩展和维护
 *
 * @param <T> 数据类型
 */
public class PageResult<T> {
    /**
     * 数据列表
     */
    private List<T> list;

    /**
     * 当前页码（从1开始）
     */
    private int page = 1;

    /**
     * 每页大小
     */
    private int pageSize = 10;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 总页数
     */
    private int totalPages;

    /**
     * 是否有上一页
     */
    private boolean hasPrevious;

    /**
     * 是否有下一页
     */
    private boolean hasNext;

    /**
     * 是否是第一页
     */
    private boolean isFirst;

    /**
     * 是否是最后一页
     */
    private boolean isLast;

    // 私有构造函数，强制使用 Builder
    private PageResult() {
    }

    /**
     * 创建分页结果的便捷方法
     *
     * @param list     数据列表
     * @param page     当前页码
     * @param pageSize 每页大小
     * @param total    总记录数
     * @param <T>      数据类型
     * @return 分页结果对象
     */
    public static <T> PageResult<T> of(List<T> list, int page, int pageSize, long total) {
        PageResult<T> result = new PageResult<>();
        result.list = list;
        result.page = page;
        result.pageSize = pageSize;
        result.total = total;

        // 计算总页数
        result.totalPages = (int) Math.ceil((double) total / pageSize);

        // 计算分页状态
        result.hasPrevious = page > 1;
        result.hasNext = page < result.totalPages;
        result.isFirst = page == 1;
        result.isLast = page == result.totalPages || result.totalPages == 0;

        return result;
    }

    /**
     * 创建空的分页结果
     *
     * @param page     当前页码
     * @param pageSize 每页大小
     * @param <T>      数据类型
     * @return 空的分页结果对象
     */
    public static <T> PageResult<T> empty(int page, int pageSize) {
        return of(java.util.Collections.emptyList(), page, pageSize, 0L);
    }

    // -------------------- Getters --------------------

    public List<T> getList() {
        return list;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getTotal() {
        return total;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public boolean isLast() {
        return isLast;
    }

    /**
     * 获取下一页页码
     */
    public int getNextPage() {
        return hasNext ? page + 1 : page;
    }

    /**
     * 获取上一页页码
     */
    public int getPreviousPage() {
        return hasPrevious ? page - 1 : page;
    }

    /**
     * 获取当前页的起始记录索引（从0开始）
     */
    public int getStartIndex() {
        return (page - 1) * pageSize;
    }

    /**
     * 获取当前页的结束记录索引（从0开始）
     */
    public int getEndIndex() {
        int end = page * pageSize - 1;
        return (int) Math.min(end, total - 1);
    }

    @Override
    public String toString() {
        return "PageResult{" +
                "page=" + page +
                ", pageSize=" + pageSize +
                ", total=" + total +
                ", totalPages=" + totalPages +
                ", listSize=" + (list != null ? list.size() : 0) +
                ", hasPrevious=" + hasPrevious +
                ", hasNext=" + hasNext +
                '}';
    }
}
