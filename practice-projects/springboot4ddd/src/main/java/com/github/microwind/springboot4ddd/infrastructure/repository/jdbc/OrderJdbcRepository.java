package com.github.microwind.springboot4ddd.infrastructure.repository.jdbc;

import com.github.microwind.springboot4ddd.infrastructure.repository.order.OrderDO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 订单 Spring Data JDBC Mapper 接口
 *
 * <p>持久化层只与 {@link OrderDO} 打交道，不引用领域模型 {@code Order}。
 *
 * @author jarry
 * @since 1.0.0
 */
@Repository("orderJdbcRepository")
public interface OrderJdbcRepository extends CrudRepository<OrderDO, Long>, PagingAndSortingRepository<OrderDO, Long> {

    @Query("SELECT * FROM orders WHERE order_no = :orderNo")
    Optional<OrderDO> findByOrderNo(String orderNo);

    @Query("SELECT * FROM orders WHERE user_id = :userId ORDER BY created_at DESC")
    List<OrderDO> findByUserId(Long userId);

    Page<OrderDO> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT * FROM orders WHERE status = :status AND created_at < :createdAtBefore ORDER BY created_at ASC")
    List<OrderDO> findByStatusAndCreatedAtBefore(String status, LocalDateTime createdAtBefore);
}
