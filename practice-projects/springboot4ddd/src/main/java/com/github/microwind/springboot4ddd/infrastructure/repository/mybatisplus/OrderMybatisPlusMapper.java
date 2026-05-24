package com.github.microwind.springboot4ddd.infrastructure.repository.mybatisplus;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.microwind.springboot4ddd.infrastructure.repository.order.OrderDO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 订单 MyBatis Plus Mapper 接口
 *
 * <p>持久化层只与 {@link OrderDO} 打交道，不引用领域模型 {@code Order}。
 *
 * @author jarry
 * @since 1.0.0
 */
@Mapper
public interface OrderMybatisPlusMapper extends BaseMapper<OrderDO> {

    @Select("SELECT id, order_no, user_id, total_amount, status, created_at, updated_at " +
            "FROM orders WHERE id = #{id}")
    OrderDO selectById(Long id);

    @Insert("INSERT INTO orders (order_no, user_id, total_amount, status, created_at, updated_at) " +
            "VALUES (#{orderNo}, #{userId}, #{totalAmount}, #{status}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OrderDO orderDO);

    @Update("UPDATE orders SET order_no = #{orderNo}, user_id = #{userId}, " +
            "total_amount = #{totalAmount}, status = #{status}, updated_at = #{updatedAt} " +
            "WHERE id = #{id}")
    int updateById(OrderDO orderDO);

    @Delete("DELETE FROM orders WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT id, order_no, user_id, total_amount, status, created_at, updated_at FROM orders")
    List<OrderDO> selectList(Object entity);

    @Select("SELECT id, order_no, user_id, total_amount, status, created_at, updated_at " +
            "FROM orders LIMIT #{limit} OFFSET #{offset}")
    List<OrderDO> selectPageData(@Param("offset") long offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM orders")
    long countAll();

    @Select("SELECT id, order_no, user_id, total_amount, status, created_at, updated_at " +
            "FROM orders WHERE order_no = #{orderNo}")
    Optional<OrderDO> findByOrderNo(String orderNo);

    @Select("SELECT id, order_no, user_id, total_amount, status, created_at, updated_at " +
            "FROM orders WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<OrderDO> findByUserId(Long userId);

    @Select("SELECT id, order_no, user_id, total_amount, status, created_at, updated_at " +
            "FROM orders WHERE status = #{status} AND created_at < #{createdAtBefore} " +
            "ORDER BY created_at ASC")
    List<OrderDO> findByStatusAndCreatedAtBefore(String status, LocalDateTime createdAtBefore);
}
