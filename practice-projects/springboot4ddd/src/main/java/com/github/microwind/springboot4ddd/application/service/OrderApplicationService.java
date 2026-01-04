package com.github.microwind.springboot4ddd.application.service;

import com.github.microwind.springboot4ddd.application.dto.OrderDTO;
import com.github.microwind.springboot4ddd.application.dto.OrderMapper;
import com.github.microwind.springboot4ddd.domain.model.Order;
import com.github.microwind.springboot4ddd.domain.repository.OrderRepository;
import com.github.microwind.springboot4ddd.interfaces.vo.CreateOrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 订单应用服务
 * 协调领域对象完成业务用例，负责事务管理
 *
 * @author jarry
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderApplicationService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    /**
     * 创建订单
     *
     * @param request 创建订单请求
     * @return 订单DTO
     */
    @Transactional
    public OrderDTO createOrder(CreateOrderRequest request) {
        log.info("创建订单，userId={}, totalAmount={}", request.getUserId(), request.getTotalAmount());

        // 使用领域模型创建订单
        Order order = Order.create(request.getUserId(), request.getTotalAmount());

        // 持久化订单
        Order savedOrder = orderRepository.save(order);

        log.info("订单创建成功，orderNo={}", savedOrder.getOrderNo());
        return orderMapper.toDTO(savedOrder);
    }

    /**
     * 获取订单详情
     *
     * @param id 订单ID
     * @return 订单DTO
     */
    public OrderDTO getOrder(Long id) {
        log.info("查询订单详情，id={}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在，id=" + id));

        return orderMapper.toDTO(order);
    }

    /**
     * 根据订单号获取订单
     *
     * @param orderNo 订单号
     * @return 订单DTO
     */
    public OrderDTO getOrderByNo(String orderNo) {
        log.info("根据订单号查询订单，orderNo={}", orderNo);

        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在，orderNo=" + orderNo));

        return orderMapper.toDTO(order);
    }

    /**
     * 获取用户订单列表
     *
     * @param userId 用户ID
     * @return 订单DTO列表
     */
    public List<OrderDTO> getUserOrders(Long userId) {
        log.info("查询用户订单列表，userId={}", userId);

        List<Order> orders = orderRepository.findByUserId(userId);
        return orderMapper.toDTOList(orders);
    }

    /**
     * 获取所有订单
     *
     * @return 订单DTO列表
     */
    public List<OrderDTO> getAllOrders() {
        log.info("查询所有订单");

        List<Order> orders = orderRepository.findAll();
        return orderMapper.toDTOList(orders);
    }

    /**
     * 取消订单
     *
     * @param id 订单ID
     * @return 订单DTO
     */
    @Transactional
    public OrderDTO cancelOrder(Long id) {
        log.info("取消订单，id={}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在，id=" + id));

        // 调用领域模型的取消方法
        order.cancel();

        // 持久化订单
        Order updatedOrder = orderRepository.save(order);

        log.info("订单取消成功，orderNo={}", updatedOrder.getOrderNo());
        return orderMapper.toDTO(updatedOrder);
    }

    /**
     * 支付订单
     *
     * @param id 订单ID
     * @return 订单DTO
     */
    @Transactional
    public OrderDTO payOrder(Long id) {
        log.info("支付订单，id={}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在，id=" + id));

        // 调用领域模型的支付方法
        order.pay();

        // 持久化订单
        Order updatedOrder = orderRepository.save(order);

        log.info("订单支付成功，orderNo={}", updatedOrder.getOrderNo());
        return orderMapper.toDTO(updatedOrder);
    }

    /**
     * 完成订单
     *
     * @param id 订单ID
     * @return 订单DTO
     */
    @Transactional
    public OrderDTO completeOrder(Long id) {
        log.info("完成订单，id={}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在，id=" + id));

        // 调用领域模型的完成方法
        order.complete();

        // 持久化订单
        Order updatedOrder = orderRepository.save(order);

        log.info("订单完成，orderNo={}", updatedOrder.getOrderNo());
        return orderMapper.toDTO(updatedOrder);
    }

    /**
     * 删除订单
     *
     * @param id 订单ID
     */
    @Transactional
    public void deleteOrder(Long id) {
        log.info("删除订单，id={}", id);

        if (!orderRepository.findById(id).isPresent()) {
            throw new IllegalArgumentException("订单不存在，id=" + id);
        }

        orderRepository.deleteById(id);
        log.info("订单删除成功，id={}", id);
    }
}
