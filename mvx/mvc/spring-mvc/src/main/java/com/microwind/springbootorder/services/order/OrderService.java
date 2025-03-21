package com.microwind.springbootorder.services.order;

import com.microwind.springbootorder.models.order.Order;
import com.microwind.springbootorder.repository.order.OrderRepository;
import com.microwind.springbootorder.utils.NotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
  private final OrderRepository orderRepository;
  private final OrderMapper orderMapper;

  // 创建订单
  public Order createOrder(Order order) {
    // 业务逻辑，例如生成订单编号等
    if (order.getOrderNo() == null || order.getOrderNo().isEmpty()) {
      String orderNo = "ORD-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
      order.setOrderNo(orderNo);
    }
    if (order.getStatus() == null) {
      order.setStatus(Order.OrderStatus.CREATED);
    }
    return orderRepository.save(order);
  }

  // 根据订单编号查询订单
  public Order getByOrderNo(String orderNo) {
    return orderRepository.findByOrderNo(orderNo)
            .orElseThrow(() -> new NotFoundException("Order not found with orderNo: " + orderNo));
  }

  // 查询用户订单列表
  public List<Order> getUserOrders(Long userId) {
    return orderRepository.findByUserId(userId);
  }

  // 更新订单状态
  @Transactional
  public Order updateOrderStatus(String orderNo, Order.OrderStatus status) {
    // 1. 先取后存
    /*
     Order order = getByOrderNo(orderNo);
     order.setStatus(status);
     return orderRepository.save(order);
     */

    // 2. 更新状态后再查询，需要注意事务的缓存问题
    int updatedRows = orderRepository.updateOrderStatus(orderNo, status);
    if (updatedRows > 0) {
      return getByOrderNo(orderNo);
    }
    throw new EntityNotFoundException("Order update failed, order not found: " + orderNo);
  }


  // 更新订单
  public Order updateOrder(String orderNo, Order order) {
    Order existingOrder = orderRepository.findByOrderNo(orderNo)
            .orElseThrow(() -> new NotFoundException("Order not found with orderNo: " + orderNo));
    if (order.getUserId()!= null) {
      existingOrder.setUserId(order.getUserId());
    }
    if (order.getAmount()!= null) {
      existingOrder.setAmount(order.getAmount());
    }
    if (order.getStatus()!= null) {
      existingOrder.setStatus(order.getStatus());
    }
    if (order.getOrderName()!= null) {
      existingOrder.setOrderName(order.getOrderName());
    }
    return orderRepository.save(existingOrder);
  }

  // 删除订单
  public void deleteOrder(String orderNo) {
    Order order = orderRepository.findByOrderNo(orderNo)
            .orElseThrow(() -> new NotFoundException("Order not found with orderNo: " + orderNo));
    orderRepository.delete(order);
  }

  // 获取所有订单
  public Page<Order> getAllOrders(Pageable pageable) {
    // 调用默认JPA方法，简单
//    return orderRepository.findAll(pageable);
    // 调用自定义方法，个性化
    return orderRepository.findAllOrders(pageable);
  }
}
