package com.github.microwind.springboot4ddd.infrastructure.repository.order;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单数据对象（Data Object）
 *
 * <p>仅承担"与 orders 表字段一对一映射"的职责，所有 Spring Data JDBC / MyBatis-Plus
 * 注解都集中在这里。领域模型 {@code Order} 不再背负持久化细节。
 *
 * <p>转换由 {@link OrderConverter} 显式完成。
 *
 * @author jarry
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("orders")
@TableName("orders")
public class OrderDO {

    @Id
    @TableId
    private Long id;

    @Column("order_no")
    private String orderNo;

    @Column("user_id")
    private Long userId;

    @Column("total_amount")
    private BigDecimal totalAmount;

    private String status;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}
