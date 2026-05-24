package com.github.microwind.springboot4ddd.infrastructure.page;

import com.github.microwind.springboot4ddd.domain.page.PageRequest;
import com.github.microwind.springboot4ddd.domain.page.PageResult;
import com.github.microwind.springboot4ddd.domain.page.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 领域分页类型 ↔ Spring Data 分页类型 的显式转换器。
 *
 * <p>仅供 infrastructure 内部使用，domain / application / interfaces 都不应引用 Spring 的 Page/Pageable。
 *
 * @author jarry
 * @since 1.0.0
 */
public final class PageMapper {

    private PageMapper() {
    }

    /**
     * domain PageRequest → Spring Data Pageable。
     * domain PageRequest 的 pageNumber 是 1-based，Spring Data Pageable 是 0-based，所以这里 -1。
     */
    public static Pageable toSpring(PageRequest pageRequest) {
        Sort sort = toSpringSort(pageRequest.getSorts());
        return org.springframework.data.domain.PageRequest.of(
                pageRequest.getPageNumber() - 1,
                pageRequest.getPageSize(),
                sort
        );
    }

    public static Sort toSpringSort(List<SortOrder> sorts) {
        if (sorts == null || sorts.isEmpty()) {
            return Sort.unsorted();
        }
        List<Sort.Order> orders = sorts.stream()
                .map(s -> s.getDirection() == SortOrder.Direction.ASC
                        ? Sort.Order.asc(s.getProperty())
                        : Sort.Order.desc(s.getProperty()))
                .collect(Collectors.toList());
        return Sort.by(orders);
    }

    /**
     * Spring Data Page → domain PageResult。pageNumber 取回 1-based。
     */
    public static <T> PageResult<T> toDomain(Page<T> page, PageRequest pageRequest) {
        return new PageResult<>(
                page.getContent(),
                page.getTotalElements(),
                pageRequest.getPageNumber(),
                pageRequest.getPageSize()
        );
    }
}
