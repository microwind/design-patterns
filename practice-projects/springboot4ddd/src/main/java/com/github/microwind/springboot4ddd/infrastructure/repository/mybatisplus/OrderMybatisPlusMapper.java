package com.github.microwind.springboot4ddd.infrastructure.repository.mybatisplus;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.microwind.springboot4ddd.domain.model.order.Order;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 订单MyBatis Plus Mapper接口
 * 继承 BaseMapper 使用默认的 CRUD 方法
 * 只定义自定义查询方法
 *
 * @author jarry
 * @since 1.0.0
 */
@Mapper
public interface OrderMybatisPlusMapper extends BaseMapper<Order> {

    /**
     * 根据ID查找订单（覆盖BaseMapper的selectById）
     */
    @Select("SELECT id, order_no, user_id, status, created_at, updated_at FROM orders WHERE id = #{id}")
    Order selectById(Long id);

    /**
     * 插入订单（覆盖BaseMapper的insert）
     */
    @Insert("INSERT INTO orders (order_no, user_id, status, created_at, updated_at) " +
            "VALUES (#{orderNo}, #{userId}, #{status}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Order order);

    /**
     * 更新订单（覆盖BaseMapper的updateById）
     */
    @Update("UPDATE orders SET order_no = #{orderNo}, user_id = #{userId}, " +
            "status = #{status}, updated_at = #{updatedAt} WHERE id = #{id}")
    int updateById(Order order);

    /**
     * 删除订单（覆盖BaseMapper的deleteById）
     */
    @Delete("DELETE FROM orders WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * 查询所有订单（覆盖BaseMapper的selectList）
     */
    @Select("SELECT id, order_no, user_id, status, created_at, updated_at FROM orders")
    List<Order> selectList(Object entity);

    /**
     * 查找所有订单
     */
    @Select("SELECT id, order_no, user_id, status, created_at, updated_at FROM orders")
    List<Order> findAllOrders();

    /**
     * 根据订单号查找
     */
    @Select("SELECT id, order_no, user_id, status, created_at, updated_at FROM orders WHERE order_no = #{orderNo}")
    Optional<Order> findByOrderNo(String orderNo);

    /**
     * 根据用户ID查找所有订单
     */
    @Select("SELECT id, order_no, user_id, status, created_at, updated_at FROM orders WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Order> findByUserId(Long userId);

    /**
     * 根据订单状态和创建时间查找订单
     */
    @Select("SELECT id, order_no, user_id, status, created_at, updated_at FROM orders WHERE status = #{status} AND created_at < #{createdAtBefore} ORDER BY created_at ASC")
    List<Order> findByStatusAndCreatedAtBefore(String status, LocalDateTime createdAtBefore);
}
