package com.github.microwind.springboot4ddd.interfaces.page;

import com.github.microwind.springboot4ddd.domain.page.PageRequest;
import com.github.microwind.springboot4ddd.domain.page.SortOrder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

/**
 * 协议层 ↔ 领域层 分页参数转换器。
 *
 * <p>Spring Pageable 用于自动解析 HTTP {@code ?page=&size=&sort=} 入参；
 * domain 层只接受零框架依赖的 {@link PageRequest}，转换由 interfaces 层完成。
 *
 * @author jarry
 * @since 1.0.0
 */
public final class PageableConverter {

    private PageableConverter() {
    }

    public static PageRequest toDomain(Pageable pageable) {
        List<SortOrder> sorts = new ArrayList<>();
        for (Sort.Order order : pageable.getSort()) {
            SortOrder.Direction direction = order.getDirection().isAscending()
                    ? SortOrder.Direction.ASC
                    : SortOrder.Direction.DESC;
            sorts.add(new SortOrder(order.getProperty(), direction));
        }
        return new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sorts);
    }
}
